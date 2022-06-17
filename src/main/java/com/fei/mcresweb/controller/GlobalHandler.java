package com.fei.mcresweb.controller;

import com.fei.mcresweb.config.I18n;
import com.fei.mcresweb.config.UserAuth;
import com.fei.mcresweb.service.UserService;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GlobalHandler implements HandlerInterceptor {
    /**
     * @return 今日http访问统计
     */
    public long getHttpCount() {
        return httpCount.get();
    }

    private final AtomicLong httpCount = new AtomicLong();
    private long httpCountLastMod;

    public GlobalHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest req, @NotNull HttpServletResponse resp,
        @NotNull Object handler) throws IOException {
        if (handler instanceof HandlerMethod handlerMethod) {
            val auth = handlerMethod.getMethod().getAnnotation(UserAuth.class);
            val code = authUser(req, auth);
            if (code != null) {
                resp.setStatus(code);
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/plain;charset=UTF-8");
                try (val out = resp.getOutputStream()) {
                    out.write(I18n.msg(auth.value().msgPath, I18n.loc(req)).getBytes(StandardCharsets.UTF_8));
                }
                return false;
            }
        }
        httpCount();
        return true;
    }

    /**
     * http请求计数
     */
    private void httpCount() {
        val now = System.currentTimeMillis() / 1000 / 60 / 60 / 24;
        if (httpCountLastMod != now) {
            httpCountLastMod = now;
            httpCount.set(1);
        } else
            httpCount.getAndIncrement();
    }

    private final UserService userService;

    /**
     * 验证用户权限
     *
     * @param req  req
     * @param auth 验证级别
     * @return 是否通过验证
     */
    private Integer authUser(@NotNull HttpServletRequest req, @Nullable UserAuth auth) {
        if (auth == null)
            return null;
        val id = userService.getUserIdByCookie(req);
        val code = switch (auth.value()) {
            case LOGIN -> userService.isUser(id) ? null : HttpServletResponse.SC_UNAUTHORIZED;
            case VIP -> userService.isVip(id) ? null : HttpServletResponse.SC_FORBIDDEN;
            case ADMIN -> userService.isAdmin(id) ? null : HttpServletResponse.SC_FORBIDDEN;
        };
        if (id == null && code == null)
            return HttpServletResponse.SC_FORBIDDEN;
        return code;
    }
}
