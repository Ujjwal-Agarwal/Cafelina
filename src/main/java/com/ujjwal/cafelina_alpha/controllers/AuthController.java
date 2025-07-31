package com.ujjwal.cafelina_alpha.controllers;

import com.ujjwal.cafelina_alpha.domain.RoleList;
import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.ApiResponse;
import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.JwtResponse;
import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.LoginRequest;
import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.SignUpRequest;
import com.ujjwal.cafelina_alpha.domain.entities.Roles;
import com.ujjwal.cafelina_alpha.domain.entities.Users;
import com.ujjwal.cafelina_alpha.repository.RoleRepository;
import com.ujjwal.cafelina_alpha.repository.UserRepository;
import com.ujjwal.cafelina_alpha.security.JWTService;
import com.ujjwal.cafelina_alpha.security.UserPrincipal;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JWTService jwtService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = jwtService.generateToken(userPrincipal);
        return ResponseEntity.ok(new JwtResponse(jwt,userPrincipal.getUsername()));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Username is already in use!"));
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Email is already in use!"));
        }
        Users user = Users.builder()
                .username(signUpRequest.getUsername())
                .userEmail(signUpRequest.getEmail())
                .passwordHash(passwordEncoder.encode(signUpRequest.getPassword()))
                .build();

        Roles userRole = roleRepository.findByRoleName(RoleList.USER)
                .orElseThrow(()-> new RuntimeException("User Role not Set"));

        user.setRolesList(Collections.singleton(userRole));
        Users result = userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse(true,"User registered successfully"));
    }
}
