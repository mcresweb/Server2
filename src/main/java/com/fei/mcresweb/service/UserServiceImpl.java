package com.fei.mcresweb.service;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.dao.UserDao;
import com.fei.mcresweb.defs.ConfType;
import com.fei.mcresweb.defs.ConfigManager;
import com.fei.mcresweb.restservice.user.LoginInfo;
import com.fei.mcresweb.restservice.user.RegisterInfo;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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
     * jwt加密密钥
     */
    private final SecretKey jwtKey;
    /**
     * jwt解析器
     */
    private final JwtParser jwtParser;

    public UserServiceImpl(UserDao repo, ConfigManager configManager) {
        this.repo = repo;
        this.configManager = configManager;

        jwtKey = Keys.hmacShaKeyFor(configManager.getOrSummon(ConfType.JWT_KEY, true));
        jwtParser = Jwts.parserBuilder().setSigningKey(jwtKey).build();
    }

    private static final String wrongUP = "错误的用户名密码";

    @Override
    public LoginInfo login(@NonNull loginReq req) {
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

    public Integer checkToken(String token) {
        if (token == null)
            return null;
        try {
            return Integer.parseInt(jwtParser.parseClaimsJws(token).getBody().getSubject(), Character.MAX_RADIX);
        } catch (JwtException ignore) {
            return null;
        }

    }
}
