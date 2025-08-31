package com.example.astrafarma.Mail.domain;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
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

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    public void sendWelcomeMail(String to, String name) throws Exception {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("homeUrl", frontendBaseUrl); // <<--- NUEVO
        String htmlBody = templateEngine.process("WelcomeMail.html", context);
        String plainBody = "Hola " + name + ",\n\n¡Bienvenido a Astrafarma!\nTu cuenta ha sido confirmada exitosamente. Accede a nuestra plataforma y explora nuestras categorías y ofertas especiales.\n\nEste correo es informativo, por favor no respondas directamente a este mensaje.\n\nAtentamente,\nAstrafarma";
        sendMail(to, "¡Bienvenido a Astrafarma!", plainBody, htmlBody);
    }

    public void sendConfirmationMail(String to, String name, String link) throws Exception {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("link", link);
        String htmlBody = templateEngine.process("ConfirmMail.html", context);
        String plainBody = "Hola " + name + ",\n\nGracias por registrarte en Astrafarma.\nPara completar tu registro, confirma tu cuenta en el siguiente enlace:\n" + link + "\n\nSi no fuiste tú quien se registró, puedes ignorar este mensaje.\n\nAtentamente,\nAstrafarma";
        sendMail(to, "Confirma tu cuenta en Astrafarma", plainBody, htmlBody);
    }

    public void sendOffersNotificationMail(String to, String name, String message) throws Exception {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("message", message);
        context.setVariable("offersUrl", frontendBaseUrl); // <<--- Aquí
        String htmlBody = templateEngine.process("OffersNotificationMail.html", context);
        String plainBody = "Hola " + name + ",\n\n" + message + "\n\nAtentamente,\nAstrafarma";
        sendMail(to, "¡Nuevas ofertas en Astrafarma!", plainBody, htmlBody);
    }

    public void sendMail(String to, String subject, String plainBody, String htmlBody) throws Exception {
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

        message.setHeader("X-Mailer", "AstrafarmaMailer");
        message.setHeader("List-Unsubscribe", "<mailto:" + mailFrom + ">");
        message.setReplyTo(new Address[] { new InternetAddress(mailFrom) });

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(plainBody, "utf-8");

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html; charset=utf-8");

        MimeMultipart multipart = new MimeMultipart("alternative");
        multipart.addBodyPart(textPart);
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}