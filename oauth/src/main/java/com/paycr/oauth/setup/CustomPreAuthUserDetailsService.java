package com.paycr.oauth.setup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import com.paycr.common.exception.PaycrException;
import com.paycr.common.util.Constants;

@Service
public class CustomPreAuthUserDetailsService
		implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

	@Autowired
	private TokenStore tokenStore;

	@Override
	public final UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) {
		try {
			OAuth2Authentication oauth = tokenStore.readAuthentication(token.getName());
			return (UserDetails) oauth.getUserAuthentication().getPrincipal();
		} catch (Exception ex) {
			throw new PaycrException(Constants.FAILURE, "401 Unauthorized");
		}
	}
}