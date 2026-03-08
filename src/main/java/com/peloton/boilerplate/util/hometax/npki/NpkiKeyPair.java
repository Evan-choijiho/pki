package com.peloton.boilerplate.util.hometax.npki;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * NPKI 디렉터리에서 인증서(signCert.der)와 비밀키(signPri.key) 로드,
 * npkiRandomNum 추출 (OID 1.2.410.200004.10.1.1.3).
 */
public final class NpkiKeyPair {

    private static final String OID_NPKI_RANDOM_NUM = "1.2.410.200004.10.1.1.3";

    private final X509Certificate certificate;
    private final PrivateKey privateKey;
    private final byte[] npkiRandomNum;

    public NpkiKeyPair(X509Certificate certificate, PrivateKey privateKey, byte[] npkiRandomNum) {
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.npkiRandomNum = npkiRandomNum != null ? npkiRandomNum : new byte[0];
    }

    public X509Certificate getCertificate() { return certificate; }
    public PrivateKey getPrivateKey() { return privateKey; }
    public byte[] getNpkiRandomNum() { return npkiRandomNum; }

    public RSAPrivateKey getRsaPrivateKey() {
        if (privateKey instanceof RSAPrivateKey)
            return (RSAPrivateKey) privateKey;
        throw new IllegalStateException("Private key is not RSA");
    }

    /**
     * NPKI 디렉터리 경로와 비밀번호로 키쌍 로드.
     * signCert.der, signPri.key 사용.
     */
    public static NpkiKeyPair load(Path certDir, char[] password) throws IOException, GeneralSecurityException {
        Path certFile = certDir.resolve("signCert.der");
        Path keyFile = certDir.resolve("signPri.key");
        if (!Files.exists(certFile) || !Files.exists(keyFile))
            throw new IOException("signCert.der or signPri.key not found in " + certDir);

        byte[] certDer = Files.readAllBytes(certFile);
        byte[] keyBytes = Files.readAllBytes(keyFile);

        X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(certDer));

        byte[] keyDer = parsePemOrDer(keyBytes);
        byte[] decrypted = NpkiKeyDecryptor.decryptPrivateKey(keyDer, password);
        byte[] pkcs8Der = unwrapPkcs8Der(decrypted);
        PrivateKey privateKey = loadPrivateKeyFromDer(pkcs8Der);
        byte[] npkiRandomNum = extractNpkiRandomNum(pkcs8Der);

        return new NpkiKeyPair(cert, privateKey, npkiRandomNum);
    }

    /** 복호화 결과가 [0] 등으로 감싸져 있으면 풀어서 실제 PKCS#8 SEQUENCE 바이트 반환 */
    private static byte[] unwrapPkcs8Der(byte[] decrypted) throws IOException {
        if (decrypted == null || decrypted.length < 2) return decrypted;
        int tag = decrypted[0] & 0xFF;
        if (tag == 0x80 || tag == 0xA0) {
            byte[] inner = skipTagAndLength(decrypted);
            if (inner != null) return unwrapPkcs8Der(inner);
        }
        try (ASN1InputStream asn1In = new ASN1InputStream(decrypted)) {
            ASN1Primitive prim = asn1In.readObject();
            if (prim == null) return decrypted;
            ASN1Sequence seq = unwrapToPkcs8Sequence(prim);
            return seq != null ? seq.getEncoded() : decrypted;
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("unknown tag")) {
                byte[] inner = skipTagAndLength(decrypted);
                if (inner != null) return unwrapPkcs8Der(inner);
            }
            throw e;
        }
    }

    /** DER에서 [0] 태그 + 길이 건너뛰고 내용 바이트 반환. 실패 시 null. */
    private static byte[] skipTagAndLength(byte[] der) {
        if (der == null || der.length < 2) return null;
        int contentStart;
        int contentLen;
        int lenByte = der[1] & 0xFF;
        if (lenByte < 128) {
            contentLen = lenByte;
            contentStart = 2;
        } else if (lenByte == 0x81 && der.length >= 3) {
            contentLen = der[2] & 0xFF;
            contentStart = 3;
        } else if (lenByte == 0x82 && der.length >= 4) {
            contentLen = ((der[2] & 0xFF) << 8) | (der[3] & 0xFF);
            contentStart = 4;
        } else {
            return null;
        }
        if (contentStart + contentLen > der.length || contentLen < 0) return null;
        byte[] out = new byte[contentLen];
        System.arraycopy(der, contentStart, out, 0, contentLen);
        return out;
    }

    private static ASN1Sequence unwrapToPkcs8Sequence(ASN1Primitive prim) {
        try {
            ASN1Encodable current = prim;
            while (current instanceof ASN1TaggedObject) {
                ASN1TaggedObject tag = (ASN1TaggedObject) current;
                try {
                    current = tag.getExplicitBaseObject();
                } catch (IllegalStateException e) {
                    if (e.getMessage() != null && e.getMessage().contains("implicit")) {
                        current = tag.getBaseObject();
                    } else {
                        throw e;
                    }
                }
                if (current == null) return null;
            }
            if (current instanceof ASN1Sequence) return (ASN1Sequence) current;
            if (current instanceof ASN1OctetString) {
                byte[] octets = ((ASN1OctetString) current).getOctets();
                try (ASN1InputStream inner = new ASN1InputStream(octets)) {
                    ASN1Primitive innerPrim = inner.readObject();
                    return innerPrim != null ? unwrapToPkcs8Sequence(innerPrim) : null;
                }
            }
            return ASN1Sequence.getInstance(current);
        } catch (Exception e) {
            return null;
        }
    }

    private static byte[] parsePemOrDer(byte[] data) throws IOException {
        String s = new String(data, java.nio.charset.StandardCharsets.UTF_8).trim();
        if (s.startsWith("-----BEGIN")) {
            return decodePemContent(s);
        }
        return data;
    }

    /** PEM 형식에서 Base64 본문만 추출하여 디코딩 */
    private static byte[] decodePemContent(String pem) {
        int beginIdx = pem.indexOf("-----BEGIN");
        if (beginIdx < 0) return new byte[0];
        int contentStart = pem.indexOf('\n', beginIdx);
        if (contentStart < 0) return new byte[0];
        contentStart++;
        int end = pem.indexOf("-----END", contentStart);
        if (end < 0) return new byte[0];
        String base64 = pem.substring(contentStart, end).replaceAll("\\s+", "");
        return Base64.getDecoder().decode(base64);
    }

    private static PrivateKey loadPrivateKeyFromDer(byte[] pkcs8Der) throws GeneralSecurityException {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(new PKCS8EncodedKeySpec(pkcs8Der));
    }

    /** TaggedObject를 풀어 내부 ASN1Sequence 반환 (DLTaggedObject 등 처리) */
    private static ASN1Sequence unwrapToSequence(ASN1Encodable enc) {
        if (enc == null) return null;
        ASN1Encodable current = enc;
        while (current instanceof ASN1TaggedObject) {
            current = ((ASN1TaggedObject) current).getExplicitBaseObject();
            if (current == null) return null;
        }
        return ASN1Sequence.getInstance(current);
    }

    /**
     * PKCS#8 PrivateKeyInfo의 attributes (tag 0)에서 OID 1.2.410.200004.10.1.1.3 값 추출.
     */
    private static byte[] extractNpkiRandomNum(byte[] pkcs8Der) {
        try {
            ASN1Sequence seq = ASN1Sequence.getInstance(pkcs8Der);
            if (seq.size() < 3) return new byte[0];
            // PKCS#8: version, algorithm, privateKey [0] attributes (context-tagged)
            ASN1Sequence alg = ASN1Sequence.getInstance(seq.getObjectAt(1));
            for (int i = 2; i < seq.size(); i++) {
                ASN1Sequence tagged = unwrapToSequence(seq.getObjectAt(i));
                if (tagged == null || tagged.size() < 1) continue;
                String oid = tagged.getObjectAt(0).toString();
                if (!OID_NPKI_RANDOM_NUM.equals(oid)) continue;
                if (tagged.size() >= 2) {
                    ASN1Sequence set = unwrapToSequence(tagged.getObjectAt(1));
                    if (set != null && set.size() >= 1 && set.getObjectAt(0) instanceof DEROctetString)
                        return ((DEROctetString) set.getObjectAt(0)).getOctets();
                }
            }
            return extractNpkiRandomNumFromAttributes(seq);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private static byte[] extractNpkiRandomNumFromAttributes(ASN1Sequence pkcs8) {
        try {
            for (int i = 2; i < pkcs8.size(); i++) {
                if (!(pkcs8.getObjectAt(i) instanceof ASN1TaggedObject)) continue;
                ASN1TaggedObject tag = (ASN1TaggedObject) pkcs8.getObjectAt(i);
                if (tag.getTagNo() != 0) continue;
                ASN1Sequence attrs = unwrapToSequence(tag.getExplicitBaseObject());
                if (attrs == null) continue;
                for (int j = 0; j < attrs.size(); j++) {
                    ASN1Sequence attr = unwrapToSequence(attrs.getObjectAt(j));
                    if (attr == null || attr.size() < 2) continue;
                    String oid = attr.getObjectAt(0).toString();
                    if (!OID_NPKI_RANDOM_NUM.equals(oid)) continue;
                    ASN1Sequence set = unwrapToSequence(attr.getObjectAt(1));
                    if (set != null && set.size() >= 1) {
                        ASN1Encodable v = set.getObjectAt(0);
                        if (v instanceof ASN1OctetString)
                            return ((ASN1OctetString) v).getOctets();
                        if (v instanceof org.bouncycastle.asn1.DERBitString)
                            return ((org.bouncycastle.asn1.DERBitString) v).getBytes();
                    }
                }
            }
        } catch (Exception ignored) {}
        return new byte[0];
    }
}
