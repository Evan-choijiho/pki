package com.peloton.boilerplate.model.dto.external;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({ "iat", "exp" })
@Schema(description = "회원 정보 DTO - 회원 가입 정보 기입용")
public class MemberInfoDto implements Serializable {

    @Schema(description = "출생년도 (4자리: yyyy)", required = false, example = "1965")
    private Integer birthYear;

    @Schema(description = "성별 (1:남자, 2:여자)", required = false, example = "1")
    private Integer gender;

    @Schema(description = "카카오 연동 여부", required = false, example = "true")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean kakaoYn;

    @Schema(description = "카카오 연동 email", required = false, example = "toriedufin@kakao.com")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;

    @Schema(description = "회원 닉네임", required = false, example = "정희")
    private String nickname;

    @Schema(description = "마케팅수신 동의 여부", required = false, example = "true")
    private Boolean marketingTermsYn;

    @JsonIgnore
    public static MemberInfoDto from(Map<String, Object> map) {
        return MemberInfoDto.builder()
                .birthYear((Integer) map.get("birthYear"))
                .gender((Integer) map.get("gender"))
                .kakaoYn((Boolean) map.get("kakaoYn"))
                .email((String) map.get("email"))
                .nickname((String) map.get("nickname"))
                .marketingTermsYn((Boolean) map.get("marketingTermsYn"))
                .build();
    }

    @JsonIgnore
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
        return (new ObjectMapper()).convertValue(this, Map.class);
    }

}
