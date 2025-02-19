package com.peloton.boilerplate.Interceptor;

import com.peloton.boilerplate.auth.AuthService;
import com.peloton.boilerplate.util.WebLogUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class HttpInterceptor implements HandlerInterceptor {

    @Autowired
    AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {//        final String uriPath = request.getRequestURI();

        //System.out.println("######### HttpInterceptor preHandle");

        final String uriPath = request.getRequestURI();
        if (WebLogUtils.isServicePath(uriPath)) {   // service url filtering ( /api/project명/* )
            WebLogUtils.setThreadLocalsFromHttpHeader(request);

            if (request.getInputStream() == null || request	.getInputStream()
                    .available() == 0) {    // Body = empty
                WebLogUtils.writeAccessRequestLog(request, true, null);
            }

            // Authentication
            //authService.authAndCheckPermission(request);
        }
        return true;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        WebLogUtils.removeAllThreadLocals();
    }

}
