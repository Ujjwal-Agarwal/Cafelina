package com.ujjwal.cafelina_alpha.controllers;

import com.resend.core.exception.ResendException;
import com.ujjwal.cafelina_alpha.domain.AuthProviders;
import com.ujjwal.cafelina_alpha.domain.RoleList;
import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.ApiResponse;
import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.EmailVerificationRequest;
import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.LoginRequest;
import com.ujjwal.cafelina_alpha.domain.dtos.authenticationDto.SignUpRequest;
import com.ujjwal.cafelina_alpha.domain.entities.EmailVerificationToken;
import com.ujjwal.cafelina_alpha.domain.entities.Roles;
import com.ujjwal.cafelina_alpha.domain.entities.Users;
import com.ujjwal.cafelina_alpha.repository.EmailVerificationTokenRepository;
import com.ujjwal.cafelina_alpha.repository.RoleRepository;
import com.ujjwal.cafelina_alpha.repository.UserRepository;
import com.ujjwal.cafelina_alpha.security.services.EmailVerificationService;
import com.ujjwal.cafelina_alpha.security.services.JWTService;
import com.ujjwal.cafelina_alpha.security.entities.UserPrincipal;
import com.ujjwal.cafelina_alpha.services.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
//    @Autowired
    private final AuthenticationManager authenticationManager;
//    @Autowired
    private final UserRepository userRepository;
//    @Autowired
    private final RoleRepository roleRepository;
//    @Autowired‚‚‚
    private final PasswordEncoder passwordEncoder;
//    @Autowired
    private final JWTService jwtService;

    private final EmailService emailService;

    private final EmailVerificationService emailVerificationService;

//    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
//    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTService jwtService, EmailService emailService) {
//        this.authenticationManager = authenticationManager;
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//        this.passwordEncoder = passwordEncoder;
//        this.jwtService = jwtService;
//        this.emailService = emailService;
//        this.emailVerificationService = new EmailVerificationService();
//    }

    @GetMapping("/verifyemail")
    public ResponseEntity<?> emailVerification(@RequestParam("token") String token) {
        String verificationToken = token;
        try{
            emailVerificationService.verifyEmailToken(verificationToken);
        }catch(Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Token is Invalid/Expired"));
        }
        return ResponseEntity.ok(new ApiResponse(true,"Email Verified"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request){
        String token = extractTokenFromCookie(request);
        if(token != null && jwtService.validateToken(token)) {
            UserPrincipal userPrincipal = jwtService.getUserPrincipalFromToken(token);
            return ResponseEntity.ok(userPrincipal);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if("sessionToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws ResendException {
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Invalid username or password"));
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if(userPrincipal.getIsEmailVerified() == false) { return ResponseEntity.badRequest().body(new ApiResponse(false,"Please verify your email")); }
        String jwt = jwtService.generateToken(userPrincipal);
        Cookie cookie = new Cookie("sessionToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        emailService.sendEmail();
        response.addCookie(cookie);
        return ResponseEntity.ok(new ApiResponse(true,"Successfully logged in"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("sessionToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(new ApiResponse(true,"Successfully logged out"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Username is already in use!"));
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Email is already in use!"));
        }
        try{
            UUID verificationToken = UUID.randomUUID();
            emailVerificationService.sendSignUpVerificationEmail(signUpRequest.getUsername(), signUpRequest.getEmail(),verificationToken.toString());
            Users user = Users.builder()
                    .username(signUpRequest.getUsername())
                    .email(signUpRequest.getEmail())
                    .passwordHash(passwordEncoder.encode(signUpRequest.getPassword()))
                    .authProviders(AuthProviders.LOCAL)
                    .isEmailVerified(false)
                    .build();

            EmailVerificationToken emailVerificationToken = EmailVerificationToken.builder()
                    .tokenHash(verificationToken.toString())
                    .build();

            user.setEmailVerificationToken(emailVerificationToken);

            Roles userRole = roleRepository.findByRoleName(RoleList.USER)
                    .orElseThrow(()-> new RuntimeException("User Role not Set"));

            user.setRoles(Collections.singletonList(userRole));

            log.debug("Registered user: {}", user);
            log.debug("Email verification token: {}", emailVerificationToken.getTokenHash());
            Users result = userRepository.save(user);
        }catch(JpaSystemException e){
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Database writing failed"));
        }
        catch(Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Sign Up Failed"));
        }
        return ResponseEntity.ok(new ApiResponse(true,"User registered successfully"));
    }
}
