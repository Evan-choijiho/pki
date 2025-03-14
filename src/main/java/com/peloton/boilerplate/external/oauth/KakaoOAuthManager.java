package com.peloton.boilerplate.external.oauth;

import com.peloton.boilerplate.exception.EntityNotFoundException;
import com.peloton.boilerplate.exception.OAuthFailedException;
import com.peloton.boilerplate.model.dto.external.MemberInfoDto;
import com.peloton.boilerplate.model.dto.external.WebRedirectOAuthSuceessInfo;
import com.peloton.boilerplate.oauth.KakaoProfile;
import com.peloton.boilerplate.oauth.KakaoTermsAllowed;
import com.peloton.boilerplate.oauth.OAuthToken;
import com.peloton.boilerplate.service.common.CommonServiceManager;
import com.peloton.boilerplate.service.common.WebSupportService;
import com.peloton.boilerplate.util.OAuthUtils;
import com.peloton.boilerplate.util.UserAuthUtils;
import com.peloton.boilerplate.util.WebLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KakaoOAuthManager {
    @Autowired
    WebSupportService webSupportService;
    @Autowired
    CommonServiceManager cmManager;
    @Value("${oauth2.kakao.client-id}")
    private String oauthClientId;

    private static final String oauthCodeRedirectUri = "/api/implant/web/oauth2/code/kakao";
    private static final String oauthCodeRedirectUriDev = "/api/implant/web/oauth2/code/kakao/dev";
    private static final String oauthResultRedirectUriProd = "https://i-mplant.co.kr/login_success";
    //    private static final String oauthResultRedirectUriProd = "http://localhost:15401/login_success";
    private static final String oauthResultRedirectUriDev = "http://localhost:15401/login_success";

    public WebRedirectOAuthSuceessInfo handleOAuthCode(HttpServletRequest request, String code, String type) {
        Boolean success = false;
        String memberInfoToken = null;
        Boolean existence = false;
        Integer failedCode = null;

        try {
            String redirectUri = WebLogUtils.getRequestBaseUrl(request) + (type.equals("prod") ? oauthCodeRedirectUri:oauthCodeRedirectUriDev);

            if (webSupportService.isProduction() || webSupportService.isStage()) {
                // 개발서버에 ssl 적용 후 아래 주석 해제
                redirectUri = redirectUri.replace("http:", "https:");
            }

            OAuthToken kakaoOauthToken = getKakaoToken(oauthClientId, redirectUri, code);
            KakaoProfile kakaoUserProfile = getKakaoMemberProfile(kakaoOauthToken);

            // Parsing Kakao Member Profile

            // 이름
            String name = null;
            try {
                name = kakaoUserProfile	.getKakaoAccount()
                        .getName();
            } catch (Exception e) {
                // 필수항목
                name = null;
                throw new OAuthFailedException(e);
            }

            // 전화번호
            String phoneNumber = null;
            try {
                phoneNumber = kakaoUserProfile	.getKakaoAccount()
                        .getPhoneNumber();
                if (!phoneNumber.startsWith("+82")) {
                    throw new OAuthFailedException(null);
                }

                // 전화번호 변환 '+82 10-1234-5678' -> '01012345678'
                phoneNumber = phoneNumber.replace("+82 10", "010");
                phoneNumber = phoneNumber.replaceAll("-", "");
            } catch (Exception e) {
                // 필수항목
                phoneNumber = null;
                throw new OAuthFailedException(e);
            }

            // 출생년도
            Integer birthYear = null;
            try {
                birthYear = Integer.parseInt(kakaoUserProfile	.getKakaoAccount()
                        .getBirthyear());
            } catch (Exception e) {
                // 필수항목
                birthYear = null;
                //throw new OAuthFailedException(e);
            }

            // 생일
            Integer birthDay = null;
            try {
                birthDay = Integer.parseInt(kakaoUserProfile	.getKakaoAccount()
                        .getBirthday());

            } catch (Exception e) {
                // 필수항목
                birthDay = null;
                //throw new OAuthFailedException(e);
            }


            // 마케팅정보 수신 동의 - 카카오 싱크 체크
//            Boolean marketingTermsYn = false;
//            KakaoTermsAllowed termsAllowed = this.getKakaoTermsAllowed(kakaoOauthToken);
//            if (termsAllowed != null && termsAllowed.getAllowedServiceTermsList() != null) {
//                for (KakaoTermsAllowed.AllowedServiceTerms allowedServiceTerms : termsAllowed.getAllowedServiceTermsList()) {
//                    if (allowedServiceTerms.getTag() != null && allowedServiceTerms	.getTag()
//                            .equals("marketing_20220628")) { // '마케팅정보 수신 동의' 약관 동의 여부 확인
//                        if (allowedServiceTerms.getAggreedAt() != null) {
//                            marketingTermsYn = true;
//                        }
//                    }
//                }
//            }

            // 기존 회원 존재여부 체크
            try {
                cmManager.getUserByPhoneNumber(phoneNumber);

                // exists
                existence = true;
                System.out.println("#### existence ok");
            } catch (EntityNotFoundException e) {
                // not exists
                existence = false;
            }

            memberInfoToken = UserAuthUtils.createMemberInfoToken(MemberInfoDto.builder()
                    .name(name)
                    .phoneNumber(phoneNumber)
                    .birthYear(birthYear)
                    .birthDay(birthDay)
                    //.marketingTermsYn(marketingTermsYn)
                    .build());
            success = true;
            failedCode = OAuthUtils.OAUTH_SUCCESS_CODE;
            System.out.println("#### memberInfoToken : " + memberInfoToken);
        } catch (Exception e) {
            // debug
            e.printStackTrace();

            success = false;
            failedCode = OAuthUtils.OAUTH_FAILED_CODE;
        }
        final String oauthResultRedirectUri = webSupportService.isLocal() ? oauthResultRedirectUriDev : (type.equals("prod") ? oauthResultRedirectUriProd:oauthResultRedirectUriDev);
        //final String oauthResultRedirectUri = webSupportService.isProduction() ? oauthResultRedirectUriProd : oauthResultRedirectUriDev;
        WebRedirectOAuthSuceessInfo oAuthSuceessInfo = WebRedirectOAuthSuceessInfo	.builder()
                .resultRedirectUri(oauthResultRedirectUri)
                .success(success)
                .existence(existence)
                .failedCode(failedCode)
                .memberInfoToken(memberInfoToken)
                .build();

        return oAuthSuceessInfo;
    }

    private OAuthToken getKakaoToken(String clientId, String redirectUri, String code) {
        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("grant_type", "authorization_code"); // 고정값
        bodyParams.add("client_id", clientId);
        bodyParams.add("redirect_uri", redirectUri);
        bodyParams.add("code", code);

        HttpHeaders header = new HttpHeaders();
        header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyParams, header);

        // Send Http Request
        try {
            ResponseEntity<OAuthToken> response = (new RestTemplate()).exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, request,
                    OAuthToken.class);
            return response.getBody();
        } catch (Exception e) {
            throw new OAuthFailedException(e);
        }
    }

    private OAuthToken getKakaoTokenRefresh(String clientId, String refreshToken) {
        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("grant_type", "refresh_token"); // 고정값
        bodyParams.add("client_id", clientId);
        bodyParams.add("refresh_token", refreshToken);

        HttpHeaders header = new HttpHeaders();
        header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(bodyParams, header);

        // Send Http Request
        try {
            ResponseEntity<OAuthToken> response = (new RestTemplate()).exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST, request,
                    OAuthToken.class);
            return response.getBody();
        } catch (Exception e) {
            throw new OAuthFailedException(e);
        }
    }

    private KakaoProfile getKakaoMemberProfile(OAuthToken authToken) {
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + authToken.getAccessToken());
        header.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(header);

        try {
            // Send Http Request
            ResponseEntity<KakaoProfile> response = new RestTemplate().exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST, request,
                    KakaoProfile.class);
            return response.getBody();
        } catch (Exception e) {
            throw new OAuthFailedException(e);
        }
    }

    private KakaoTermsAllowed getKakaoTermsAllowed(OAuthToken authToken) {
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + authToken.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(header);

        try {
            // Send Http Request
            ResponseEntity<KakaoTermsAllowed> response = new RestTemplate().exchange("https://kapi.kakao.com/v1/user/service/terms", HttpMethod.GET, request,
                    KakaoTermsAllowed.class);
            return response.getBody();
        } catch (Exception e) {
            return null;
        }
    }
}
