package com.example.astrafarma;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

class LoginServiceTest {

    @Test
    void loginSendsEmailOnSuccess() {
        EmailService emailService = mock(EmailService.class);
        LoginService loginService = new LoginService(emailService);

        assertTrue(loginService.login("user@example.com", "secret"));
        verify(emailService).sendLoginNotification("user@example.com");
    }

    @Test
    void loginFailsWithoutEmail() {
        EmailService emailService = mock(EmailService.class);
        LoginService loginService = new LoginService(emailService);

        assertFalse(loginService.login("user@example.com", "wrong"));
        verify(emailService, never()).sendLoginNotification(anyString());
    }
}
