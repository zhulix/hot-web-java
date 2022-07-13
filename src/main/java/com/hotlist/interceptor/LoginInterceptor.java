package com.hotlist.interceptor;

import com.hotlist.utils.HotCookie;
import com.hotlist.utils.HotSpringBeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Resource
    private HotCookie hotCookie;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorization = request.getHeader("Authorization");
        try {
            hotCookie.validate(authorization.split(" ")[1]);
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }

        return true;
    }
}
