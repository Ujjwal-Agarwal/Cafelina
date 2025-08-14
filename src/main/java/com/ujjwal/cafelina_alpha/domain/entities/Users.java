package com.ujjwal.cafelina_alpha.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ujjwal.cafelina_alpha.domain.AuthProviders;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class Users {
    @Id
//    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

//    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String passwordHash;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProviders authProviders;

    private String providerId;

    private String imageUrl;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Roles> roles = ConcurrentHashMap.newKeySet();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

    public Set<Roles> getRoles(){
        return new HashSet<>(roles);
    }

    public void setRoles(List<Roles> list){
        if(this.roles == null){this.roles = ConcurrentHashMap.newKeySet();}
        if(list.isEmpty()){ return; }
        final Object rolesLock = new Object();
        synchronized (rolesLock) {
            this.roles.add(list.getFirst());
        }
    }
}
