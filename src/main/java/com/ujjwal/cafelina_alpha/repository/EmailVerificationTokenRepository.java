package com.ujjwal.cafelina_alpha.repository;

import com.ujjwal.cafelina_alpha.domain.entities.EmailVerificationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, String> {
    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);
    List<EmailVerificationToken> findAllByHasBeenUsedFalseAndExpiresAtAfter(LocalDateTime now);

    void removeAllTokensByHasBeenUsedIsTrue();

    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationToken e WHERE e.expiresAt <= :currentTime")
    void removeAllTokensByHasExpired(@Param("currentTime") LocalDateTime currentTime);
}