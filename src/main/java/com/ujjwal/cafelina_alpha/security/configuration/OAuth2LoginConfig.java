package com.ujjwal.cafelina_alpha.security.configuration;

import com.ujjwal.cafelina_alpha.security.handlers.OAuth2AuthenticationFailureHandler;
import com.ujjwal.cafelina_alpha.security.handlers.OAuth2AuthenticationSuccessHandler;
import com.ujjwal.cafelina_alpha.security.repositories.HttpCookieOAuth2AuthorizationRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginConfig {
    private final OAuth2AuthenticationSuccessHandler oauth2SuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2FailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    public void configure(OAuth2LoginConfigurer<HttpSecurity> oauth2) {
        oauth2
                .authorizationEndpoint(authorization -> authorization
                        .baseUri("/api/auth/oauth2/authorize")
                        .authorizationRequestRepository(cookieAuthorizationRequestRepository))
                .redirectionEndpoint(redirection -> redirection
                        .baseUri("/api/auth/oauth2/callback/*"))
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService))
                .successHandler(oauth2SuccessHandler)
                .failureHandler(oauth2FailureHandler);
    }
}
