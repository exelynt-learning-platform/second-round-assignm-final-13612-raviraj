package com.webproj.ecom.proj.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.webproj.ecom.proj.model.User;
import com.webproj.ecom.proj.repo.UserRepository;

@Component
public class AuthUtil {

    @Autowired
    private UserRepository userRepository;

    public User getLoggedInUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        String username = auth.getName();

        return userRepository.findByEmail(username)
                .orElse(null);
    }
}
