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
@Schema(description = "회원 가입 Request DTO")
public class SignUpDto {
    @Schema(description = "이름", required = false, example = "홍길동")
    private String name;
    @Schema(description = "사용자 id", required = false, example = "hansener@naver.com")
    private String userId;
    @Schema(description = "비밀번호", required = false, example = "hansener@naver.com")
    private String password;
    @Schema(description = "전화번호", required = false, example = "010-1234-5678")
    private String phone;
}
