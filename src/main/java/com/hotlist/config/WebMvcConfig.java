package com.hotlist.config;

import com.hotlist.interceptor.LoginCheckInterceptor;
import com.hotlist.interceptor.PreviewInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LoginCheckInterceptor loginInterceptor;

    @Resource
    private PreviewInterceptor previewInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/u/mySiteCard")
                .excludePathPatterns("/u/info")
                .addPathPatterns("/u/**");
        registry.addInterceptor(previewInterceptor).addPathPatterns("/u/mySiteCard", "/u/info", "/s/**");
    }
}
