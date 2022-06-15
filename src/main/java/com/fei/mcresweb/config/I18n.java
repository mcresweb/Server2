package com.fei.mcresweb.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;

/**
 * 国际化工具箱
 */
@Component
public final class I18n {

    @Value("${spring.messages.basename}")
    private void setBasename(String basename) {
        messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename(basename);
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setDefaultLocale(Locale.CHINA);
        messageSource.setUseCodeAsDefaultMessage(true);
    }

    private static final AcceptHeaderLocaleResolver LOCALE_RESOLVER;
    private static ResourceBundleMessageSource messageSource;

    static {
        LOCALE_RESOLVER = new AcceptHeaderLocaleResolver();
        LOCALE_RESOLVER.setDefaultLocale(Locale.CHINA);
        LOCALE_RESOLVER.setSupportedLocales(Arrays.asList(Locale.ENGLISH, Locale.CHINA));
    }

    public static @NotNull Locale loc(@NotNull HttpServletRequest req) {
        return LOCALE_RESOLVER.resolveLocale(req);
    }

    public static @NotNull String msg(@NotNull String code, @Nullable Locale locale) {
        return messageSource.getMessage(code, null, locale == null ? Locale.getDefault() : locale);
    }

    public static @NotNull String msg(@NotNull String code, @Nullable Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale == null ? Locale.getDefault() : locale);
    }
}
