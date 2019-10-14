package com.cg.onlinewalletwithspringbootrest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cg.onlinewalletwithspringbootrest.dto.TransactionHistory;
import com.cg.onlinewalletwithspringbootrest.dto.WalletAccount;

public interface OnlineWalletTransactionHistoryRepository extends JpaRepository<TransactionHistory,Integer>{
	public List<TransactionHistory> findByWalletAccount(WalletAccount walletAccount);
}
