package com.cg.onlinewalletwithspringbootrest.model;


public class UserDto {

	private String userName;
	private String userPassword;

	public String getUsername() {
		return userName;
	}

	public void setUsername(String username) {
		this.userName = username;
	}

	public String getPassword() {
		return userPassword;
	}

	public void setPassword(String password) {
		this.userPassword = password;
	}
}