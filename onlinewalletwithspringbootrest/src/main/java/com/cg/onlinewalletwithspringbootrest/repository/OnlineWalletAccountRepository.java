package com.cg.onlinewalletwithspringbootrest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.onlinewalletwithspringbootrest.dto.WalletAccount;



public interface OnlineWalletAccountRepository extends JpaRepository<WalletAccount,Integer>{
	public WalletAccount findByAccountNo(Integer accountNo);
	
}
