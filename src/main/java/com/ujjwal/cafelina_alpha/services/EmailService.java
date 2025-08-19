package com.ujjwal.cafelina_alpha.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailService {
    Resend resend;

    public EmailService(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey); // Value available immediately
    }
    public void sendEmail() throws ResendException {
        log.info("Sending email...");
        log.info(resend.toString());
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Acme <onboarding@ujjwalagarwal.net>")
                .to("agarwal.ujjwal2100@gmail.com")
                .subject("it works!")
                .html("<strong>hello world</strong>")
                .build();
        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println(data.getId());
        } catch (
                ResendException e) {
            e.printStackTrace();
        }
    }
}
