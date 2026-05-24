package com.dbp.democarpultec.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrationEmailServiceTest {

    @Test
    void shouldSendWelcomeEmail() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage message = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(message);
        RegistrationEmailService service = new RegistrationEmailService();
        ReflectionTestUtils.setField(service, "mailSender", mailSender);
        ReflectionTestUtils.setField(service, "fromAddress", "no-reply@carpoolutec.local");

        service.sendWelcomeEmail("juan@utec.edu.pe", "Juan");

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void shouldSkipEmailWhenMailSenderIsNotConfigured() {
        RegistrationEmailService service = new RegistrationEmailService();
        ReflectionTestUtils.setField(service, "fromAddress", "no-reply@carpoolutec.local");

        assertDoesNotThrow(() -> service.sendWelcomeEmail("juan@utec.edu.pe", "Juan"));
    }
}
