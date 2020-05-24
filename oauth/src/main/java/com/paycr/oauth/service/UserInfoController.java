package com.paycr.oauth.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserInfoController {

    @GetMapping(value = "/userinfo")
    public UserDetails user() {
        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal == null) {
                throw new AccessDeniedException("Access Denied");
            }
            UserDetails userDetails = (UserDetails) principal;
            return userDetails;
        } catch (Exception ex) {
            throw new AccessDeniedException(ex.getMessage());
        }
    }

}