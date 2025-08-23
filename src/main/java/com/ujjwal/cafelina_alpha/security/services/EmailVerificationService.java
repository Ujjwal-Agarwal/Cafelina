package com.ujjwal.cafelina_alpha.security.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.ujjwal.cafelina_alpha.domain.entities.EmailVerificationToken;
import com.ujjwal.cafelina_alpha.domain.entities.Users;
import com.ujjwal.cafelina_alpha.exceptions.InvalidEmailTokenException;
import com.ujjwal.cafelina_alpha.repository.EmailVerificationTokenRepository;
import com.ujjwal.cafelina_alpha.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
//    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    @Value("${resend.api-key}")
    private Resend resend;

    @Value("${app.base.url}")
    private String baseUrl;

    @Value("${app.name}")
    private String appName;

    @Value("${app.support.email}")
    private String supportEmail;

    @Value("${verification.token.expiration}")
    private Long expirationMinutes;

    @Async
    public String sendSignUpVerificationEmail(String userName,String userEmail,String verificationToken) throws ResendException {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Cafelina Team <onboarding@ujjwalagarwal.net>")
                    .to(userEmail)
                    .subject("Email Verification for Cafelina")
                    .html(buildEmailSignUpTemplate(userName, verificationToken.toString()))
                    .build();
            CreateEmailResponse data = resend.emails().send(params);
        } catch (
                ResendException e) {
            e.printStackTrace();
        }
        return verificationToken.toString();
    }

    private String buildEmailSignUpTemplate(String userName, String verificationToken) {
        String verificationLink = baseUrl + "verify-email?token=" + verificationToken;

        String template = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Verify Your Email Address</title>
            <style>
                /* ... same CSS styles as above ... */
            </style>
        </head>
        <body>
            <div class="email-container">
                <div class="header">
                    <h1>🔐 Verify Your Email</h1>
                </div>
                
                <div class="content">
                    <div class="greeting">
                        Hello {{userName}},
                    </div>
                    
                    <div class="message">
                        Thank you for registering with <strong>{{appName}}</strong>! To complete your account setup and ensure the security of your account, please verify your email address by clicking the button below.
                    </div>
                    
                    <div style="text-align: center;">
                        <a href="{{verificationLink}}" class="verify-button">
                            Verify Email Address
                        </a>
                    </div>
                    
                    <div class="security-notice">
                        <strong>Security Notice:</strong> This verification link will expire in {{expirationMinutes}} minutes. If you didn't create an account with us, please ignore this email.
                    </div>
                    
                    <div class="alternative-text">
                        <strong>Can't click the button?</strong> Copy and paste this link into your browser:<br>
                        <span style="word-break: break-all; color: #667eea;">{{verificationLink}}</span>
                    </div>
                </div>
                
                <div class="footer">
                    <p>
                        This email was sent by <strong>{{appName}}</strong><br>
                        If you have any questions, contact us at {{supportEmail}}
                    </p>
                </div>
            </div>
        </body>
        </html>
        """;

        return template
                .replace("{{userName}}", userName)
                .replace("{{appName}}", appName)
                .replace("{{verificationLink}}", verificationLink)
                .replace("{{expirationMinutes}}", String.valueOf(expirationMinutes))
                .replace("{{supportEmail}}", supportEmail);
    }

    @Transactional
    public void verifyEmailToken(String verificationToken) throws InvalidEmailTokenException {
        log.debug("OHUSADFUHDASFIHADverification {}{}", verificationToken);
        if (verificationToken == null || verificationToken.isEmpty()) {
            throw new InvalidEmailTokenException("Invalid verification token");
        }
        Optional<EmailVerificationToken> tokenOpt = emailVerificationTokenRepository
                .findByTokenHash(verificationToken);
        log.debug("Token: {}", tokenOpt.get().getTokenHash());
        if (tokenOpt.isEmpty()) {
//            log.debug("No token found with hash: '{}'", verificationToken);
            throw new InvalidEmailTokenException("Invalid verification token");
        }
        EmailVerificationToken token = tokenOpt.get();
        if (token.getHasBeenUsed() ) throw new InvalidEmailTokenException("Token has been used");
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) throw new InvalidEmailTokenException("Token has expired");

        Users user = token.getUser();
        token.setHasBeenUsed(true);
        user.setIsEmailVerified(true);
        log.debug("Updating user: {}", user);
        userRepository.save(user);
        // emailVerificationTokenRepository.saveAndFlush(matched);
    }
}
