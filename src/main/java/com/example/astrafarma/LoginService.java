package com.example.astrafarma;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final EmailService emailService;

    public LoginService(EmailService emailService) {
        this.emailService = emailService;
    }

    public boolean login(String email, String password) {
        boolean success = "secret".equals(password);
        if (success) {
            emailService.sendLoginNotification(email);
        }
        return success;
    }
}
