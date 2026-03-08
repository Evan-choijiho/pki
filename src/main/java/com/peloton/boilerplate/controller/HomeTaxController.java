package com.peloton.boilerplate.controller;

import com.peloton.boilerplate.model.dto.response.HomeTaxLoginResultDto;
import com.peloton.boilerplate.service.HomeTaxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "HomeTax", description = "홈택스 공인인증서 로그인 API")
@RestController
@RequestMapping("/api/hometax")
public class HomeTaxController {

    @Autowired
    private HomeTaxService homeTaxService;

    @Operation(summary = "홈택스 공인인증서 로그인", description = "설정된 인증서 폴더(signPri.key, signCert.der)로 홈택스 로그인 수행")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public HomeTaxLoginResultDto login() {
        return homeTaxService.loginWithCert();
    }
}
