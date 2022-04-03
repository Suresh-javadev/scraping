package org.ans.scraping;

public class ScrapResponse {
	
    private ScrapResponse.Status status;
    
    private String refNo;
    
    private String message;
	   
	public ScrapResponse.Status getStatus() {
		return status;
	}

	public void setStatus(ScrapResponse.Status status) {
		this.status = status;
	}

	public String getRefNo() {
		return refNo;
	}

	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}




	public enum Status{
		SUCCESS,ERROR
	}
}
