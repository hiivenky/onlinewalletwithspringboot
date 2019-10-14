package com.cg.onlinewalletwithspringbootrest.service;


import com.cg.onlinewalletwithspringbootrest.dto.WalletUser;
import com.cg.onlinewalletwithspringbootrest.dto.WalletUserDetails;
import com.cg.onlinewalletwithspringbootrest.exception.MyException;
import com.cg.onlinewalletwithspringbootrest.model.UserDto;
import com.cg.onlinewalletwithspringbootrest.repository.OnlineWalletUserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class JwtUserDetailsService implements UserDetailsService{
	
	@Autowired
	OnlineWalletService service = new OnlineWalletServiceImpl();
	@Autowired
	private WalletUserDetails userDetails;

	@Autowired
	private OnlineWalletUserRepository repository;

	@Autowired
	private PasswordEncoder bcryptEncoder;
	
	@Override
	public UserDetails loadUserByUsername(String loginName) throws UsernameNotFoundException {
		
		Optional<WalletUser> user;
		try {
			System.out.println(loginName);
			user = Optional.of(service.getUser(loginName));
			System.out.println(user.get().getPhoneNo());
			user.orElseThrow(() -> new UsernameNotFoundException("Not Found: "+loginName));
			return user.map(WalletUserDetails::new).get();
		} catch (MyException e) {
			e.printStackTrace();
		}
	
		return null;

	}
	
	public WalletUser save(UserDto user) {
		WalletUser newUser = new WalletUser();
		newUser.setUserName(user.getUsername());
		newUser.setUserPassword(bcryptEncoder.encode(user.getPassword()));
		return repository.save(newUser);
	}
}