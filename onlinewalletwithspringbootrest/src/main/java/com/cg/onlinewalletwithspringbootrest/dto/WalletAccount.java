package com.cg.onlinewalletwithspringbootrest.dto;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Audited
public class WalletAccount {
	@Id
	@GeneratedValue
	private Integer accountNo;
	private Double balance = 0.0;
	@OneToMany(mappedBy = "walletAccount",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	List<TransactionHistory> transactionList;
	@Enumerated(EnumType.STRING)
	private Status accountStatus;
	@OneToOne(cascade = CascadeType.ALL,mappedBy = "account")
	private WalletUser user;
	 @CreatedBy
	 protected String createdBy;
	    @CreatedDate
	    @Temporal(TemporalType.TIMESTAMP)
	    protected Date creationDate;
	    @LastModifiedBy
	    protected String lastModifiedBy;
	    @LastModifiedDate
	    @Temporal(TemporalType.TIMESTAMP)
	    protected Date lastModifiedDate;
	
	public WalletAccount() {
		this.balance=0.0;
		this.accountStatus=Status.WatingForApproval;
		this.transactionList=new ArrayList<TransactionHistory>(0);
	}
	
	public WalletAccount(Integer accountNo, List<TransactionHistory> transactionList) {
		super();
		this.accountNo = accountNo;
		this.balance = 0.0;
		this.transactionList = transactionList;
		this.accountStatus = Status.WatingForApproval;
	}

	public Integer getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(Integer accountNo) {
		this.accountNo = accountNo;
	}

	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
	@OneToMany(mappedBy = "walletAccount",fetch = FetchType.LAZY)
	public List<TransactionHistory> getTransactionList() {
		return transactionList;
	}

	public void setTransactionList(List<TransactionHistory> transactionList) {
		this.transactionList = transactionList;
	}

	public Status getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(Status accountStatus) {
		this.accountStatus = accountStatus;
	}

	
	
	

}
