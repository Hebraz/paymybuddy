package com.paymybuddy.application.controller.principalInfo;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Implements PrincipalInfo interface for a oAuth2/OpenId authentication
 */
public class OAuth2PrincipalInfo implements PrincipalInfo {

    private final OAuth2AuthenticationToken authToken;

    public OAuth2PrincipalInfo(OAuth2AuthenticationToken authToken) {
        this.authToken = authToken;
    }
    /**
     * Gets the type of authentication of the principal
     * @return type of authentication
     */
    @Override
    public AuthenticationType authenticationType() {
        return AuthenticationType.OAUTH2;
    }
    /**
     * Gets the email of the principal
     * @return email, may be null
     */
    @Override
    public String getEmail() {
        if(authToken.isAuthenticated()){
            final OidcIdToken oidcIdToken = getIdToken(authToken.getPrincipal());
            if(oidcIdToken != null){
                return  (String)oidcIdToken.getClaims().get("email");
            }
            return null;
        }
        return null;
    }
    /**
     * Gets the first name of the principal
     * @return first name, may be null
     */
    @Override
    public String getFirstName() {
        if(authToken.isAuthenticated()){
            final OidcIdToken oidcIdToken = getIdToken(authToken.getPrincipal());
            if(oidcIdToken != null){
                return  (String)oidcIdToken.getClaims().get("given_name");
            }
            return null;
        }
        return null;
    }
    /**
     * Gets the last name of the principal
     * @return last name, may be null
     */
    @Override
    public String getLastName() {
        if(authToken.isAuthenticated()){
            final OidcIdToken oidcIdToken = getIdToken(authToken.getPrincipal());
            if(oidcIdToken != null){
                return  (String)oidcIdToken.getClaims().get("family_name");
            }
            return null;
        }
        return null;
    }
    /**
     * Gets the ID token provided by OpenId connect
     * @return ID token
     */
    private OidcIdToken getIdToken(OAuth2User principal){
        if(principal instanceof DefaultOidcUser) {
            DefaultOidcUser oidcUser = (DefaultOidcUser)principal;
            return oidcUser.getIdToken();
        }
        return null;
    }
}
