package com.fei.mcresweb.service;

import com.fei.mcresweb.Tool;
import com.fei.mcresweb.WaitMaintain;
import com.fei.mcresweb.defs.ConfigManager;
import com.fei.mcresweb.defs.Configs;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮件服务
 */
@Service
public class MailServiceImpl implements MailService {

    private final Conf conf;
    private final Session session;
    private final InternetAddress from;
    private final ConfigManager configManager;

    public MailServiceImpl(Conf conf, ConfigManager configManager) throws AddressException {
        this.conf = conf;
        this.from = new InternetAddress(conf.from);
        this.configManager = configManager;

        //create session
        Properties props = new Properties();
        props.put("mail." + conf.type + ".host", conf.host+":"+conf.port);
        props.put("mail.debug", conf.debug);
        session = Session.getInstance(props);
    }

    @Override
    public boolean send(@NotNull String to, @NonNull String subject, @NonNull String content) {
        try (Transport bus = session.getTransport(conf.type)) {
            bus.connect(conf.host,conf.port,conf.user,conf.passwd);

            Message msg = new MimeMessage(session);

            msg.setFrom(from);
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);

            msg.setSubject(subject);
            msg.setSentDate(new Date());
            msg.setContent(content, "text/html;charset=UTF-8");

            msg.saveChanges();
            bus.sendMessage(msg, address);
        } catch (Throwable e) {
            if (conf.debug) e.printStackTrace();
            return false;
        }
        return true;
    }

    private final Map<String,String> registerCode=new ConcurrentHashMap<>();
    private  final Random random=new SecureRandom();
    @Override
    public boolean sendRegisterCode(@NonNull String to,@NonNull String username, @NonNull Locale locale) {
        val content = configManager.getOrSummon(Configs.MAIL_REGCODE_CONTENT.getValue(locale), true);
        val code=Tool.randomNumber(random,6);
        WaitMaintain.put(registerCode,to,code,1000,null);
        return send(to,"标题",content.formatted(username,code));
    }

    @Component
    @Data
    @ConfigurationProperties(prefix = "mrw.mail")
    public static final class Conf {
        boolean enabled;
        String host;
        int port;
        String from;
        String user;
        String passwd;
        String type;
        boolean debug;
    }
}
