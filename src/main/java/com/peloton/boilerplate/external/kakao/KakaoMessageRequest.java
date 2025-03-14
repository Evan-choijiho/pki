package com.peloton.boilerplate.external.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class KakaoMessageRequest {
    // @JsonProperty("msgid")
    // private String msgId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + RandomStringUtils.random(6, "0123456789");

    @JsonProperty("message_type")
    private String type;

    @JsonProperty("profile")
    private String profile;

    @JsonProperty("tmplId")
    private String tmplId;

    @JsonProperty("phn")
    private String phn;

    @JsonProperty("reserveDt")
    private String reserveDt = "00000000000000";

    @JsonProperty("msg")
    private String msg;

    private Map<String, String> button1 = null;
    private Map<String, String> button2 = null;

    @SuppressWarnings("unchecked")
    public KakaoMessageRequest(KakaoMessage.Type type, String profile, String phone, String msg, String tmplId, Map<String, Object> dataMap) {
        this.type = type.toString();
        this.phn = phone;
        this.profile = profile;
        this.msg = msg;
        this.tmplId = tmplId;

        if (dataMap == null) {
            dataMap = new HashMap<>();
        }
        this.button1 = (Map<String, String>) dataMap.getOrDefault("button1", null);
        this.button2 = (Map<String, String>) dataMap.getOrDefault("button2", null);
    }
}
