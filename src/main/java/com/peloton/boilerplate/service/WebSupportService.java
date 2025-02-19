package com.peloton.boilerplate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class WebSupportService {
    @Autowired
    Environment env;

    // Profile
    public String getActiveProfileName() {
        for (String profile : env.getActiveProfiles()) {
            return profile;
        }
        return null;
    }

    public boolean isProduction() {
        final String activeProfile = getActiveProfileName();
        if (activeProfile != null && activeProfile.toLowerCase().startsWith("prod") ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isStage() {
        final String activeProfile = getActiveProfileName();
        if (activeProfile != null && activeProfile.toLowerCase().startsWith("stg") ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isLocal() {
        final String activeProfile = getActiveProfileName();
        if (activeProfile != null && activeProfile.toLowerCase().startsWith("dev") ) {
            return true;
        } else {
            return false;
        }
    }

}
