package org.springframework.web.servlet;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleHandlerInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(request.getMethod());
        sb.append("] [");
        sb.append(request.getRequestURI());
        sb.append("] ");
        Enumeration<String> e = request.getParameterNames();
        boolean flag = false;
        while (e.hasMoreElements()) {
            if (flag) {
                sb.append(", ");
            } else {
                flag = true;
            }
            String name = e.nextElement();
            String[] values = request.getParameterValues(name);
            if (1 == values.length) {
                sb.append(name).append("=").append(values[0]);
            } else {
                sb.append(name).append("[]={");
                for (int i = 0; i < values.length; ++i) {
                    if (0 < i) {
                        sb.append(",");
                    }
                    sb.append(values[i]);
                }
                sb.append("}");
            }
        }
        LOG.info(sb.toString());
        return true;
    }

    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
    }

    private static final Logger LOG
            = LoggerFactory.getLogger(ConsoleHandlerInterceptor.class);
}
