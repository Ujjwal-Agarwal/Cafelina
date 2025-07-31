package com.ujjwal.cafelina_alpha.controllers;

import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.MessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController { // All protected controllers are defined here
    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userAccess(){
        return ResponseEntity.ok(new MessageResponse("User Content."));
    }

    @GetMapping("/moderator/board")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> moderatorAccess(){
        return ResponseEntity.ok(new MessageResponse("Moderator Board."));
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAccess(){
        return ResponseEntity.ok(new MessageResponse("Admin Board."));
    }
}
