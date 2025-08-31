package com.example.astrafarma.Mail.listeners;

import com.example.astrafarma.Mail.domain.MailService;
import com.example.astrafarma.User.domain.User;
import com.example.astrafarma.Mail.events.UserRegisteredEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationListener {

    @Autowired
    private MailService mailService;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    @Async
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        User user = event.getUser();
        String to = user.getEmail();
        String name = user.getFullName();
        String token = user.getVerificationToken();
        String link = frontendBaseUrl.endsWith("/") ? frontendBaseUrl + "verify?token=" + token : frontendBaseUrl + "/verify?token=" + token;

        try {
            mailService.sendConfirmationMail(to, name, link);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}