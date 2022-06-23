package com.fei.mcresweb.controller;

import com.fei.mcresweb.WaitMaintain;
import com.fei.mcresweb.config.CallCooling;
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
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class GlobalHandler implements HandlerInterceptor {

    /**
     * 冷却毫秒数的head key
     */
    public static final String COOLING_HEADER = "X-API-COOLING";
    private final AtomicLong httpCount = new AtomicLong();
    /**
     * 最后一次调用时间
     * 方法 - 用户 - 时间戳
     */
    private final Map<Method, ArrayList<Object>> lstCall = new HashMap<>();
    private final UserService userService;
    private long httpCountLastMod;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public GlobalHandler(UserService userService) {
        this.userService = userService;
    }

    /**
     * @return 今日http访问统计
     */
    public long getHttpCount() {
        return httpCount.get();
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest req, @NotNull HttpServletResponse resp,
        @NotNull Object handler) throws IOException {
        if (handler instanceof HandlerMethod handlerMethod) {

            val method = handlerMethod.getMethod();
            val userAuth = method.getAnnotation(UserAuth.class);
            val callCooling = method.getAnnotation(CallCooling.class);

            if (userAuth != null && callCooling != null) {
                val id = userService.getUserIdByCookie(req);
                Integer code;

                //auth
                code = authUser(id, userAuth);
                if (code != null) {
                    writeResp(req, resp, code, userAuth.value().msgPath);
                    return false;
                }

                // cool down
                code = checkCooling(method, id, req.getRemoteAddr(), callCooling);
                if (code == null) {
                    resp.setHeader(COOLING_HEADER, Long.toString(callCooling.value()));
                } else {
                    writeResp(req, resp, code, CallCooling.UnlimitedType.msgPath,
                        decimalFormat.format(callCooling.value() / 1000F));
                    return false;
                }
            }
        }
        httpCount();
        return true;
    }

    private @Nullable Integer checkCooling(@NotNull Method method, @Nullable Integer id, @NotNull String remoteAddr,
        @NotNull CallCooling callCooling) {
        val unlimited = switch (callCooling.unlimited()) {
            case LOGIN -> userService.isUser(id);
            case VIP -> userService.isVip(id);
            case ADMIN -> userService.isAdmin(id);
            default -> false;
        };
        if (unlimited)
            return null;
        val flag = callCooling.useIp() || id == null ? remoteAddr : id;
        synchronized (lstCall) {
            val users = lstCall.get(method);
            if (users != null && users.contains(flag))
                return HttpServletResponse.SC_FORBIDDEN;
            WaitMaintain.add(lstCall, method, flag, callCooling.value(), ArrayList::new, lstCall, null);
        }
        return null;
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

    /**
     * 验证用户权限
     *
     * @param id   用户ID
     * @param auth 验证级别
     * @return 是否通过验证
     */
    private @Nullable Integer authUser(@Nullable Integer id, @Nullable UserAuth auth) {
        if (auth == null)
            return null;
        val code = switch (auth.value()) {
            case LOGIN -> userService.isUser(id) ? null : HttpServletResponse.SC_UNAUTHORIZED;
            case VIP -> userService.isVip(id) ? null : HttpServletResponse.SC_FORBIDDEN;
            case ADMIN -> userService.isAdmin(id) ? null : HttpServletResponse.SC_FORBIDDEN;
        };
        if (id == null && code == null)
            return HttpServletResponse.SC_FORBIDDEN;
        return code;
    }

    private void writeResp(HttpServletRequest req, @NotNull HttpServletResponse resp, int code, String msgPath,
        Object... msgArgs) throws IOException {
        resp.setStatus(code);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain;charset=UTF-8");
        try (val out = resp.getOutputStream()) {
            out.write(I18n.msg(msgPath, I18n.loc(req), msgArgs).getBytes(StandardCharsets.UTF_8));
        }
    }
}
