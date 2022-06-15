package com.fei.mcresweb.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ErrorPageConfig implements ErrorPageRegistrar {
    /**
     * 注册错误页
     *
     * @param registry ErrorPageRegistry
     */
    public void registerErrorPages(ErrorPageRegistry registry) {
        registry.addErrorPages(new ErrorPage("/api/errors"));
    }
}