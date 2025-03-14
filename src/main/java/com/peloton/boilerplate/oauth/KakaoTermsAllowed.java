package com.peloton.boilerplate.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
public class KakaoTermsAllowed {
    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("allowed_service_terms")
    private List<AllowedServiceTerms> allowedServiceTermsList;

    @Getter
    @ToString
    public static class AllowedServiceTerms {
        @JsonProperty("tag")
        private String tag;

        @JsonProperty("agreed_at")
        private LocalDateTime aggreedAt;
    }
}
