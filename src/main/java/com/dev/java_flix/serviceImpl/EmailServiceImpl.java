package com.dev.java_flix.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.dev.java_flix.exception.EmailNotVerifiedException;
import com.dev.java_flix.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService{

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender javaMailSender;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("JavaFlix - Verify your email");

            String verificationLink = frontendUrl + "/verify-email?token="+token;

            String emailBody = 
                    "Welcome to JavaFlix!\n\n"
                    + "Thank you for registering. Please verify your email address by clicking the link below:\n\n"
                    + verificationLink
                    + "\n\n"
                    + "This link will expire in 24 hours. \n\n"
                    + "If you didn`t create this account, please ignore this email. \n\n"
                    + "Best regards, \n\n"
                    + "JavaFlix Team";

            message.setText(emailBody);
            javaMailSender.send(message);
            logger.info("Verification email sent to {}", toEmail);        
        } catch (Exception e) {
            logger.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new EmailNotVerifiedException("Failed to send verification email");
        }
        
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("JavaFlix - Password Reset");

            String resetLink = frontendUrl + "/reset-password?token="+token;

            String emailBody = 
                    "Hi,\n\n"
                    + "We received a request to reset your password. Click the link below to reset it:\n\n"
                    + resetLink
                    + "\n\n"
                    + "This link will expire in 1 hours. \n\n"
                    + "If you didn`t request a password reset, please ignore this email. \n\n"
                    + "Best regards, \n\n"
                    + "JavaFlix Team";

            message.setText(emailBody);
            javaMailSender.send(message);
            logger.info("Password reset email sent to {}", toEmail);

        } catch (Exception e) {
            logger.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send password reset email");
        }
    }

}
