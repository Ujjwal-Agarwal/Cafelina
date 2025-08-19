package com.ujjwal.cafelina_alpha.repository;

import com.ujjwal.cafelina_alpha.domain.entities.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
    List<EmailVerificationToken> findAllByHasBeenUsedFalseAndExpiresAtAfter(LocalDateTime now);
}