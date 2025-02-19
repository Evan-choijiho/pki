package com.peloton.boilerplate.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.regex.Pattern;

@Getter
@Setter
public class PathPermissionList {
    private List<PathPermission> list;
}

@Getter
@Setter
class PathPermission {
    public static enum Role {
        VISITOR, ADMIN, USER, WEB
    }

    @JsonIgnore
    private Pattern pathPattern;

    public String getPath() {
        return this.pathPattern.toString();
    }

    public void setPath(String path) {
        this.pathPattern = Pattern.compile(path);
    }

    private String method;
    private Role role;

}
