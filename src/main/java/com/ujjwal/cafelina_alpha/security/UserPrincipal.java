package com.ujjwal.cafelina_alpha.security;

import com.ujjwal.cafelina_alpha.domain.entities.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.catalina.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private Collection<? extends GrantedAuthority> authorities;


    // Static helps create Object directly without needing another User instance
    public static UserPrincipal create(Users user) {
        List<GrantedAuthority> authorities = user.getRolesList().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getUserId(),
                user.getUsername(),
                user.getUserEmail(),
                user.getPasswordHash(),
                authorities
        );
    }
    @Override
    public boolean isAccountNonExpired() {return true;}
    @Override
    public boolean isAccountNonLocked() {return true;}
    @Override
    public boolean isCredentialsNonExpired() {return true;}
    @Override
    public boolean isEnabled() {return true;}
}
