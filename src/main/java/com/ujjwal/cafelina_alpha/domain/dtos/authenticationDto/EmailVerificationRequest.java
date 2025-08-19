package com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto;

import lombok.Data;

@Data
public class EmailVerificationRequest {
    private String token;
}
