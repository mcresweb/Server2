package com.fei.mcresweb.service;

import lombok.val;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Locale;

/**
 * 初始化服务
 */
@Service
public class InitServiceImpl implements InitService {
    public static final Path file = Path.of("runtime", "init-code.txt");
    /**
     * id长度(字节)
     */
    public static final int idLen = 64;

    @Override
    public String summonID() {
        try {
            Files.createDirectories(file.getParent());
            try (val out = Files.newBufferedWriter(file)) {

                val buf = new byte[idLen];
                new SecureRandom().nextBytes(buf);
                val str = new BigInteger(1, buf).toString(Character.MAX_RADIX).toUpperCase(Locale.ENGLISH);

                out.write(str);
            }
        } catch (IOException e) {
            return e.toString();
        }
        return file.toString();
    }

    @Override
    public boolean removeID() {
        try {
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean checkID(String id) {
        try {
            if (Files.notExists(file))
                return false;
            try (val in = Files.newBufferedReader(file)) {
                return id.equalsIgnoreCase(in.readLine());
            }
        } catch (IOException e) {
            return false;
        }
    }
}
