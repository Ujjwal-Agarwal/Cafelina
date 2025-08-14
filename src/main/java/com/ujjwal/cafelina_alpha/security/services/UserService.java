package com.ujjwal.cafelina_alpha.security.services;

import com.ujjwal.cafelina_alpha.domain.AuthProviders;
import com.ujjwal.cafelina_alpha.domain.RoleList;
import com.ujjwal.cafelina_alpha.domain.entities.Roles;
import com.ujjwal.cafelina_alpha.domain.entities.Users;
import com.ujjwal.cafelina_alpha.repository.RoleRepository;
import com.ujjwal.cafelina_alpha.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Users processOAuth2User(OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        return userRepository.findByEmail(email)
                .orElseGet(()->{
                    Users newUser = new Users();
                    newUser.setEmail(email);
                    newUser.setUsername(name);
                    newUser.setAuthProviders(AuthProviders.GOOGLE);
                    Roles userRole = roleRepository.findByRoleName(RoleList.USER)
                            .orElseThrow(()-> new RuntimeException("User Role not Set"));
                    newUser.setRoles(Collections.singletonList(userRole));
                    return userRepository.save(newUser);
                });
    }
}
