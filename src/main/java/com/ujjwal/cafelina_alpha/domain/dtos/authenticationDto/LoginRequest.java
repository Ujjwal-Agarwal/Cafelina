package com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

