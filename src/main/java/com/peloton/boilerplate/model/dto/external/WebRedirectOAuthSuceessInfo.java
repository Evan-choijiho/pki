package com.peloton.boilerplate.model.dto.external;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class WebRedirectOAuthSuceessInfo implements WebRedirectResponse {
    private String resultRedirectUri;

    private Boolean success;

    private String memberInfoToken;

    private Boolean existence;

    private Integer failedCode;

    @Override
    public String getResultRedirectUrl() {
        final String returnRedirectUri = String.format("%s?success=%s&existence=%s&failed-code=%d&member-info-token=%s", resultRedirectUri, success, existence,
                failedCode, memberInfoToken);

        return "redirect:" + returnRedirectUri;
    }
}
