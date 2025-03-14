package com.peloton.boilerplate.external.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class KakaoMessage {
    public enum Type {
        AT, FT
    }

    private String tmplId;
    private String globalCellNumber;
    private String msg;
    private Map<String, Object> dataMap;
}
