package com.peloton.boilerplate.log;


import com.peloton.boilerplate.util.WebLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
public class AccessLog extends UserActivityLog {
    public enum Type {
        REQ, RES
    }

    protected Type type;
    protected String path;
    protected String method;
    protected int status;
    protected int error;
    protected String authToken;
    protected Map<String, String> headers;
    protected Map<String, String> params;
    protected Long rt;
    protected Object body;

    public AccessLog(HttpServletRequest request, Type type, Object body) {
        super("access_log");

        this.type = type;
        this.path = request	.getRequestURI()
                .replaceAll("/+", "/");
        this.method = request.getMethod();
        this.status = 0;
        this.error = 0;
        this.authToken = request.getHeader("X-Auth-Token");
        this.headers = this.getHeaderMap(request);
        this.params = this.getParamMap(request);
        this.rt = WebLogUtils.getTxResponseTime();
        this.body = body;
    }

    public AccessLog(HttpServletRequest request, HttpServletResponse response, Type type, int errorCode, Object body) {
        super("access_log");

        this.type = type;
        this.path = request	.getRequestURI()
                .replaceAll("/+", "/");
        this.method = request.getMethod();
        this.status = response.getStatus();
        this.error = errorCode;
        this.authToken = request.getHeader("X-Auth-Token");
        this.headers = this.getHeaderMap(request);
        this.params = this.getParamMap(request);
        this.rt = WebLogUtils.getTxResponseTime();
        this.body = body;
    }

    protected Map<String, String> getHeaderMap(HttpServletRequest request) {
        return Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(h -> h, request::getHeader));
    }

    protected Map<String, String> getParamMap(HttpServletRequest request) {
        return Collections	.list(request.getParameterNames())
                .stream()
                .collect(Collectors.toMap(p -> p, request::getParameter));
    }
}
