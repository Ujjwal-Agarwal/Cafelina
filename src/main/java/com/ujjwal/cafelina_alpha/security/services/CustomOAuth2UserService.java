package com.ujjwal.cafelina_alpha.security.services;

import com.ujjwal.cafelina_alpha.domain.AuthProviders;
import com.ujjwal.cafelina_alpha.domain.RoleList;
import com.ujjwal.cafelina_alpha.domain.entities.Roles;
import com.ujjwal.cafelina_alpha.domain.entities.Users;
import com.ujjwal.cafelina_alpha.exceptions.OAuth2AuthenticationProcessingException;
import com.ujjwal.cafelina_alpha.repository.RoleRepository;
import com.ujjwal.cafelina_alpha.repository.UserRepository;
import com.ujjwal.cafelina_alpha.security.entities.OAuth2.OAuth2UserInfo;
import com.ujjwal.cafelina_alpha.security.entities.OAuth2.OAuth2UserInfoFactory;
import com.ujjwal.cafelina_alpha.security.entities.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        try{
            return processOAuth2User(userRequest,oAuth2User);
        }catch (AuthenticationException ex){
            throw ex;
        }catch(Exception ex){
            log.error("Error processing user",ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws OAuth2AuthenticationProcessingException {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes()
        );
        if(oAuth2UserInfo.getEmail().isEmpty()){
            throw new OAuth2AuthenticationProcessingException("Email is empty from OAuth2Provider");
        }
        Optional<Users> usersOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        Users user;

        if(usersOptional.isPresent()){
            user = usersOptional.get();
            if(!user.getAuthProviders().equals(AuthProviders.valueOf(
                    userRequest.getClientRegistration().getRegistrationId().toUpperCase()
            ))){
                throw new OAuth2AuthenticationProcessingException("Email Already in use with " + user.getAuthProviders() + "provider");
            }
            user = updateExistingUser(user,oAuth2UserInfo);
        }else{
            user = registerNewUser(userRequest,oAuth2UserInfo);
        }
        return UserPrincipal.create(user,oAuth2User.getAttributes());
    }

    private Users registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo){
        Users user = new Users();
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setAuthProviders(AuthProviders.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase()));
        user.setProviderId(oAuth2UserInfo.getId());
        user.setUsername(oAuth2UserInfo.getName());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        Roles userRole = roleRepository.findByRoleName(RoleList.USER)
                .orElseThrow(()-> new RuntimeException("User Role not Set"));
        user.setRoles(Collections.singletonList(userRole));

        return userRepository.save(user);
    }

    private Users updateExistingUser(Users user, OAuth2UserInfo oAuth2UserInfo){
        user.setUsername(oAuth2UserInfo.getName());
        user.setImageUrl(oAuth2UserInfo.getImageUrl());
        return userRepository.save(user);
    }
}
