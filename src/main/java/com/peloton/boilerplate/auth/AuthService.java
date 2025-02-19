package com.peloton.boilerplate.auth;

import com.peloton.boilerplate.db.dao.UserRepository;
import com.peloton.boilerplate.exception.*;
import com.peloton.boilerplate.model.dto.response.UserDto;
import com.peloton.boilerplate.service.WebSupportService;
import com.peloton.boilerplate.util.ServiceUtils;
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

    @Autowired
    UserRepository userRepository;

    public void authAndCheckPermission(HttpServletRequest httpRequest) {
        String accessToken = httpRequest.getHeader(WebLogUtils.HEADER_NAME_MEMBER_ACCESS_TOKEN);

        if ( webSupportService.isLocal() ) {	// local 의 경우 default value 세팅
            if (accessToken == null) {
                //accessToken = httpRequest.getHeader(WebLogUtils.HEADER_NAME_MEMBER_ACCESS_TOKEN) != null ? httpRequest.getHeader(WebLogUtils.HEADER_NAME_MEMBER_ACCESS_TOKEN) : "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyU2lkIjoyMDAwMDAsImV4cCI6MzMxMTgyMDU3MywidXNlcklkIjoiaGFuc2VuZXJAbmF2ZXIuY29tIiwiaWF0IjoxNzM1MDIwNTczfQ.ADK4eI0XGXuggnOtqefk2NWA6LfJuX7zBSAN1LpaXcE";
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
        if (StringUtils.isEmpty(accessToken)) {
            throw new ClientRequestInputMissingException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.AccessToken, null);
        }

        final Long userSid = checkAccessToken(accessToken);
        // check user in DB
        final UserDto userDto = new UserDto(userRepository.findBySidAndDeleteTimeIsNull(userSid)) ;
        // validate 'status' of members
        switch (userDto.getStatus()) {
            case active:
            case pending:
                break; // validated
            case expiration:
                System.out.println("###### case expiration");
                throw new UserExpiredException(ServiceException.ErrorTarget.User, null);
        }
        // check 'admin' access policy
        this.checkAdminUserPolicy(userSid);

        WebLogUtils.setAuthUserSid(userSid);
        WebLogUtils.setAuthUserId(userDto.getUserId());
        WebLogUtils.setAuthUserName(userDto.getName());
        WebLogUtils.setAuthCompanySid(userDto.getCompanySid());
    }

    private void extractAuthMemberInfo(HttpServletRequest httpRequest, final String accessToken) {
        // accessToken이 존재하면 authMember 정보만 extract
        // - 존재하지 않아도 exception이 발생하지 않는다.
        // - memberSid 를 extract하여 ThreadLocal에 기록
        try {
            if (accessToken != null) {
                final Long userSid = checkAccessToken(accessToken);
                WebLogUtils.setAuthUserSid(userSid);
            }
        } catch (Exception e) {

        }
    }

    private Long checkAccessToken(@NonNull String accessToken) {
        final Long userSid = UserAuthUtils.extractAccessToken(accessToken);
        return userSid;
    }

    private void checkAdminUserPolicy(@NonNull Long userSid) {
        if (userSid.longValue() == ServiceUtils.adminUserSid) { // admin
            if (webSupportService.isProduction()) {
                // Not permitted in Production
                throw new AuthenticationFailedException(ServiceException.ErrorTarget.AccessToken, null);
            }
        }
        return;
    }

}
