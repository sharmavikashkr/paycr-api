package com.paycr.oauth.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

    @GetMapping(value = "/userinfo")
    public UserDetails user() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            UserDetails userDetails = (UserDetails) principal;
            return userDetails;
        } catch (Exception ex) {
            return null;
        }
    }

}