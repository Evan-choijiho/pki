package com.peloton.boilerplate.util.hometax.npki;

import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Arrays;

/**
 * NPKI 공동인증서 signPri.key 복호화.
 * 규격: PKCS#5 PBES2 + PBKDF2(HMAC-SHA1) + SEED-CBC.
 * 키 유도: PBKDF2 → 20바이트, 앞 16바이트=키, 뒤 4바이트 SHA-1 해시 앞 16바이트=IV.
 */
public final class NpkiKeyDecryptor {

    private static final String OID_PBES2 = "1.2.840.113549.1.5.13";
    private static final String OID_SEED_CBC = "1.2.410.200004.1.4";
    private static final String OID_SEED_CBC_SHA1 = "1.2.410.200004.1.15";

    static {
        try {
            java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        } catch (Exception ignored) {}
    }

    /**
     * EncryptedPrivateKeyInfo(DER) + 비밀번호로 복호화하여 PKCS#8 PrivateKeyInfo 옥텟 반환.
     * 파일에 여러 DER 객체나 trailing bytes가 있으면 첫 번째 ASN.1 객체만 파싱.
     */
    public static byte[] decryptPrivateKey(byte[] encryptedPkcs8Der, char[] password)
            throws GeneralSecurityException, IOException {
        ASN1Sequence seq = readFirstSequence(encryptedPkcs8Der);
        if (seq == null || seq.size() < 2)
            throw new IllegalArgumentException("Invalid EncryptedPrivateKeyInfo");
        ASN1Sequence algId = ASN1Sequence.getInstance(seq.getObjectAt(0));
        ASN1OctetString encryptedData = ASN1OctetString.getInstance(seq.getObjectAt(1));
        String oid = ASN1ObjectIdentifier.getInstance(algId.getObjectAt(0)).getId();

        if (OID_PBES2.equals(oid)) {
            return decryptPBES2(algId, encryptedData.getOctets(), password);
        }
        if (OID_SEED_CBC.equals(oid) || OID_SEED_CBC_SHA1.equals(oid)) {
            return decryptSeedCbcLegacy(algId, encryptedData.getOctets(), password);
        }
        throw new GeneralSecurityException("Unsupported encryption OID: " + oid);
    }

    /** 바이트 배열에서 EncryptedPrivateKeyInfo에 해당하는 SEQUENCE를 읽어 반환. [0] 태그로 감싸진 경우 재귀 풀기. */
    private static ASN1Sequence readFirstSequence(byte[] der) throws IOException {
        if (der == null || der.length < 2) return null;
        int tag = der[0] & 0xFF;
        if (tag == 0x80 || tag == 0xA0) {
            byte[] inner = skipTagAndLength(der);
            if (inner != null) return readFirstSequence(inner);
        }
        try (ASN1InputStream asn1In = new ASN1InputStream(der)) {
            ASN1Primitive prim = asn1In.readObject();
            if (prim == null) return null;
            return unwrapToSequence(prim, asn1In);
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("unknown tag")) {
                byte[] inner = skipTagAndLength(der);
                if (inner != null) return readFirstSequence(inner);
            }
            throw e;
        }
    }

    /** DER에서 [0] 태그 + 길이 건너뛰고 내용 바이트 반환. */
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

    /** TaggedObject/OctetString를 풀어 내부 ASN1Sequence 반환. [0] IMPLICIT OCTET STRING(암호문만) + 다음 객체(alg) 형식 지원. */
    private static ASN1Sequence unwrapToSequence(ASN1Primitive prim, ASN1InputStream asn1In) throws IOException {
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
            if (octets.length >= 64) {
                try {
                    return readFirstSequence(octets);
                } catch (IOException | IllegalArgumentException ignored) {
                }
            }
            if (asn1In != null) {
                ASN1Primitive second = asn1In.readObject();
                if (second instanceof ASN1Sequence) {
                    return new DERSequence(new ASN1Encodable[] { (ASN1Sequence) second, new DEROctetString(octets) });
                }
            }
            return null;
        }
        return ASN1Sequence.getInstance(current);
    }

    private static byte[] decryptPBES2(ASN1Sequence algId, byte[] encrypted, char[] password)
            throws GeneralSecurityException, IOException {
        PBES2Parameters pbes2 = PBES2Parameters.getInstance(algId.getObjectAt(1));
        PBKDF2Params kdfParams = PBKDF2Params.getInstance(pbes2.getKeyDerivationFunc().getParameters());
        byte[] salt = kdfParams.getSalt();
        int iter = kdfParams.getIterationCount().intValueExact();

        ASN1Sequence encScheme = ASN1Sequence.getInstance(pbes2.getEncryptionScheme());
        String encOid = ASN1ObjectIdentifier.getInstance(encScheme.getObjectAt(0)).getId();
        if (!OID_SEED_CBC.equals(encOid) && !OID_SEED_CBC_SHA1.equals(encOid))
            throw new GeneralSecurityException("Unsupported PBES2 encryption: " + encOid);

        byte[] keyBytes = pbkdf2HmacSha1(password, salt, iter, 20);
        byte[] key = Arrays.copyOf(keyBytes, 16);
        byte[] iv;
        if (encScheme.size() > 1 && encScheme.getObjectAt(1) instanceof ASN1OctetString)
            iv = ASN1OctetString.getInstance(encScheme.getObjectAt(1)).getOctets();
        else
            iv = first16OfSha1(Arrays.copyOfRange(keyBytes, 16, 20));
        return decryptSeedCbc(encrypted, key, iv);
    }

    private static byte[] decryptSeedCbcLegacy(ASN1Sequence algId, byte[] encrypted, char[] password)
            throws GeneralSecurityException, IOException {
        byte[] salt = new byte[0];
        int iter = 1;
        if (algId.size() > 1) {
            ASN1Sequence params = ASN1Sequence.getInstance(algId.getObjectAt(1));
            for (int i = 0; i < params.size(); i++) {
                ASN1Primitive p = params.getObjectAt(i).toASN1Primitive();
                if (p instanceof ASN1OctetString)
                    salt = ((ASN1OctetString) p).getOctets();
                else if (p instanceof ASN1Integer)
                    iter = ((ASN1Integer) p).intValueExact();
            }
        }
        byte[] keyBytes = pbkdf2HmacSha1(password, salt, iter, 20);
        byte[] key = Arrays.copyOf(keyBytes, 16);
        byte[] iv = first16OfSha1(Arrays.copyOfRange(keyBytes, 16, 20));
        return decryptSeedCbc(encrypted, key, iv);
    }

    private static byte[] first16OfSha1(byte[] input) {
        try {
            return Arrays.copyOf(MessageDigest.getInstance("SHA-1").digest(input), 16);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] pbkdf2HmacSha1(char[] password, byte[] salt, int iterations, int keyLen)
            throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLen * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WITHHMACSHA1", "BC");
        byte[] key = skf.generateSecret(spec).getEncoded();
        spec.clearPassword();
        return key;
    }

    private static byte[] decryptSeedCbc(byte[] encrypted, byte[] key, byte[] iv) {
        SEEDEngine engine = new SEEDEngine();
        CBCBlockCipher cbc = new CBCBlockCipher(engine);
        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(cbc);
        CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
        cipher.init(false, params);
        byte[] out = new byte[cipher.getOutputSize(encrypted.length)];
        int len = cipher.processBytes(encrypted, 0, encrypted.length, out, 0);
        try {
            len += cipher.doFinal(out, len);
        } catch (org.bouncycastle.crypto.InvalidCipherTextException e) {
            throw new RuntimeException("Decrypt failed (wrong password or corrupted key)", e);
        }
        return Arrays.copyOf(out, len);
    }

    private NpkiKeyDecryptor() {}
}
