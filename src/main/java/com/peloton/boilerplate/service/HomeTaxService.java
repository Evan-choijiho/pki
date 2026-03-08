package com.peloton.boilerplate.service;

import com.peloton.boilerplate.model.dto.response.HomeTaxLoginResultDto;
import com.peloton.boilerplate.util.hometax.HomeTaxLoginClient;
import com.peloton.boilerplate.util.hometax.npki.NpkiKeyPair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

@Slf4j
@Service
public class HomeTaxService {

    @Value("${hometax.cert.dir:}")
    private String certDirPath;

    @Value("${hometax.cert.password:}")
    private String certPassword;

    /**
     * 설정된 폴더의 signPri.key, signCert.der로 홈택스 공인인증서 로그인 수행.
     * 설정: hometax.cert.dir (인증서 디렉터리), hometax.cert.password (인증서 비밀번호)
     * 또는 환경변수 HOMETAX_CERT_PASSWORD 사용 가능.
     */
    public HomeTaxLoginResultDto loginWithCert() {
        if (!StringUtils.hasText(certDirPath)) {
            return HomeTaxLoginResultDto.builder()
                    .success(false)
                    .errMsg("hometax.cert.dir not configured")
                    .build();
        }

        String password = StringUtils.hasText(certPassword)
                ? certPassword
                : System.getenv("HOMETAX_CERT_PASSWORD");
        if (!StringUtils.hasText(password)) {
            return HomeTaxLoginResultDto.builder()
                    .success(false)
                    .errMsg("hometax.cert.password or HOMETAX_CERT_PASSWORD not set")
                    .build();
        }

        Path certDir = Paths.get(certDirPath).toAbsolutePath();
        if (!Files.isDirectory(certDir) || !Files.exists(certDir.resolve("signCert.der")) || !Files.exists(certDir.resolve("signPri.key"))) {
            return HomeTaxLoginResultDto.builder()
                    .success(false)
                    .errMsg("Cert dir not found or missing signCert.der/signPri.key: " + certDir)
                    .build();
        }

        try {
            NpkiKeyPair keyPair = NpkiKeyPair.load(certDir, password.toCharArray());
            HomeTaxLoginClient client = new HomeTaxLoginClient(keyPair);
            HomeTaxLoginClient.LoginResult result = client.login();

            return HomeTaxLoginResultDto.builder()
                    .success(result.isSuccess())
                    .errMsg(result.getErrMsg())
                    .tin(result.getTin())
                    .sysCode(result.getSysCode())
                    .build();
        } catch (java.io.IOException e) {
            log.warn("HomeTax cert read failed: {}", e.getMessage());
            return HomeTaxLoginResultDto.builder()
                    .success(false)
                    .errMsg("Cert file read failed: " + e.getMessage())
                    .build();
        } catch (GeneralSecurityException e) {
            log.warn("HomeTax cert load/login failed: {}", e.getMessage());
            return HomeTaxLoginResultDto.builder()
                    .success(false)
                    .errMsg("Certificate or password invalid: " + e.getMessage())
                    .build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("HomeTax login interrupted");
            return HomeTaxLoginResultDto.builder()
                    .success(false)
                    .errMsg("Login interrupted")
                    .build();
        } catch (Exception e) {
            log.warn("HomeTax login failed", e);
            return HomeTaxLoginResultDto.builder()
                    .success(false)
                    .errMsg(e.getMessage() != null ? e.getMessage() : "Login failed")
                    .build();
        }
    }
}
