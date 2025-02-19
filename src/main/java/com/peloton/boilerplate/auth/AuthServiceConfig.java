package com.peloton.boilerplate.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peloton.boilerplate.exception.NoAuthenticationDefinitionException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AuthServiceConfig {
    private PathPermissionList pathPermissionList = null;

    @PostConstruct
    public void init() {
        try {
            this.pathPermissionList = new ObjectMapper().readValue(//
                    (new ClassPathResource("auth/request-role.json")).getInputStream(), PathPermissionList.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public PathPermission.Role getPathRole(String path, String method) {
        for (PathPermission pathPermission : pathPermissionList.getList()) {
            if (pathPermission	.getPathPattern()
                    .matcher(path)
                    .matches()) {
                if (pathPermission	.getMethod()
                        .equals("ALL")) {// mathod is 'ALL'
                    return pathPermission.getRole();
                } else {
                    if (pathPermission	.getMethod()
                            .equals(method)) { // mathod is matched
                        return pathPermission.getRole();
                    }
                }
            }
        }
        throw new NoAuthenticationDefinitionException(path, method, null);
    }
}
