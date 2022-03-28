package org.ans.scraping.exception;

public class LoginException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginException() {
		super();
	}
	
	public LoginException(String msg){
		super(msg);
	}
	
	public LoginException(String msg, Throwable cause) {
        super(msg, cause);
	}    
}
