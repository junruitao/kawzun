package com.kwz.service;


public interface AuthenticationService {
	
	boolean login(String username, String password);

	void logout();

	boolean hasRole(String roles);

    boolean isLoggedIn();

    String getLogin();

}
