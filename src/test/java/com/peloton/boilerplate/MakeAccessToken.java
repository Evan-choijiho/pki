package com.peloton.boilerplate;

import com.peloton.boilerplate.exception.ServerSystemException;
import com.peloton.boilerplate.exception.ServiceException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@TestPropertySource(locations = "/application-dev.properties")
@SpringBootTest
@Transactional
@Rollback(value = false)
public class MakeAccessToken {
    public enum TokenType {
        access_token("2JHNiybwFWcO2o3S", //
                //60 * 60 * 24 * 10, // 10 days
                60 * 60 * 24 * 365 * 50, // 50 years
                //10, // 10 seconds
                ServiceException.ErrorTarget.AccessToken //
        ); //

        String secretKey;
        long ttl;
        ServiceException.ErrorTarget errorType;

        private TokenType(String secretKey, long ttl, ServiceException.ErrorTarget errorType) {
            this.secretKey = secretKey;
            this.ttl = ttl;
            this.errorType = errorType;
        }
    }

    @Test
    public void MakeAccessToken(){
        // 원하는 user 의 sid
        Long userSid = Long.valueOf(200000);
        String access_token = createAccessToken(userSid);

        System.out.println("###### access_token : " + access_token);
    }

    public static String createAccessToken(Long userSid) {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("userSid", userSid);
        return createToken(claimsMap, TokenType.access_token);
    }

    protected static String createToken(Map<String, Object> claimsMap, TokenType tokeyType) {
        Date issueTime = new Date(System.currentTimeMillis());
        Date expireTime = new Date(issueTime.getTime() + tokeyType.ttl * 1000);

        return createToken(claimsMap, tokeyType.secretKey, issueTime, expireTime);
    }

    protected static String createToken(Map<String, Object> claimsMap, String secretKey, Date issueTime, Date expireTime) {
        try {
            return Jwts.builder()
                    .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                    .setClaims(claimsMap)
                    .setIssuedAt(issueTime)
                    .setExpiration(expireTime)
                    .signWith(SignatureAlgorithm.HS256, secretKey.getBytes("UTF-8"))
                    .compact();
        } catch (UnsupportedEncodingException e) {
            throw new ServerSystemException(e);
        }
    }
}
