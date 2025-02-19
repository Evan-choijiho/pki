package com.peloton.boilerplate.model.dto.response;

import com.peloton.boilerplate.model.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AuthToken 정보 DTO")
public class AuthTokenDto {
    @Schema(name = "회원 refresh token", required = false, example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(name = "회원 access token", required = false, example = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(name = "사용자 SID", required = false, example = "200000")
    private Long userSid;

    @Schema(name = "사용자 ID", required = false, example = "hansener")
    private String userId;

    @Schema(name = "사용자명", required = false, example = "서지훈")
    private String userName;

    @Schema(name = "회사 SID", required = false, example = "200000")
    private Long companySid;

    @Schema(name = "회사 명", required = false, example = "peloton")
    private String companyName;

    @Schema(name = "그룹 명", required = false, example = "IT본부")
    private String groupName;

    @Schema(name = "최초 로그인 여부", required = false, example = "true/false")
    private Boolean firstLogin;

//    @Schema(name = "로그인 권한", required = false, example = "admin/normal")
//    private User.Grant grant;
}
