package com.peloton.boilerplate.external.kakao;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoMessageResponse {
    private String code;
    private ResultData data;
    private String message;

    @JsonIgnore
    public boolean isSuccess() {
        return code != null && code.equals("success");
    }

    @JsonIgnore
    public String getGlobalCellNumber() {
        return (data != null && data.getPhn() != null) ? data.getPhn() : null;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultData {
        private String phn;
        private String msgid;
        private String type;
    }
}
