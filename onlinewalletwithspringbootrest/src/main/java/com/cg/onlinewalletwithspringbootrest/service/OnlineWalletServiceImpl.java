package com.cg.onlinewalletwithspringbootrest.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cg.onlinewalletwithspringbootrest.dto.Status;
import com.cg.onlinewalletwithspringbootrest.dto.TransactionHistory;
import com.cg.onlinewalletwithspringbootrest.dto.WalletAccount;
import com.cg.onlinewalletwithspringbootrest.dto.WalletUser;
import com.cg.onlinewalletwithspringbootrest.exception.MyException;
import com.cg.onlinewalletwithspringbootrest.repository.OnlineWalletAccountRepository;
import com.cg.onlinewalletwithspringbootrest.repository.OnlineWalletTransactionHistoryRepository;
import com.cg.onlinewalletwithspringbootrest.repository.OnlineWalletUserRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Service("OnlineWalletService")
public class OnlineWalletServiceImpl implements OnlineWalletService {
	public static final String ACCOUNT_SID = "AC31aba4770528836f973a01e47009fd1a";
	public static final String AUTH_TOKEN = "e742955b9554cf3a828cafdbb9fb555a";
	  
	@Autowired
	private OnlineWalletUserRepository userDao;
	@Autowired
	private OnlineWalletAccountRepository accountDao;
	@Autowired
	private OnlineWalletTransactionHistoryRepository transactionDao;
	
	
	/**
	 *author: Venkatesh
	 *Description : This function is used is called when a user registers 
	 *              to our wallet.This method adds checks whether the user 
	 *              user is already present and then adds
	 *created Date: 08/9/2019
	 *last modified : 08/09/2019  
	 *Input : WalletUser object
	 *Output : WalletUser object            
	 */
	@Override
	public WalletUser addWalletUser(WalletUser user) throws MyException {
		// TODO Auto-generated method stub
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		List<WalletUser> users = userDao.findAll();
		if(users!=null) {
		for(int i=0;i<users.size();i++) {
			if(users.get(i).getPhoneNo().equals(user.getPhoneNo())){
				
				throw new MyException("User exists try to login");
			}
		}
		}
		Validate.validatePhoneNumber(user.getPhoneNo());
		WalletUser modifiedUser = userDao.save(user);
		modifiedUser.setRoles("ROLE_CUSTOMER");
		modifiedUser = userDao.findByUserId(modifiedUser.getUserId());
		modifiedUser.setLoginName(modifiedUser.getUserName()+modifiedUser.getUserId());
		userDao.save(modifiedUser);
		 Message message = Message
	                .creator(new PhoneNumber("+91"+modifiedUser.getPhoneNo()), // to
	                        new PhoneNumber("+18102070628"), // from
	                        "Your online wallet request has been initiated,please wait till the admin approves your account ")
	                .create();
		
		return modifiedUser;
	}
	/**
	 *author: Venkatesh
	 *Description : This method is used by admin to approve accounts that 
	 *              are waiting for approval
	 *created Date: 08/9/2019
	 *last modified : 08/09/2019
	 *Input : Integer accountNo
	 *Output : WalletAccount Object              
	 */
	@Override
	public WalletAccount approveAccount(Integer accountNo) throws MyException {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		WalletAccount account = accountDao.getOne(accountNo);
		if(account!=null) {
			System.out.println(account.getAccountNo());
			account.setAccountStatus(Status.Approved);
			accountDao.save(account);
			WalletUser modifiedUser = userDao.findByAccount(account);
			System.out.println(modifiedUser);
			 Message message = Message
		                .creator(new PhoneNumber("+91"+modifiedUser.getPhoneNo()), // to
		                        new PhoneNumber("+18102070628"), // from
		                        "Hello "+modifiedUser.getUserName()+"Your account has been activated "
		                        		+ "your Login Credentials userName: "+modifiedUser.getLoginName()+" password "+
		                        modifiedUser.getUserPassword()).create();
		}
		return account;
	}
	/**
	 *author: Utkarsh
	 *Description : This method is used to verify the login credentials
	 *created Date: 08/9/2019
	 *last modified : 08/09/2019              
	 */
	@Override
	public boolean validateLoginCredentials(String userName, String password) {
		
		 WalletUser user = userDao.findByLoginName(userName);
	        if(user!=null){
	            if(user.getUserPassword().equals(password)){
	                return true;
	            }
	            else{
	                return false;
	            }
	        }
		return false;
	}
	/**
	 *author: Venkatesh
	 *Description : It is used to get the particular user
	 *created Date: 08/9/2019
	 *last modified : 08/09/2019
	 *Input : String loginName
	 *Output : WalletUser Object               
	 */
	@Override
	public WalletUser getUser(String userName) throws MyException {
		WalletUser user=userDao.findByLoginName(userName);
		if(user!=null) {
			return user;
		}
		throw new MyException("User not present");
	}
	/**
	 *author: Venkatesh
	 *Description : It is used to add amount to the user wallet 
	 *              This function uses the payment gateway to add the amount
	 *              to the wallet
	 *created Date: 08/9/2019
	 *last modified : 08/09/2019
	 *Input : Integer accountId , Double amount
	 *Output : Double balance               
	 */
	@Override
	public Double addAmount(Integer accountId, Double amount) throws MyException {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Validate.validateId(accountId);
        if(amount>0){
            WalletAccount account = accountDao.getOne(accountId);
            WalletUser modifiedUser = userDao.findByAccount(account);
            account.setBalance(account.getBalance()+amount);
            accountDao.save(account);
        	TransactionHistory transactionHistory = new TransactionHistory();
			transactionHistory.setAmount(amount);
			modifiedUser.getAccount().setBalance(account.getBalance());
			transactionHistory.setBalance(modifiedUser.getAccount().getBalance());
			transactionHistory.setDateOfTransaction(LocalDateTime.now());
			transactionHistory.setAccount(modifiedUser.getAccount());
			transactionHistory.setDescription("myself");
			transactionDao.save(transactionHistory);
            Message message = Message
	                .creator(new PhoneNumber("+91"+modifiedUser.getPhoneNo()), // to
	                        new PhoneNumber("+18102070628"),"Your account has been credited with amount "+amount+
	                        " Your balance is "+modifiedUser.getAccount().getBalance()
	                        ).create();
            
        }
        throw new MyException("amount can not be less than zero");
	}
	/**
	 *author: Venkatesh
	 *Description : This function is used to tranfer amount to the 
	 *              users who have registered with our wallet  
	 *created Date: 08/9/2019
	 *last modified : 08/09/2019
	 *Input : Integer userId , Double amount
	 *Output : Double balance               
	 */
	@Override
	public Double transferAmount(Integer userId, String phoneNumber, Double amount) throws MyException {
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		boolean present = false;
		Validate.validateId(userId);
		WalletUser user = userDao.findByUserId(userId);
		List<WalletUser> users = userDao.findAll();
		for(int i=0;i<users.size();i++) {
			if(users.get(i).getPhoneNo().equals(phoneNumber)) {
				present = true;
			}
		}
		if(!present) {
			throw new MyException("User does not exist");
		}
		if(amount<0) {
			throw new MyException("amount can not be less than zero");
		}
		
		
		if(user.getAccount().getBalance().compareTo(amount)<0) {
			throw new MyException("Insufficient funds");
		}
		WalletUser toUser = userDao.findByPhoneNo(phoneNumber);
		WalletUser fromUser = userDao.findByUserId(userId);
		fromUser.getAccount().setBalance(fromUser.getAccount().getBalance()-amount);
		TransactionHistory transactionHistory = new TransactionHistory();
		transactionHistory.setAccount(fromUser.getAccount());
		transactionHistory.setBalance(fromUser.getAccount().getBalance());
		transactionHistory.setAmount(amount);
		transactionHistory.setDateOfTransaction(LocalDateTime.now());
		transactionHistory.setDescription("transferred to phone number "+phoneNumber);
		userDao.save(fromUser);
		transactionDao.save(transactionHistory);
		Message message = Message
                .creator(new PhoneNumber("+91"+fromUser.getPhoneNo()), // to
                        new PhoneNumber("+18102070628"),"Your account has been debited with amount "+amount+
                        " Your balance is "+fromUser.getAccount().getBalance()
                        ).create();
		TransactionHistory transaction1 = new TransactionHistory();
		toUser.getAccount().setBalance(toUser.getAccount().getBalance()+amount);
		transaction1.setAccount(toUser.getAccount());
		transaction1.setBalance(toUser.getAccount().getBalance());
		transaction1.setAmount(amount);
		transaction1.setDescription("received from "+fromUser.getPhoneNo());
		transaction1.setDateOfTransaction(LocalDateTime.now());
		userDao.save(toUser);
		transactionDao.save(transaction1);
		Message toMessage = Message
                .creator(new PhoneNumber("+91"+toUser.getPhoneNo()), // to
                        new PhoneNumber("+18102070628"),"Your account has been credited with amount "+amount+" from user "+fromUser.getPhoneNo()+
                        " Your balance is "+toUser.getAccount().getBalance()
                        ).create();
		return amount;
	}
	@Override
	public List<TransactionHistory> getTransactions(Integer accountId,LocalDateTime fromDate,
			LocalDateTime toDate){
		//em.refresh(TransactionHistory.class);
		WalletAccount walletAccount = accountDao.findByAccountNo(accountId);
		List<TransactionHistory> myTransactions = transactionDao.findByWalletAccount(walletAccount);
		System.out.println(myTransactions.size()+" "+fromDate);
		List<TransactionHistory> retTransactions = new ArrayList<TransactionHistory>();
		for(int i=0;i<myTransactions.size();i++) {
			if(myTransactions.get(i).getDateOfTransaction()!=null&&myTransactions.get(i).getDateOfTransaction().compareTo(fromDate)>=0
			   &&myTransactions.get(i).getDateOfTransaction().compareTo(toDate)<=0) {
				retTransactions.add(myTransactions.get(i));
			}
		}
		return retTransactions;
	}
	
	@Override
	public List<WalletAccount> getAccountsToApprove() {
		// TODO Auto-generated method stub
	    List<WalletAccount> walletAccounts = accountDao.findAll();
	    List<WalletAccount> notApproved = new ArrayList<WalletAccount>();
	    for(int i=0;i<walletAccounts.size();i++) {
	    	if(walletAccounts.get(i).getAccountStatus().toString().equals("WatingForApproval")) {
	    		notApproved.add(walletAccounts.get(i));
	    	}
	    }
		return notApproved;
	}
	


	

}
