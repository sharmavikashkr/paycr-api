package com.payme.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.payme.dashboard.controller.CustomerUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Bean
    public UserDetailsService mongoUserDetails() {
        return new CustomerUserDetailsService();
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/", "/login", "/createuser", "/css/*", "/js/*", "/data/*", "/dist/**", "/html/*", "/img/*", "/less/*", "/vendor/**", "/vendor/***").permitAll()
		.anyRequest().authenticated()
		.and().formLogin().loginPage("/login").defaultSuccessUrl("/dashboard");
		http.headers().frameOptions().disable();
		http.csrf().disable();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		UserDetailsService userDetailsService = mongoUserDetails();
        auth.userDetailsService(userDetailsService);
        //userDetailsService.loadUserByUsername("user@email.com");
	}
}
