package com.ujjwal.cafelina_alpha.security.entities.OAuth2;

import com.ujjwal.cafelina_alpha.domain.AuthProviders;
import com.ujjwal.cafelina_alpha.exceptions.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) throws OAuth2AuthenticationProcessingException {
        if(registrationId.equalsIgnoreCase(AuthProviders.GOOGLE.toString())){
            return new GoogleOAuth2UserInfo(attributes);
        }else{
            throw new OAuth2AuthenticationProcessingException(
                    "OAUTH2 LOGIN NOT SUPPORTED FOR : " + registrationId
            );
        }
    }
}
