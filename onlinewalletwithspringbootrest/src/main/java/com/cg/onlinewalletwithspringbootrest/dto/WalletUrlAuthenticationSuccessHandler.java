package com.cg.onlinewalletwithspringbootrest.dto;

import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.cg.onlinewalletwithspringbootrest.exception.MyException;
import com.cg.onlinewalletwithspringbootrest.service.OnlineWalletService;


/**
 *author: Venkatesh
 *Description : This class is used to redirect the user to the required page post the authentication
 *              is successful 
 *created Date: 11/10/2019
 *last modified : 13/10/2019           
 */
@Component
public class WalletUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	HttpSession session;

	@Autowired
	OnlineWalletService service;

	private static final Logger logger = LoggerFactory.getLogger(WalletUrlAuthenticationSuccessHandler.class);

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
	/**
	 *author: Venkatesh
	 *Description : This method is used to set the session post the authentication is successful 
	 *created Date: 11/10/2019
	 *last modified : 13/10/2019
	 *Input : HttpServletRequest Object,HttpServletResponse Object,Authentication Object
	 *Output : void          
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		String loginName="";

		if(authentication.getPrincipal() instanceof Principal) {
			loginName=((Principal)authentication.getPrincipal()).getName();
		}else {
			loginName=((WalletUserDetails)authentication.getPrincipal()).getUsername();
		}

		System.out.println("loginName: "+loginName);

		try {
			session.setAttribute("user", service.getUser(loginName));
		} catch (MyException e) {
			e.printStackTrace();
		}

		handle(request, response, authentication);
		clearAuthenticationAttributes(request);
	}
	/**
	 *author: Venkatesh
	 *Description : This method is used to determine the target url once the login is successful 
	 *created Date: 11/10/2019
	 *last modified : 13/10/2019           
	 *Input : HttpServletRequest Object,HttpServletResponse Object,Authentication Object
	 *Output : void 
	 */
	protected void handle(HttpServletRequest request, 
			HttpServletResponse response, Authentication authentication)
					throws IOException {
		System.out.println("target url verify");
		String targetUrl = determineTargetUrl(authentication);
		System.out.println(targetUrl);
		if (response.isCommitted()) {
			logger.debug(
					"Response has already been committed. Unable to redirect to "
							+ targetUrl);
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}
	/**
	 *author: Venkatesh
	 *Description : This method is used to determine the target url  
	 *created Date: 11/10/2019
	 *last modified : 13/10/2019           
	 *Input : HttpServletRequest Object,HttpServletResponse Object,Authentication Object
	 *Output : void
	 */
	protected String determineTargetUrl(Authentication authentication) {
		boolean isUser = false;
		boolean isAdmin = false;
		System.out.println("inside handler");
		Collection<? extends GrantedAuthority> authorities
		= authentication.getAuthorities();
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_CUSTOMER")) {
				isUser = true;
				break;
			} else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
				isAdmin = true;
				break;
			}
		}
		System.out.println(isUser+" "+isAdmin);
		if (isUser) {
			return "/userPage";
		} else if (isAdmin) {
			return "/userPage";
		} else {
			throw new IllegalStateException();
		}
	}
	/**
	 *author: Venkatesh
	 *Description : This method is used to clear the authentication attributes  
	 *created Date: 11/10/2019
	 *last modified : 13/10/2019           
	 *Input : HttpServletRequest Object
	 *Output : void
	 */
	protected void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}
	/**
	 *author: Venkatesh
	 *Description : This method is used to set the RedirectStrategy attribute 
	 *created Date: 11/10/2019
	 *last modified : 13/10/2019
	 *Input : RedirectStrategy Object
	 *Output : void           
	 */
	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}
	/**
	 *author: Venkatesh
	 *Description : This method is used to get the RedirectStrategy attribute 
	 *created Date: 11/10/2019
	 *last modified : 13/10/2019
	 *Output : RedirectStrategy Object
	 *Input : void           
	 */
	protected RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

}


