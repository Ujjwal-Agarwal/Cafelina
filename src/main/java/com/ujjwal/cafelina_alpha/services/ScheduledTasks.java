package com.ujjwal.cafelina_alpha.services;

import com.ujjwal.cafelina_alpha.repository.EmailVerificationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    @Async
    @Scheduled(cron = "${app.scheduling.email-token-cleanup}")
    @Transactional
    public void removeExpiredEmailVerificationTokens() {
        try{
            emailVerificationTokenRepository.removeAllTokensByHasBeenUsedIsTrue();
            emailVerificationTokenRepository.removeAllTokensByHasExpired(LocalDateTime.now());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
