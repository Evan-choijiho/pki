package com.peloton.boilerplate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "Test", description = "Test APIs - 테스트")
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Operation(summary = "test 조회", description = "test 조회")
    @RequestMapping(value = "/get_api", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public String test() {
        return "API 통신 성공";
    }

}
