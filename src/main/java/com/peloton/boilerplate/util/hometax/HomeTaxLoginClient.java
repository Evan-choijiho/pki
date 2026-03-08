package com.peloton.boilerplate.util.hometax;

import com.peloton.boilerplate.util.hometax.npki.NpkiKeyPair;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateEncodingException;
import java.security.Signature;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 홈택스 공동인증서 로그인.
 * - pkcEncSsn 수신 → PKCS#1 v1.5 SHA-256 서명 → logSgnt 생성 → POST pubcLogin.do
 */
public final class HomeTaxLoginClient {

    private static final String PKC_ENC_SSN_URL = "https://www.hometax.go.kr/wqAction.do?actionId=ATXPPZXA001R01&screenId=UTXPPABA01";
    private static final String LOGIN_URL = "https://www.hometax.go.kr/pubcLogin.do?domain=hometax.go.kr&mainSys=Y";
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final Pattern RE_LOGIN_CALLBACK = Pattern.compile("nts_loginSystemCallback\\s*\\('(\\w+)'\\s*,\\s*\\{(.+)\\}\\s*\\)", Pattern.DOTALL);
    private static final Pattern RE_KEY_VALUE = Pattern.compile("'(\\w+)'\\s*:\\s*([^,}]+)");

    private final HttpClient client;
    private final NpkiKeyPair keyPair;

    public HomeTaxLoginClient(NpkiKeyPair keyPair) {
        this.keyPair = keyPair;
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NEVER)
                .cookieHandler(cookieManager)
                .sslContext(createSSLContext())
                .build();
    }

    private static SSLContext createSSLContext() {
        try {
            return SSLContext.getDefault();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인 수행. 성공 시 true.
     */
    public LoginResult login() throws IOException, InterruptedException, CertificateEncodingException {
        String pkcEncSsn = fetchPkcEncSsn();
        if (pkcEncSsn == null || pkcEncSsn.isEmpty())
            return LoginResult.fail("pkcEncSsn not found");

        byte[] signature = signWithSha256Pkcs1(pkcEncSsn);
        String logSgnt = buildLogSgnt(pkcEncSsn, signature);
        String certPem = toPemCertificate(keyPair.getCertificate().getEncoded());

        String form = "logSgnt=" + formEncode(logSgnt)
                + "&cert=" + formEncode(certPem)
                + "&randomEnc=" + formEncode(Base64.getEncoder().encodeToString(keyPair.getNpkiRandomNum()))
                + "&pkcLoginYnImpv=Y"
                + "&pkcLgnClCd=03"
                + "&ssoStatus="
                + "&portalStatus="
                + "&scrnId=UTXPPABA01"
                + "&userScrnRslnXcCnt=1451"
                + "&userScrnRslnYcCnt=907";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .header("Origin", "https://www.hometax.go.kr")
                .header("Referer", "https://www.hometax.go.kr/websquare/websquare.wq?w2xPath=/ui/comm/a/b/UTXPPABA01.xml&w2xHome=/ui/pp/&w2xDocumentRoot=")
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .POST(HttpRequest.BodyPublishers.ofString(form, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200)
            return LoginResult.fail("HTTP " + response.statusCode());

        return parseLoginResult(response.body());
    }

    private String fetchPkcEncSsn() throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(PKC_ENC_SSN_URL))
                .GET()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() != 200) return null;
        return extractPkcEncSsnFromXml(res.body());
    }

    private static String extractPkcEncSsnFromXml(String xml) {
        int start = xml.indexOf("<pkcEncSsn>");
        if (start < 0) return "";
        start += "<pkcEncSsn>".length();
        int end = xml.indexOf("</pkcEncSsn>", start);
        if (end < 0) return "";
        return xml.substring(start, end).trim();
    }

    private byte[] signWithSha256Pkcs1(String message) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(keyPair.getPrivateKey());
            sig.update(message.getBytes(StandardCharsets.UTF_8));
            return sig.sign();
        } catch (Exception e) {
            throw new RuntimeException("Sign failed", e);
        }
    }

    private String buildLogSgnt(String pkcEncSsn, byte[] signature) {
        byte[] serialBytes = keyPair.getCertificate().getSerialNumber().toByteArray();
        if (serialBytes[0] == 0 && serialBytes.length > 1)
            serialBytes = java.util.Arrays.copyOfRange(serialBytes, 1, serialBytes.length);
        String serial = HexFormat.of().formatHex(serialBytes);
        String ts = ZonedDateTime.now(SEOUL).format(TS_FORMAT);
        String payload = pkcEncSsn + "$" + serial + "$" + ts + "$" + Base64.getEncoder().encodeToString(signature);
        return Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }

    private static String toPemCertificate(byte[] der) {
        String b64 = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.UTF_8)).encodeToString(der);
        return "-----BEGIN CERTIFICATE-----\n" + b64 + "\n-----END CERTIFICATE-----";
    }

    private static String formEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    private static LoginResult parseLoginResult(String body) {
        Matcher m = RE_LOGIN_CALLBACK.matcher(body);
        if (!m.find())
            return LoginResult.fail("login callback not found in response");
        String sysCode = m.group(1);
        String jsonPart = m.group(2);
        String code = "";
        String errMsg = "";
        String tin = "";
        for (Matcher kv = RE_KEY_VALUE.matcher(jsonPart); kv.find(); ) {
            String key = kv.group(1);
            String value = kv.group(2).trim().replaceAll("^'|'$", "").replace("\\'", "'");
            switch (key) {
                case "code": code = value; break;
                case "errMsg": errMsg = value; break;
                case "tin": tin = value; break;
                default: break;
            }
        }
        boolean success = "S".equals(code);
        return new LoginResult(success, errMsg, tin, sysCode);
    }

    public static final class LoginResult {
        private final boolean success;
        private final String errMsg;
        private final String tin;
        private final String sysCode;

        LoginResult(boolean success, String errMsg, String tin, String sysCode) {
            this.success = success;
            this.errMsg = errMsg != null ? errMsg : "";
            this.tin = tin != null ? tin : "";
            this.sysCode = sysCode != null ? sysCode : "";
        }

        static LoginResult fail(String errMsg) {
            return new LoginResult(false, errMsg, "", "");
        }

        public boolean isSuccess() { return success; }
        public String getErrMsg() { return errMsg; }
        public String getTin() { return tin; }
        public String getSysCode() { return sysCode; }
    }
}
