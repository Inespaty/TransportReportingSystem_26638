package com.transport.TransportReportingSystem.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        if (to == null || subject == null || body == null) {
            throw new IllegalArgumentException("Email parameters cannot be null");
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message); // This will fail without config, but code is correct.
        } catch (MessagingException e) {
            // Log without user input to prevent log injection
            System.err.println("Failed to send email: MessagingException occurred");
            // In production, use proper logging framework
        } catch (Exception e) {
            System.err.println("Error sending email: Exception occurred");
        }
    }
}
