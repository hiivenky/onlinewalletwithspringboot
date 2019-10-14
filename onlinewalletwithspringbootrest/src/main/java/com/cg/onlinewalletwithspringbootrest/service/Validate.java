package com.cg.onlinewalletwithspringbootrest.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cg.onlinewalletwithspringbootrest.dto.WalletUser;
import com.cg.onlinewalletwithspringbootrest.exception.MyException;
import com.cg.onlinewalletwithspringbootrest.repository.OnlineWalletUserRepository;



class Validate {
	
	@Autowired
	private static OnlineWalletUserRepository userDao;
	
	static void  validateId(Integer userId) throws MyException {

		String str = ""+userId;

		if(!str.matches("\\d+")) {

			throw new MyException("userId consists only digits");

		}

	}
	static void validateDate(LocalDateTime date) throws MyException {

		if(LocalDateTime.now().isBefore(date)){

			throw new MyException("Invalid date");

		}

	}
	static void validateDuplicate(WalletUser user) throws MyException {
		
		List<WalletUser> users = userDao.findAll();
		if(users!=null) {
		for(int i=0;i<users.size();i++) {
			if(users.get(i).getPhoneNo().equals(user.getPhoneNo())) {
				throw new MyException("User exists try to login");
			}
		}
		}
		
	}

	static void validatePhoneNumber(String number) throws MyException {
		
		if(number.matches("[a-z][A-Z]+")) {
			throw new MyException("number contains only digits");
		}

		if(!number.matches("\\d{10}")){

			throw new MyException("Phone number must be 10 digits");

		}

	}

}