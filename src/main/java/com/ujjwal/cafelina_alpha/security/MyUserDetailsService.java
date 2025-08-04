package com.ujjwal.cafelina_alpha.security;

import com.ujjwal.cafelina_alpha.domain.entities.Users;
import com.ujjwal.cafelina_alpha.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {
//    @Autowired
    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"+username));
        return UserPrincipal.create(user);
    }
}
