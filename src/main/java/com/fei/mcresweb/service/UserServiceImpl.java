package com.fei.mcresweb.service;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.dao.UserDao;
import com.fei.mcresweb.defs.ConfType;
import com.fei.mcresweb.defs.ConfigManager;
import com.fei.mcresweb.defs.CookiesManager;
import com.fei.mcresweb.restservice.user.LoginInfo;
import com.fei.mcresweb.restservice.user.MyUserInfo;
import com.fei.mcresweb.restservice.user.OtherUserInfo;
import com.fei.mcresweb.restservice.user.RegisterInfo;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * 用户服务
 *
 * @author yuanlu
 */
@Service
public class UserServiceImpl implements UserService {
    /**
     * 用户DAO
     */
    final UserDao repo;
    /**
     * 配置文件
     */
    final ConfigManager configManager;
    /**
     * cookie管理器
     */
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
        this.repo = repo;
        this.configManager = configManager;

        jwtKey = Keys.hmacShaKeyFor(configManager.getOrSummon(ConfType.JWT_KEY, true));
        jwtParser = Jwts.parserBuilder().setSigningKey(jwtKey).build();
        this.cookiesManager = cookiesManager;
    }

    private static final String wrongUP = "错误的用户名密码";
    private static final String wrongTime = "登录请求失效";

    @Override
    public LoginInfo login(@NonNull loginReq req) {
        val range = configManager.getOrSummon(ConfType.LOGIN_TIME_RANGE, true);
        if (Math.abs(req.time() - System.currentTimeMillis()) > range) {
            //TODO 登录时间戳判断, 暂不启用
            // return LoginInfo.byErr(wrongTime);
        }
        val user = repo.findByUsername(req.username());
        if (user == null)
            return LoginInfo.byErr(wrongUP);
        val pwd = hashString(user.getPassword(), req.time());
        val success = pwd != null && pwd.equalsIgnoreCase(req.password());
        if (!success)
            return LoginInfo.byErr(wrongUP);
        return user.toLoginInfo();
    }

    @Override
    public RegisterInfo register(@NonNull registerReq req) {
        User user = new User();
        user.setUsername(req.username());
        user.setPassword(req.password());
        user.setEmail(req.email());
        user = repo.save(user);
        return user.toRegisterInfo();
    }

    /**
     * 列出他人信息
     *
     * @param id 用户ID
     * @return 信息
     */
    @Override
    public OtherUserInfo infoOther(int id) {
        val user = repo.findById(id);
        return user.map(OtherUserInfo::fromDatabase).orElse(null);
    }

    /**
     * 列出个人信息
     *
     * @param id 用户ID
     * @return 信息
     */
    @Override
    public MyUserInfo infoMe(int id) {
        val user = repo.findById(id);
        return user.map(MyUserInfo::fromDatabase).orElse(null);
    }

    private static String hashString(String string) {
        try {
            val md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes());
            md.digest();
            return Tool.byte2hex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String hashString(String str1, Object str2) {
        return hashString(str1 + str2);
    }

    @Override
    public @NonNull String summonToken(int user) {
        val exp = configManager.getOrSummon(ConfType.LOGIN_EXP, true);
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
        cook.setMaxAge((int)(configManager.getOrSummon(ConfType.LOGIN_EXP, true) / 1000));
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
        for (val cook : req.getCookies()) {
            if (key.equalsIgnoreCase(cook.getName())) {
                return checkToken(cook.getValue());
            }
        }
        return null;
    }

}
