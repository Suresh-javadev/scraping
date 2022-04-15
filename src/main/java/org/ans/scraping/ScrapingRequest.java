package org.ans.scraping;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;


public class ScrapingRequest implements FileRequestInput{

	private String username;
	private String password;
	private String amc;
	private String filetype;
	@JsonFormat(pattern="dd-MM-yyyy",timezone="Asia/Kolkata")
	private Date fromdate;
	@JsonFormat(pattern="dd-MM-yyyy",timezone="Asia/Kolkata")
	private Date todate;
	private String zipPassword;
	private boolean asOnDate;
	private List<String> foliolist;
	
	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getFiletype() {
		return this.filetype;
	}

	@Override
	public Date getFromdate() {
		return this.fromdate;
	}

	@Override
	public Date getTodate() {
		return this.todate;
	}

	@Override
	public boolean getAsOnDate() {
		return this.asOnDate;
	}

	@Override
	public List<String> getFoliolist() {
		return this.foliolist;
	}

	@Override
	public String getAmc() {
		return this.amc;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAmc(String amc) {
		this.amc = amc;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public void setFromdate(Date fromdate) {
		this.fromdate = fromdate;
	}

	public void setTodate(Date todate) {
		this.todate = todate;
	}

	public void setAsOnDate(boolean asOnDate) {
		this.asOnDate = asOnDate;
	}

	public void setFoliolist(List<String> foliolist) {
		this.foliolist = foliolist;
	}

	@Override
	public String getZipPassword() {		
		return this.zipPassword;
	}	
	
	public String setZipPassword(String zipPassword) {		
		return this.zipPassword = zipPassword;
	}	
}
