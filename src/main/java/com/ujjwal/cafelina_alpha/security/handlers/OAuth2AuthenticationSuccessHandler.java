package com.ujjwal.cafelina_alpha.security.handlers;

import com.ujjwal.cafelina_alpha.domain.entities.Users;
import com.ujjwal.cafelina_alpha.security.repositories.HttpCookieOAuth2AuthorizationRequestRepository;
import com.ujjwal.cafelina_alpha.security.services.CookieUtil;
import com.ujjwal.cafelina_alpha.security.services.JWTService;
import com.ujjwal.cafelina_alpha.security.services.MyUserDetailsService;
import com.ujjwal.cafelina_alpha.security.services.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ujjwal.cafelina_alpha.security.repositories.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
@RequiredArgsConstructor
@Slf4j // Lombok Logging utility
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTService jwtService;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
    private final UserService userService;
    private final MyUserDetailsService userDetailsService;

//    @Value("${app.cors.allowed-origins}")
    private List<String> authorizedRedirectUris = Arrays.asList("https://accounts.google.com", "https://oauth2.googleapis.com","http://localhost:5173/oauth2/authorize","http://localhost:5173/dashboard");


    private void setJwtCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("sessionToken", token);
        cookie.setHttpOnly(true);  // Prevent XSS attacks
        cookie.setSecure(false);   // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 24 hours
//        cookie.setSameSite(Cookie.SameSite.LAX.attributeValue());
        response.addCookie(cookie);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return authorizedRedirectUris.stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request,
                                                 HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request,REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        log.debug(String.valueOf(redirectUri));
        String targetUrl = redirectUri.orElse("http://localhost:5173/dashboard");

        if(!isAuthorizedRedirectUri(targetUrl)) {
            try {
                throw new BadRequestException("Unauthorised Redirect URI" + targetUrl);
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        Users user = userService.processOAuth2User(oauth2User);
        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getUsername()));

        setJwtCookie(response,token);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("oauth2","success")
                .build().toUriString();
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException{

        String targetUrl = determineTargetUrl(request, response, authentication);
        if(response.isCommitted()) {
            log.debug("Response has already been committed");
            return;
        }
        log.info(String.valueOf(response.getHeaders("Set-Cookie")));
        clearAuthenticationAttributes(request,response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
