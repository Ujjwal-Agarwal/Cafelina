package com.ujjwal.cafelina_alpha.security;

import com.ujjwal.cafelina_alpha.domain.RoleList;
import com.ujjwal.cafelina_alpha.domain.entities.Roles;
import com.ujjwal.cafelina_alpha.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class DataLoader implements ApplicationRunner {
//    @Autowired
    private final RoleRepository roleRepository;

    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository; // Construvctor Injection
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(roleRepository.findByRoleName(RoleList.USER).isEmpty()) {
            roleRepository.save(new Roles(RoleList.USER,new HashSet<>()));
        }
        if(roleRepository.findByRoleName(RoleList.ADMIN).isEmpty()) {
            roleRepository.save(new Roles(RoleList.ADMIN,new HashSet<>()));
        }
        if(roleRepository.findByRoleName(RoleList.MODERATOR).isEmpty()) {
            roleRepository.save(new Roles(RoleList.MODERATOR,new HashSet<>()));
        }
    }
}
