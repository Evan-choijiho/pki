package com.peloton.boilerplate.model.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KakaoAlimtalkDto {
    @Schema(description = "사용자명", required = false, example = "지훈")
    private String userName;

    @Schema(description = "전화번호", required = false, example = "01012345678")
    private String globalPhoneNumber;

    @Schema(name = "사용자 생성 시간", type = "java.lang.String", nullable = true, required = false, example = "2022-03-14 20:38:11")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected LocalDateTime userInsertTime;

    @Schema(description = "치과명", required = false, example = "잠실연세치과")
    private String hospitalName;

    @Schema(name = "인증서 발급 신청 일", type = "java.lang.String", nullable = true, required = false, example = "2022-03-14 20:38:11")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected LocalDateTime regInsertTime;

    @Schema(name = "인증서 발급일", type = "java.lang.String", nullable = true, required = false, example = "2022-03-14 20:38:11")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    protected LocalDateTime issueInsertTime;
}
