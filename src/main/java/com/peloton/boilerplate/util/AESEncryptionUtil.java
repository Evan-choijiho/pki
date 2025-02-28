package com.peloton.boilerplate.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class AESEncryptionUtil {
    @Value("${encrypt.column.algorithm}")
    private String algorithm;
    @Value("${encrypt.column.transformation}")
    private String transformation;
    @Value("${encrypt.column.sec-key}")
    private String secKey;

    private static String ALGORITHM;
    private static String TRANSFORMATION;
    private static String SECRET_KEY;
    @PostConstruct
    public void init() {
        ALGORITHM = algorithm;
        TRANSFORMATION = transformation;
        SECRET_KEY = secKey;
    }

    // AES 암호화
    public static String encrypt(String value) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // AES 복호화
    public static String decrypt(String encryptedValue) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decrypted = cipher.doFinal(decodedValue);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
