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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;

@RestController
@RequestMapping("/api/auth")
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

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
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
        String jwt = jwtService.generateToken(userPrincipal);
        Cookie cookie = new Cookie("sessionToken", jwt);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);

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
            Users user = Users.builder()
                    .username(signUpRequest.getUsername())
                    .email(signUpRequest.getEmail())
                    .passwordHash(passwordEncoder.encode(signUpRequest.getPassword()))
                    .build();

            Roles userRole = roleRepository.findByRoleName(RoleList.USER)
                    .orElseThrow(()-> new RuntimeException("User Role not Set"));

            user.setRoles(Collections.singletonList(userRole));
            Users result = userRepository.save(user);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse(false,"Sign Up Failed"));
        }
        return ResponseEntity.ok(new ApiResponse(true,"User registered successfully"));
    }


}
