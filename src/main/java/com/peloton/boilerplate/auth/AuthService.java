package com.peloton.boilerplate.auth;

import com.peloton.boilerplate.exception.*;
import com.peloton.boilerplate.service.common.WebSupportService;
import com.peloton.boilerplate.util.UserAuthUtils;
import com.peloton.boilerplate.util.WebLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {
    @Autowired
    private AuthServiceConfig authServiceConfig;

    @Autowired
    private WebSupportService webSupportService;

    public void authAndCheckPermission(HttpServletRequest httpRequest) {
        String accessToken = httpRequest.getHeader(WebLogUtils.HEADER_NAME_MEMBER_ACCESS_TOKEN);

        if ( webSupportService.isLocal() ) {	// local 의 경우 default value 세팅
            if (accessToken == null) {
                accessToken = httpRequest.getHeader(WebLogUtils.HEADER_NAME_MEMBER_ACCESS_TOKEN) != null ?
                        httpRequest.getHeader(WebLogUtils.HEADER_NAME_MEMBER_ACCESS_TOKEN) : "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyU2lkIjoyMDAwMDAsImV4cCI6MzMxMTgyMDU3MywidXNlcklkIjoiaGFuc2VuZXJAbmF2ZXIuY29tIiwiaWF0IjoxNzM1MDIwNTczfQ.ADK4eI0XGXuggnOtqefk2NWA6LfJuX7zBSAN1LpaXcE";
            }
        }

        final String requestUri = httpRequest	.getRequestURI()
                .replaceAll("/+", "/");
        final String requestMethod = httpRequest.getMethod()
                .toUpperCase();
        PathPermission.Role requiredRole = authServiceConfig.getPathRole(requestUri, requestMethod);

        // check request permission
        switch (requiredRole) {
            case WEB:
            case VISITOR:
                break;
            case ADMIN:
            case USER:
                checkMemberPermission(httpRequest, accessToken);    // token 으로 user 세팅
                break;
            default:
                throw new ServerSystemException(null);
        }
    }

    private void checkMemberPermission(HttpServletRequest httpRequest, final String accessToken) {
        // check accessToken
        if (!StringUtils.hasText(accessToken)) {
            throw new ClientRequestInputMissingException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.AccessToken, null);
        }
        final Long userSid = checkAccessToken(accessToken);
        WebLogUtils.setAuthUserSid(userSid);
    }

    private Long checkAccessToken(@NonNull String accessToken) {
        final Long userSid = UserAuthUtils.extractAccessToken(accessToken);
        return userSid;
    }

}
