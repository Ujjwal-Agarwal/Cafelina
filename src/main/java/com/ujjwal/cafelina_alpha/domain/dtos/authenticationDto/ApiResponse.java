package com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse{
    private Boolean success;
    private String message;
}
