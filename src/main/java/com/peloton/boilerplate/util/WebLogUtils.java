package com.peloton.boilerplate.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.peloton.boilerplate.log.AccessLog;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.util.StringUtils;
import java.util.UUID;

public class WebLogUtils {

    public static final String HEADER_NAME_MEMBER_ACCESS_TOKEN = "X-Member-Access-Token";
    public static final String HEADER_NAME_REQUEST_ID = "X-Request-Id";

    protected static Logger accessLogger = LoggerFactory.getLogger("access");
    protected static Logger systemLogger = LoggerFactory.getLogger("system");

    public static boolean isServicePath(String uri) {
        if (uri.startsWith("/api/")) {   // /api/ssm/
            return true;
        }
        return false;
    }

    // 카카오 로그인 구현 에서와 같이 RedirectUri 의 BaseUrl 캐치 때 사용
    public static String getRequestBaseUrl(HttpServletRequest request) {
        return request	.getRequestURL()
                .substring(0, request	.getRequestURL()
                        .length()
                        - request	.getRequestURI()
                        .length())
                + request.getContextPath();
    }

    // *********************************************
    // Access Log
    // *********************************************
    public static void writeAccessRequestLog(HttpServletRequest request, Boolean enabled, Object body) {
        if (enabled) {
            try {
                // accessLogger.info => logback 사용하여 로그를 기록
                // ObjectMapper => Jackson 라이브러리를 사용하여 객체를 JSON으로 변환
                // registerModule(new JavaTimeModule()) => LocalDateTime 같은 Java 시간 타입을 JSON으로 변환
                // .writeValueAsString(new AccessLog(request, AccessLog.Type.REQ, body)) => AccessLog 객체를 "JSON 문자열"로 변환

                accessLogger.info(new ObjectMapper().registerModule(new JavaTimeModule())
                        .writeValueAsString(new AccessLog(request, AccessLog.Type.REQ, body)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    // *********************************************
    // System Log
    // *********************************************
    public static void writeSystemLog(Level level, String message) {
        if (level == Level.ERROR) {
            systemLogger.error(message);
        } else if (level == Level.INFO) {
            systemLogger.info(message);
        } else if (level == Level.WARN) {
            systemLogger.warn(message);
        } else if (level == Level.DEBUG) {
            systemLogger.debug(message);
        }
    }

    public static void writeSystemStackTrace(Level level, Exception e) {
        if (level == Level.ERROR) {
            systemLogger.error(String.format("'%s' StackTrace:", WebLogUtils.getRequestId()), e);
        } else if (level == Level.INFO) {
            systemLogger.info(String.format("'%s' StackTrace:", WebLogUtils.getRequestId()), e);
        } else if (level == Level.WARN) {
            systemLogger.warn(String.format("'%s' StackTrace:", WebLogUtils.getRequestId()), e);
        } else if (level == Level.DEBUG) {
            systemLogger.debug(String.format("'%s' StackTrace:", WebLogUtils.getRequestId()), e);
        }
    }

    // *********************************************
    // ThreadLocal
    // *********************************************
    public static void setThreadLocalsFromHttpHeader(HttpServletRequest request) {
        WebLogUtils.setTxStartTime();
        WebLogUtils.setRequestId(request.getHeader(HEADER_NAME_REQUEST_ID));
        WebLogUtils.setRequestIp(request);
        WebLogUtils.setResponseErrorCode(0);
    }

    public static void removeAllThreadLocals() {
        WebLogUtils.removeTxStartTime();
        WebLogUtils.removeRequestId();
        WebLogUtils.removeAuthUserSid();
        WebLogUtils.removeRequestIp();
        WebLogUtils.removeRequestUrl();
        WebLogUtils.removeResponseErrorCode();
    }

    // ThreadLocal : requestId
    protected final static ThreadLocal<String> requestIdThreadLocal = new ThreadLocal<String>();

    public static String getRequestId() {
        String requestId = requestIdThreadLocal.get();
        return StringUtils.hasText(requestId) ? requestId : newRequestId();
    }

    public static void removeRequestId() {
        requestIdThreadLocal.remove();
    }

    public static void setRequestId(String requestId) {
        if (requestId != null) {
            requestIdThreadLocal.set(requestId);
        } else {
            requestIdThreadLocal.remove();
        }
    }

    protected static String newRequestId() {
        String requestId = UUID.randomUUID()
                .toString();
        requestIdThreadLocal.set(requestId);
        return requestId;
    }

    // ThreadLocal : txStartTime
    protected final static ThreadLocal<Long> txStartTimeThreadLocal = new ThreadLocal<Long>();

    public static Long getTxStartTime() {
        return txStartTimeThreadLocal.get();
    }

    public static Long getTxResponseTime() {
        return getTxStartTime() != null ? (System.currentTimeMillis() / 1000) - getTxStartTime() : null;
    }

    public static void setTxStartTime() {
        txStartTimeThreadLocal.set(System.currentTimeMillis() / 1000);
    }

    public static void removeTxStartTime() {
        txStartTimeThreadLocal.remove();
    }

    // ThreadLocal : authUserSid
    protected final static ThreadLocal<Long> authUserSidThreadLocal = new ThreadLocal<Long>();
    public static Long getAuthUserSid() {
        return authUserSidThreadLocal.get();
    }
    public static void setAuthUserSid(Long authUserSid) {
        if (authUserSid != null) {
            authUserSidThreadLocal.set(authUserSid);
        } else {
            authUserSidThreadLocal.remove();
        }
    }
    public static void removeAuthUserSid() {
        authUserSidThreadLocal.remove();
    }

    // ThreadLocal : requestIp
    protected final static ThreadLocal<String> requestIpThreadLocal = new ThreadLocal<String>();
    public static String getRequestIp() {
        return requestIpThreadLocal.get();
    }
    public static void setRequestIp(HttpServletRequest request) {
        final String requestIp = !StringUtils.hasText(request.getHeader("X-Forwarded-For")) ? request.getRemoteAddr() : request.getHeader("X-Forwarded-For"); // X-Forwarded-For header check
        setRequestIp(requestIp);
    }
    public static void setRequestIp(String requestIp) {
        if (requestIp != null) {
            requestIpThreadLocal.set(requestIp);
        } else {
            requestIpThreadLocal.remove();
        }
    }

    public static void removeRequestIp() {
        requestIpThreadLocal.remove();
    }

    // ThreadLocal : requestUrl
    protected final static ThreadLocal<String> requestUrlThreadLocal = new ThreadLocal<String>();

    public static void removeRequestUrl() {
        requestUrlThreadLocal.remove();
    }

    // ThreadLocal : responseErrorCode
    protected final static ThreadLocal<Integer> responseErrorCodeThreadLocal = new ThreadLocal<Integer>();

    public static Integer getResponseErrorCode() {
        return responseErrorCodeThreadLocal.get();
    }

    public static void setResponseErrorCode(Integer errorCode) {
        if (errorCode != null) {
            responseErrorCodeThreadLocal.set(errorCode);
        } else {
            responseErrorCodeThreadLocal.remove();
        }
    }

    public static void removeResponseErrorCode() {
        responseErrorCodeThreadLocal.remove();
    }

}
