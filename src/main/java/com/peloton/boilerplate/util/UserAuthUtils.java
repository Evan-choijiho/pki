package com.peloton.boilerplate.util;

import com.peloton.boilerplate.exception.*;
import com.peloton.boilerplate.model.dto.external.MemberInfoDto;
import io.jsonwebtoken.*;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserAuthUtils {

    public enum TokenType {
        refresh_token("vEuwKyukn5tKiRS5", //
                60 * 60 * 24 * 90, // 90 days
                ServiceException.ErrorTarget.RefreshToken //
        ), //
        access_token("2JHNiybwFWcO2o3S", //
                60 * 60 * 1, // 1 hour
                ServiceException.ErrorTarget.AccessToken //
        ), //
        member_info_token("qs2v7WwIpy0TAncf", //
                60 * 60 * 1, // 1 hour
                ServiceException.ErrorTarget.MemberInfoToken //
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

    public static String createRefreshToken(String userId, Long userSid) {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("userId", userId);
        claimsMap.put("userSid", userSid);
        //claimsMap.put("companySid", companySid);
        return createToken(claimsMap, TokenType.refresh_token);
    }

    public static String createAccessToken(String userId, Long userSid) {
        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("userId", userId);
        claimsMap.put("userSid", userSid);
        //claimsMap.put("companySid", companySid);
        return createToken(claimsMap, TokenType.access_token);
    }

    public static String createMemberInfoToken(MemberInfoDto memberInfoDto) {
        Map<String, Object> claimsMap = memberInfoDto.toMap();
        return createToken(claimsMap, TokenType.member_info_token);
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

    public static Long extractRefreshToken(String refreshToken) {
        Claims claims = getTokenClaims(refreshToken, TokenType.refresh_token);
        return Long.valueOf((Integer) claims.get("userSid"));
    }

    public static Long extractAccessToken(String accessToken) {
        Claims claims = getTokenClaims(accessToken, TokenType.access_token);
        return Long.valueOf((Integer) claims.get("userSid"));
    }

    public static MemberInfoDto extractMemberInfoToken(String memberInfoToken) {
        Claims claims = getTokenClaims(memberInfoToken, TokenType.member_info_token);
        return MemberInfoDto.from(claims);
    }

    protected static Claims getTokenClaims(String token, TokenType tokenType) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(tokenType.secretKey)) {
            throw new ClientRequestInputMissingException(ServiceException.ErrorType.ClientSystem, tokenType.errorType, null);
        } else {
            try {
                return Jwts	.parser()
                        .setSigningKey(tokenType.secretKey.getBytes("UTF-8"))
                        .parseClaimsJws(token)
                        .getBody();
            } catch (ExpiredJwtException e) {
                throw new AuthenticationExpiredException(tokenType.errorType, e);
            } catch (Exception e) {
                throw new AuthenticationFailedException(tokenType.errorType, e);
            }
        }
    }

    protected final static ThreadLocal<Long> authUserIdThreadLocal = new ThreadLocal<Long>();

    public static void checkAdminMemberPermission() {
        @NonNull
        final Long authMemberSid = WebLogUtils.getAuthUserSid();

        if (authMemberSid.longValue() == ServiceUtils.adminUserSid) { // admin
            return;
        } else { // no matched target user
            throw new AuthenticationFailedException(ServiceException.ErrorTarget.AccessToken, null);
        }
    }

    public static void checkAuthMemberPermission(@NonNull Long targetUserSid) {
        @NonNull
        final Long authUserSid = WebLogUtils.getAuthUserSid();

        if (authUserSid.longValue() == ServiceUtils.adminUserSid) { // admin
            return;
        } else if (authUserSid.longValue() == targetUserSid.longValue()) { // member
            return;
        } else { // no matched target member
            throw new AuthenticationFailedException(ServiceException.ErrorTarget.AccessToken, null);
        }
    }
}
