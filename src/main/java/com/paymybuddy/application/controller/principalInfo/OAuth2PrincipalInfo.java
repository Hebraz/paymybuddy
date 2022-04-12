package com.paymybuddy.application.controller.principalInfo;

import com.paymybuddy.application.exception.PrincipalAuthenticationException;
import lombok.Getter;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Implements PrincipalInfo interface for a oAuth2/OpenId authentication
 */
@Getter
public class OAuth2PrincipalInfo implements PrincipalInfo {

    private final String email;
    private final String firstName;
    private final String lastName;

    public OAuth2PrincipalInfo(OAuth2AuthenticationToken authToken) throws PrincipalAuthenticationException {
        if(authToken.isAuthenticated()) {
            final OidcIdToken oidcIdToken = getIdToken(authToken.getPrincipal());
            if(oidcIdToken != null) {
                this.email = extractEmailFromIdToken(oidcIdToken);
                this.firstName = extractFirstNameFromIdToken(oidcIdToken);
                this.lastName = extractLastNameFromIdToken(oidcIdToken);
            } else {
                throw new PrincipalAuthenticationException("OidcIdToken is null");
            }
        } else {
            throw new PrincipalAuthenticationException("Principal not authenticated");
        }
    }

    /**
     * Extract email of principal from Oidc ID Authentication token
     * @param oidcIdToken an OidcIdToken instance
     * @return email
     * @throws PrincipalAuthenticationException when email cannot be extracted from token, since email is the user identifier in application.
     */
    private String extractEmailFromIdToken(OidcIdToken oidcIdToken) throws PrincipalAuthenticationException {
        String email = (String)oidcIdToken.getClaims().get("email");
        if(email == null){
            throw new PrincipalAuthenticationException("Email cannot be extracted from token: no email in oidcIdToken");
        }
        return email;

    }

    /**
     * Extract first name of principal from Oidc ID Authentication token
     * @param oidcIdToken an OidcIdToken instance
     * @return first name, or null if no first name can be extracted (first name not mandatory)
     */
    private String extractFirstNameFromIdToken(OidcIdToken oidcIdToken) {
        return  (String)oidcIdToken.getClaims().get("given_name");
    }

    /**
     * Extract last name of principal from Oidc ID Authentication token
     * @param oidcIdToken an OidcIdToken instance
     * @return last name, or null if no last name can be extracted (last name not mandatory)
     */
    private String extractLastNameFromIdToken(OidcIdToken oidcIdToken) {
        return  (String)oidcIdToken.getClaims().get("family_name");
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

    /**
     * Gets the type of authentication of the principal
     *
     * @return type of authentication
     */
    @Override
    public AuthenticationType authenticationType() {
        return AuthenticationType.OAUTH2;
    }
}
