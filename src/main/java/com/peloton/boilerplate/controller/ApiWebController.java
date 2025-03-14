package com.peloton.boilerplate.controller;

import com.peloton.boilerplate.exception.ClientRequestInputInvalidException;
import com.peloton.boilerplate.exception.ServiceException;
import com.peloton.boilerplate.external.oauth.KakaoOAuthManager;
import com.peloton.boilerplate.model.dto.external.WebRedirectOAuthSuceessInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Web Pages", description = "Web 기반으로 연동되는 Request를 처리하는 API")
@Controller
@RequestMapping("/api/implant/web")
public class ApiWebController {

    @Autowired
    private KakaoOAuthManager kakaoOAuthManager;

    @Operation(
            summary = "[OAuth] 카카오 로그인 성공시 redirect url", description = "OAuth 로그인 성공된 code를 전달받아 회원정보 조회후 refreshToke을 발급힌다.\n* Return Redirect URL: {baseUrl}/main.html\n* parameters:\n  * success: OAuth 성공여부 (Boolean)\n  * existence: 기존 회원 전화번호 존재 여부 (Boolean)\n  * failedCode: OAuth 실패시 code값 (801)\n  * refreshToken: 인증이 완료된 회원의 정보를 포함한 JwtToken (String)"
    )
    @GetMapping("/oauth2/code/{provider}")
    public String oauthLoginSuccess(HttpServletRequest request, @PathVariable(value = "provider", required = true) String provider,
                                    @RequestParam(value = "code", required = true) String code) {
        //log.info("########## oauthLoginSuccess : {}", provider);
        WebRedirectOAuthSuceessInfo oAuthSuceessInfo = null;
        switch (provider) {
            case "kakao":
                oAuthSuceessInfo = kakaoOAuthManager.handleOAuthCode(request, code, "prod");
                break;

            case "apple":
                //oAuthSuceessInfo = appleOAuthManager.handleOAuthCode(request, code);
                break;

            default:
                throw new ClientRequestInputInvalidException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.OAuth, null);
        }

        // OAuth success
        return oAuthSuceessInfo.getResultRedirectUrl();
    }

    @Operation(
            summary = "[OAuth] 카카오 로그인 성공시 redirect url", description = "OAuth 로그인 성공된 code를 전달받아 회원정보 조회후 refreshToke을 발급힌다.\n* Return Redirect URL: {baseUrl}/main.html\n* parameters:\n  * success: OAuth 성공여부 (Boolean)\n  * existence: 기존 회원 전화번호 존재 여부 (Boolean)\n  * failedCode: OAuth 실패시 code값 (801)\n  * refreshToken: 인증이 완료된 회원의 정보를 포함한 JwtToken (String)"
    )
    @GetMapping("/oauth2/code/{provider}/dev")
    public String oauthLoginSuccessDev(HttpServletRequest request, @PathVariable(value = "provider", required = true) String provider,
                                       @RequestParam(value = "code", required = true) String code) {
        //log.info("########## oauthLoginSuccess : {}", provider);
        WebRedirectOAuthSuceessInfo oAuthSuceessInfo = null;
        switch (provider) {
            case "kakao":
                oAuthSuceessInfo = kakaoOAuthManager.handleOAuthCode(request, code, "dev");
                break;

            case "apple":
                //oAuthSuceessInfo = appleOAuthManager.handleOAuthCode(request, code);
                break;

            default:
                throw new ClientRequestInputInvalidException(ServiceException.ErrorType.ClientSystem, ServiceException.ErrorTarget.OAuth, null);
        }

        // OAuth success
        return oAuthSuceessInfo.getResultRedirectUrl();
    }

}
