/*package com.paycr.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class CustomPreAuthUserDetailsService
		implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private CustomUserDetailsService userDetSer;

	@Override
	public final UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) {
		try {
			OAuth2Authentication oauth = tokenStore.readAuthentication(tokenStore.readAccessToken(token.getName()));
			return userDetSer.loadUserByUsername(oauth.getUserAuthentication().getName());
		} catch (Exception ex) {
			return null;
		}
	}
}*/