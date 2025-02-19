package com.peloton.boilerplate.Interceptor;

import com.peloton.boilerplate.auth.AuthService;
import com.peloton.boilerplate.util.WebLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class HttpInterceptor implements HandlerInterceptor {

    @Autowired
    AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        final String uriPath = request.getRequestURI();
        if ( WebLogUtils.isServicePath(uriPath) ) {
            WebLogUtils.setThreadLocalsFromHttpHeader(request);

            if (request.getInputStream() == null || request	.getInputStream().available() == 0) {
                WebLogUtils.writeAccessRequestLog(request, true, null);
            }

            // Authentication ( JWT 유효성 Check )
            authService.authAndCheckPermission(request);
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception { WebLogUtils.removeAllThreadLocals(); }

}
