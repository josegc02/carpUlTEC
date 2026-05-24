package com.dbp.democarpultec.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RegistrationEmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.mail.from:no-reply@carpultec.local}")
    private String fromAddress;

    public void sendWelcomeEmail(String toEmail, String recipientName) {
        if (mailSender == null) {
            log.info("Skipping registration email because JavaMailSender is not configured");
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("Bienvenido a carpULTEC");
            message.setText(buildBody(recipientName));
            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("Could not send registration email to {}: {}", toEmail, ex.getMessage());
        }
    }

    private String buildBody(String recipientName) {
        String name = recipientName == null || recipientName.isBlank() ? "estudiante" : recipientName;
        return "Hola " + name + ",\n\n"
                + "Tu cuenta en carpULTEC fue creada correctamente.\n"
                + "Ya puedes iniciar sesion y publicar o solicitar viajes.\n\n"
                + "Equipo carpULTEC";
    }
}
