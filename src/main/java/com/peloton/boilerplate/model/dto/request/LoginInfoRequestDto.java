package com.peloton.boilerplate.model.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "로그인 정보 Request DTO")
public class LoginInfoRequestDto {

    @Schema(name = "사용자 id", required = false, example = "hansener@naver.com")
    private String userId;

    @Schema(name = "비밀번호", required = false, example = "qwer1234")
    private String password;

}
