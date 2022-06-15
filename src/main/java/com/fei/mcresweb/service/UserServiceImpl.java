package com.fei.mcresweb.service;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.config.I18n;
import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.dao.UserDao;
import com.fei.mcresweb.defs.ConfigManager;
import com.fei.mcresweb.defs.Configs;
import com.fei.mcresweb.defs.CookiesManager;
import com.fei.mcresweb.restservice.user.*;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;

/**
 * 用户服务
 *
 * @author yuanlu
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final ConfigManager configManager;
    private final CookiesManager cookiesManager;
    /**
     * jwt加密密钥
     */
    private final SecretKey jwtKey;
    /**
     * jwt解析器
     */
    private final JwtParser jwtParser;

    public UserServiceImpl(UserDao repo, ConfigManager configManager, CookiesManager cookiesManager) {
        this.userDao = repo;
        this.configManager = configManager;

        jwtKey = Keys.hmacShaKeyFor(configManager.getOrSummon(Configs.JWT_KEY, true));
        jwtParser = Jwts.parserBuilder().setSigningKey(jwtKey).build();
        this.cookiesManager = cookiesManager;
    }

    private static @Nullable String hashString(@NotNull String str1, @NotNull Object str2) {
        try {
            val md = MessageDigest.getInstance("SHA-256");
            md.update(str1.getBytes());
            md.update(String.valueOf(str2).getBytes());
            return Tool.byte2hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public LoginInfo login(@NotNull Locale locale, @NonNull loginReq req) {
        val range = configManager.getOrSummon(Configs.LOGIN_TIME_RANGE, true);
        if (Math.abs(req.time() - System.currentTimeMillis()) > range) {
            return LoginInfo.byErr(I18n.msg("user.wrong.time", locale));
        }
        val user = userDao.findByUsername(req.username());
        if (user == null)
            return LoginInfo.byErr(I18n.msg("user.wrong.user-pwd", locale));
        val pwd = hashString(user.getPassword(), req.time());
        val success = pwd != null && pwd.equalsIgnoreCase(req.password());
        if (!success)
            return LoginInfo.byErr(I18n.msg("user.wrong.user-pwd", locale));
        return user.toLoginInfo();
    }

    //    private static final String MSG_USER_EXISTS = "用户名已存在";

    @Override
    public RegisterInfo register(@NotNull Locale locale, @NonNull registerReq req) {

        if (isInvalidUserName(req.username()))
            return RegisterInfo.byErr(I18n.msg("user.invalid-data.username", locale));

        User user = new User();
        user.setUsername(req.username());
        user.setPassword(req.password());
        user.setEmail(req.email());
        user.setAdmin(true);//TODO dev

        synchronized (this) {
            if (userDao.findByUsername(req.username()) != null)
                return RegisterInfo.byErr(I18n.msg("user.exist", locale));

            user = userDao.save(user);
        }
        return user.toRegisterInfo();
    }

    private boolean isInvalidUserName(@NotNull String username) {
        if (username.length() > configManager.getOrSummon(Configs.USERNAME_LENGTH, true))
            return true;
        if (username.length() <= 0)
            return false;
        boolean isNumber = true;
        for (var i = 0; i < username.length(); i++) {
            char c = username.charAt(i);
            if (isNumber && (c < '0' || c > '9'))
                isNumber = false;
            if (c == '@')
                return true;
        }
        return isNumber;
    }

    @Override
    public boolean maybeUserIdString(@NotNull String str) {
        if (str.isEmpty())
            return false;
        for (var i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if ((c < '0' || c > '9'))
                return false;
        }
        return true;
    }

    @Override
    public boolean maybeUserEmailString(@NotNull String str) {
        if (str.length() < 5)//x@x.x
            return false;
        val index = str.indexOf("@", 1);
        if (index < 0)
            return false;
        return str.indexOf(".", index + 2) >= 0;
    }

    /**
     * 列出他人信息
     *
     * @param id 用户ID
     * @return 信息
     */
    @Override
    public OtherUserInfo infoOther(int id) {
        val user = userDao.findById(id);
        return user.map(OtherUserInfo::fromDatabase).orElse(null);
    }

    /**
     * 列出个人信息
     *
     * @param id 用户ID
     * @return 信息
     */
    @Override
    public @NonNull MyUserInfo infoMe(Integer id) {
        if (id == null)
            return MyUserInfo.NOT_LOGIN;
        val user = userDao.findById(id);
        return user.map(MyUserInfo::fromDatabase).orElse(MyUserInfo.NOT_LOGIN);
    }

    @Override
    public @NonNull String summonToken(int user) {
        val exp = configManager.getOrSummon(Configs.LOGIN_EXP, true);
        long time = System.currentTimeMillis();

        return Jwts.builder()//
            .setSubject(Integer.toString(user, Character.MAX_RADIX))//
            .setIssuedAt(new Date(time))//
            .signWith(jwtKey)//
            .setExpiration(new Date(time + exp))//
            .compact();
    }

    @Override
    public @NonNull Cookie summonTokenCookie(int user) {
        val cook = cookiesManager.cookie(CookiesManager.CookiesField.LOGIN, summonToken(user));
        cook.setMaxAge((int)(configManager.getOrSummon(Configs.LOGIN_EXP, true) / 1000));
        return cook;
    }

    public Integer checkToken(String token) {
        if (!Tool.valid(token))
            return null;
        try {
            return Integer.parseInt(jwtParser.parseClaimsJws(token).getBody().getSubject(), Character.MAX_RADIX);
        } catch (JwtException ignore) {
            return null;
        }
    }

    @Override
    public Integer getUserIdByCookie(@NonNull HttpServletRequest req) {
        val key = cookiesManager.key(CookiesManager.CookiesField.LOGIN);
        val cookies = req.getCookies();
        if (cookies == null)
            return null;
        for (val cook : cookies) {
            if (key.equalsIgnoreCase(cook.getName())) {
                return checkToken(cook.getValue());
            }
        }
        return null;
    }

    @Override
    public boolean isAdmin(Integer user) {
        if (user == null)
            return false;
        return userDao.findById(user).map(User::isAdmin).orElse(false);
    }

    @Override
    public void setVaptcha(SetVaptchaReq data) {
        if (data.vid() != null)
            configManager.setConfig(Configs.VAPTCHA_VID, data.vid());
        if (data.key() != null)
            configManager.setConfig(Configs.VAPTCHA_KEY, data.key());
    }

    @Override
    public boolean isVip(Integer user, boolean real) {
        if (user == null)
            return false;
        return userDao.findById(user).map(u -> u.isVip() || (!real && u.isAdmin())).orElse(false);
    }

    @Override
    public VipInfo vipUser(Integer user) {
        if (user == null)
            return null;
        return userDao.findById(user).map(VipInfo::new).orElse(null);
    }

    @Override
    public long countUser() {
        return userDao.count();
    }

    @Override
    public boolean isUser(Integer id) {
        return id != null && userDao.existsById(id);
    }
}
