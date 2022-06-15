package com.fei.mcresweb.config;

import com.fei.mcresweb.controller.GlobalHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    public WebConfig(GlobalHandler globalHandler) {
        this.globalHandler = globalHandler;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalHandler).addPathPatterns("/**");
    }

    private final GlobalHandler globalHandler;
}
