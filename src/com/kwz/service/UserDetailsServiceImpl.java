package com.kwz.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kwz.entity.User;

/*
 * Spring-security requires an implementation of UserDetailService. 
 */
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    /*
     * Mock for users from database. In the real application users will be retrieved from database and proper Spring UserDetails object will be created then for
     * each database user.
     */
    @Resource(name = "kwzDao")
    private KwzDao kwzDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<User> users = kwzDao.getUsers();
        for (User u : users)
            if (u.getName().equalsIgnoreCase(username))
                return u;

        throw new UsernameNotFoundException("UserAccount for name \"" + username + "\" not found.");
    }
}
