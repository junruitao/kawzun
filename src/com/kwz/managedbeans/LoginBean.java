package com.kwz.managedbeans;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.kwz.navigation.NavigationBean;
import com.kwz.service.AuthenticationService;
import com.kwz.util.FacesUtils;

@ManagedBean(name = "loginBean")
@ApplicationScoped
public class LoginBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String login;
    private String password;

    @ManagedProperty(value = "#{authenticationService}")
    private AuthenticationService authenticationService; // injected Spring

    public String login() {

        boolean loggedIn = authenticationService.login(login, password);

        if (loggedIn) {
            goHome();
            return "home.xhtml";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Login or password incorrect."));
            return null;
        }
    }

    private void goHome() {
        try {
            FacesUtils.getExternalContext().redirect("home.kwz");
        } catch (IOException e) {
            e.printStackTrace();
        } // return to application but being logged now
    }

    public String getLogin() {
        return login;
    }
    
    public String getUserName() {
        return authenticationService.getLogin();
    }


    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthenticationService(AuthenticationService as) {
        authenticationService = as;
    }

    public void logout() {
        goHome();
    }


    public boolean hasRole(String roles) {
        return authenticationService.hasRole(roles);
    }

    public boolean isLoggedIn() {
        return authenticationService.isLoggedIn();
    }

}
