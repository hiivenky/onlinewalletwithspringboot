package com.cg.onlinewalletwithspringbootrest.dto;

import java.time.LocalDateTime;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;



@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
public class TransactionHistory {
	
	@Id
	@GeneratedValue
	private Integer transactionId;
	private String description;
	private LocalDateTime dateOfTransaction;
	private Double amount;
	private Double balance;
	@ManyToOne(cascade = CascadeType.ALL,fetch = FetchType.EAGER )
	@JoinColumn
	private WalletAccount walletAccount;
		
	public TransactionHistory() {
		
	}

	public TransactionHistory(String description, Integer transactionId, LocalDateTime dateOfTransaction, Double amount,
			Double balance, WalletAccount account) {
		super();
		this.description = description;
		this.transactionId = transactionId;
		this.dateOfTransaction = dateOfTransaction;
		this.amount = amount;
		this.balance = balance;
		this.walletAccount = account;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public LocalDateTime getDateOfTransaction() {
		return dateOfTransaction;
	}

	public void setDateOfTransaction(LocalDateTime dateOfTransaction) {
		this.dateOfTransaction = dateOfTransaction;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public WalletAccount getAccount() {
		return walletAccount;
	}

	public void setAccount(WalletAccount account) {
		this.walletAccount = account;
	}

	


}
