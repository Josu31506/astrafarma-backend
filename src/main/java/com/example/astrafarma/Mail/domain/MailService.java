package com.example.astrafarma.Mail.domain;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Properties;

@Service
public class MailService {

    @Autowired
    private MailOAuth2Service mailOAuth2Service;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.host}")
    private String mailHost;

    @Value("${spring.mail.port}")
    private int mailPort;

    @Value("${mail.oauth.from}")
    private String mailFrom;

    public void sendWelcomeMail(String to, String name) throws Exception {
        Context context = new Context();
        context.setVariable("name", name);
        String body = templateEngine.process("WelcomeMail.html", context);
        sendMail(to, "Bienvenido a Astrafarma", body, true);
    }

    public void sendConfirmationMail(String to, String name, String link) throws Exception {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("link", link);
        String body = templateEngine.process("ConfirmMail.html", context);
        sendMail(to, "Confirma tu cuenta en Astrafarma", body, true);
    }

    public void sendMail(String to, String subject, String body, boolean isHtml) throws Exception {
        String accessToken = mailOAuth2Service.getAccessToken();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", mailHost);
        props.put("mail.smtp.port", String.valueOf(mailPort));
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailFrom, accessToken);
            }
        });

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mailFrom));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(body, isHtml ? "text/html" : "text/plain");

        Transport.send(message);
    }
}