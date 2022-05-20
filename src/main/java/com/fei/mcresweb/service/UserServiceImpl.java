package com.fei.mcresweb.service;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.dao.User;
import com.fei.mcresweb.dao.UserDao;
import com.fei.mcresweb.restservice.user.LoginInfo;
import com.fei.mcresweb.restservice.user.RegisterInfo;
import lombok.NonNull;
import lombok.val;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用户服务
 *
 * @author yuanlu
 */
@Service
public class UserServiceImpl implements UserService {
    final UserDao repo;

    public UserServiceImpl(UserDao repo) {
        this.repo = repo;
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
}
