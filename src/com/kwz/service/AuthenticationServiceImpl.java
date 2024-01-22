package com.kwz.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {

    @Resource(name = "authenticationManager")
    private AuthenticationManager authenticationManager; // specific for Spring
                                                         // Security

    @Override
    public boolean login(String username, String password) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            if (authenticate.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authenticate);
                return true;
            }
        } catch (AuthenticationException e) {
        }
        return false;
    }

    @Override
    public void logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    // Check if it' has any role except ROLE_ANONYMOUS
    @Override
    public boolean isLoggedIn() {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        if (authenticate == null)
            return false;
        for (GrantedAuthority a : authenticate.getAuthorities()) {
            if (a.getAuthority().equals("ROLE_ANONYMOUS"))
                continue;
            else
                return true;
        }
        return false;
    }

    @Override
    public String getLogin() {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        if (authenticate == null)
            return null;
        return authenticate.getName();
    }

    @Override
    public boolean hasRole(String roles) {
        Authentication authenticate = SecurityContextHolder.getContext().getAuthentication();
        if (authenticate == null)
            return false;
        for (GrantedAuthority a : authenticate.getAuthorities()) {
            for (String role : roles.split(",")) {
                if (!StringUtils.isBlank(role) && a.getAuthority().endsWith(role))
                    return true;
            }
        }
        return false;
    }

}
