package com.peloton.boilerplate.util.hometax;

import com.peloton.boilerplate.util.hometax.npki.NpkiKeyPair;

import java.io.Console;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 홈택스 공동인증서 로그인 실행 클래스.
 *
 * 사용법:
 *   mvn compile exec:java -Dexec.mainClass="kr.go.hometax.Main" -Dexec.args="/path/to/NPKI/KICA/USER/..."
 *   또는
 *   java -cp ... kr.go.hometax.Main /path/to/certdir
 *
 * certdir: signCert.der, signPri.key 가 있는 NPKI 인증서 디렉터리
 */
public class Main {

    public static void main(String[] args) {
        if (args == null || args.length < 1) {
            System.err.println("Usage: java kr.go.hometax.Main <certdir>");
            System.err.println("  certdir  NPKI certificate directory (e.g. .../NPKI/KICA/USER/...)");
            System.exit(1);
        }

        Path certDir = Paths.get(args[0]).toAbsolutePath();
        if (!certDir.toFile().exists() || !certDir.toFile().isDirectory()) {
            System.err.println("Not a directory: " + certDir);
            System.exit(1);
        }

        char[] password = readPassword();
        if (password == null || password.length == 0) {
            System.err.println("Password required.");
            System.exit(1);
        }

        try {
            NpkiKeyPair keyPair = NpkiKeyPair.load(certDir, password);
            Arrays.fill(password, '\0');
            HomeTaxLoginClient client = new HomeTaxLoginClient(keyPair);
            HomeTaxLoginClient.LoginResult result = client.login();
            if (result.isSuccess()) {
                System.out.println("LOGIN SUCCESS");
                if (!result.getTin().isEmpty())
                    System.out.println("TIN: " + result.getTin());
            } else {
                System.err.println("LOGIN FAILED: " + result.getErrMsg());
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static char[] readPassword() {
        Console cons = System.console();
        if (cons != null) {
            return cons.readPassword("Enter certificate password: ");
        }
        System.err.println("Console not available. Set password via env: NPKI_PASSWORD");
        String env = System.getenv("NPKI_PASSWORD");
        return env != null ? env.toCharArray() : null;
    }
}
