package com.peloton.boilerplate.controller;

import com.peloton.boilerplate.model.dto.request.SignUpDto;
import com.peloton.boilerplate.model.dto.response.AuthTokenDto;
import com.peloton.boilerplate.model.dto.response.UserDto;
import com.peloton.boilerplate.service.AccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Access", description = "Access APIs - 서비스 접근 관리")
@RestController
@RequestMapping("/api/access")
public class AccessController {

    @Autowired
    AccessService accessService;

    @Operation(summary = "Hello API", description = "helloworld 문자열을 반환합니다.")
    @GetMapping("/hello")
    @ResponseStatus(value = HttpStatus.OK)
    public String hello() {
        return "helloworld";
    }

    @Operation(
            summary = "사용자 상세 정보 조회 (by sid)", description = "사용자 상세 정보 조회 (by sid)",
            parameters = { @Parameter(name = "X-Member-Access-Token", description = "사용자 AccessToken - 로그인 시 발급",
                    required = false, in = ParameterIn.HEADER, example = "elk14lvke..." ) } )
    @RequestMapping(value = "/user/{user_sid}", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public UserDto getUserBySid( @Parameter(description = "user sid", required = true) @PathVariable(value = "user_sid", required = true) Long userSid) {
        UserDto userDto = accessService.getUserBySid(userSid);
        return userDto;
    }

    @Operation(
            summary = "회원 가입", description = "회원 가입",
            parameters = { @Parameter(name = "X-Member-Access-Token", description = "사용자 AccessToken - 로그인 시 발급",
                    required = false, in = ParameterIn.HEADER, example = "elk14lvke..." ) } )
    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public AuthTokenDto registUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody( description = "사용자 등록 요청 정보", required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = SignUpDto.class)))
            @Valid @RequestBody SignUpDto signUpDto )
    {
        AuthTokenDto accessTokenDto = accessService.registUser(signUpDto);
        return accessTokenDto;
    }


}
