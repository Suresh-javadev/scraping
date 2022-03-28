package org.ans.scraping.exception;

public class FailToLoadSiteException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FailToLoadSiteException() {
		super();
	}
	
	public FailToLoadSiteException(String msg){
		super(msg);
	}
	
	public FailToLoadSiteException(String msg, Throwable cause) {
        super(msg, cause);
	} 
}
