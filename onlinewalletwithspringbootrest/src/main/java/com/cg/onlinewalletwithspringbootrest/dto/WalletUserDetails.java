package com.cg.onlinewalletwithspringbootrest.dto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class WalletUserDetails implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7164816192795145413L;
	
	
	private String loginName;
	private String userPassword;
	private boolean active;
	private List <GrantedAuthority> authorities;
	
	public WalletUserDetails() {
		// TODO Auto-generated constructor stub
	}
	
	public WalletUserDetails(WalletUser user) {
		this.loginName=user.getLoginName();
		this.userPassword=user.getUserPassword();
		//this.active=user.isActive();
		this.authorities=Arrays.stream(user.getRoles().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return authorities;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return userPassword;
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return loginName;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}


}

