package com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse{
    private String accessToken;
    private String tokenType = "Bearer";
    private String username;

    public JwtResponse(String accessToken,String username) {
        this.accessToken = accessToken;
        this.username = username;
    }
}

