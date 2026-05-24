package com.dbp.democarpultec.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RegistrationEmailServiceTest {

    @Test
    void shouldSendWelcomeEmail() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        RegistrationEmailService service = new RegistrationEmailService();
        ReflectionTestUtils.setField(service, "mailSender", mailSender);
        ReflectionTestUtils.setField(service, "fromAddress", "no-reply@carpultec.local");

        service.sendWelcomeEmail("juan@utec.edu.pe", "Juan");

        verify(mailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
    }

    @Test
    void shouldSkipEmailWhenMailSenderIsNotConfigured() {
        RegistrationEmailService service = new RegistrationEmailService();
        ReflectionTestUtils.setField(service, "fromAddress", "no-reply@carpultec.local");

        assertDoesNotThrow(() -> service.sendWelcomeEmail("juan@utec.edu.pe", "Juan"));
    }
}
