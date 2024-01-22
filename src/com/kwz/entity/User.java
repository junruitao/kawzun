package com.kwz.entity;

import java.util.ArrayList;
import java.util.Collection;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;

import com.kwz.enums.UserRole;

//@Entity
public class User extends KwzBaseBean implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Email
    private String email;
    @NotNull
    private String roles;
    private String password;
    private Long salt;

    public User() {
        this("newUser","newUser@email","","newUser");
    }

    public User(String name, String email, String roles, String password) {
        this(name, email, roles, password, null);
    };

    public User(String name, String email, String roles, String password, Long salt) {
        super();
        this.name = name;
        this.roles = roles;
        this.password = password;
        this.email = email;
        if (salt == null)
            this.salt = getCreated().getTime();
        else
            this.salt = salt;
    };

    public User(User data) {
        this(data.getName(), data.getEmail(),data.getRoles(),"",data.getSalt());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String r : getAllRoles())
            authorities.add(new GrantedAuthorityImpl(r));
        return authorities;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
//    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
//    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
//    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
//    @Transient
    public boolean isEnabled() {
        return true;
    }


    public void digestPassword(String inputPassword) {
        if (!StringUtils.isBlank(name) && !StringUtils.isBlank(inputPassword)) {
            PasswordEncoder passwordEncoder = new ShaPasswordEncoder(256);
            password = passwordEncoder.encodePassword(inputPassword, getSalt());
        }
    }


    public void digestPassword() {
        digestPassword(getPassword());
    }

    public Long getSalt() {
        return salt;
    }

    public void setSalt(Long salt) {
        this.salt = salt;
    }

    public UserRole[] getAllRoleItems() {
        return UserRole.values();
    }

    public String[] getAllRoles() {
        if (StringUtils.isBlank(roles))
            return null;
        return roles.split(",");
    }

    public void setAllRoles(String [] roles) {
        this.roles=StringUtils.join(roles,",");
    }


}
