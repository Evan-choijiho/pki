package com.peloton.boilerplate.service.common;

import com.peloton.boilerplate.util.WebLogUtils;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class LoginUserAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        String userSid = "";

        if ( WebLogUtils.getAuthUserSid() != null ) {
            userSid = WebLogUtils.getAuthUserSid().toString();
        } else {
            userSid = "System";
        }
        return Optional.of(userSid);
    }
}
