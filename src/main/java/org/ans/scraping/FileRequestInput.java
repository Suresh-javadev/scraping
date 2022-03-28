package org.ans.scraping;

import java.util.Date;
import java.util.List;

public interface FileRequestInput { 
	public String getUsername();
	public String getPassword();
	public String getAmc();
	public String getFiletype();
	public Date getFromdate();
	public Date getTodate();
	public boolean getAsOnDate();
	public List<String> getFoliolist();
}
