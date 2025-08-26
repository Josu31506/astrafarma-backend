package com.example.astrafarma.Mail.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // --- Correo HTML de bienvenida ---
    public void sendWelcomeMail(String to, String name) throws Exception {
        Context context = new Context();
        context.setVariable("name", name);
        String body = templateEngine.process("WelcomeMail.html", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Bienvenido a Astrafarma");
        helper.setText(body, true);
        mailSender.send(message);
    }

    // --- Correo HTML de confirmación ---
    public void sendConfirmationMail(String to, String name, String link) throws Exception {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("link", link);
        String body = templateEngine.process("ConfirmMail.html", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject("Confirma tu cuenta en Astrafarma");
        helper.setText(body, true);
        mailSender.send(message);
    }
}