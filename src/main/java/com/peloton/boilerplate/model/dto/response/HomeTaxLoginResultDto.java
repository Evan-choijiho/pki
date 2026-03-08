package com.peloton.boilerplate.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "홈택스 공인인증서 로그인 결과")
public class HomeTaxLoginResultDto {

    @Schema(description = "로그인 성공 여부")
    private boolean success;

    @Schema(description = "에러 메시지 (실패 시)")
    private String errMsg;

    @Schema(description = "사업자번호/세무번호 (성공 시)")
    private String tin;

    @Schema(description = "시스템 코드")
    private String sysCode;
}
