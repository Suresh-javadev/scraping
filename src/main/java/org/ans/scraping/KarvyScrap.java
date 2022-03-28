package org.ans.scraping;

import java.io.Closeable;

import org.ans.scraping.exception.FailToLoadSiteException;
import org.ans.scraping.exception.LoginException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope(scopeName="prototype")
public class KarvyScrap implements Closeable{
	
	@Value("${karvy.userid.input}")
	private String usernameLocator;
	@Value("${karvy.password.input}") 
	private String passwordLocator;
	@Value("${karvy.captcha.input}") 
	private String captchaInputLocator;
	@Value("${karvy.captcha}") 
	private String captchaLocator;
	@Value("${karvy.login.submit}") 
	private String loginSubmitLocator;	
	@Value("${karvy.login.url}")
	private String karvyUrl; 
	@Value("${karvy.report.url}")
	private String karvyReportConsoleUrl;
	@Value("${karvy.MFSD246.url}")
	private String mfsd246Url;
	@Value("${karvy.mfsd246.asondate.locator}")
	private String mfsd246AsondateLocator;

	private WebDriver driver;
	private String refNo="";
	private FileRequestInput input;
	
	public KarvyScrap setDriver(WebDriver driver,FileRequestInput input) {
		this.driver = driver;
		this.input = input;
		
		Assert.hasLength(this.input.getUsername(), "Username is required");
		Assert.hasLength(this.input.getPassword(), "Password is required");
		Assert.hasLength(this.input.getFiletype(), "Filetype is required");
		
		return this;
	}
	
	public KarvyScrap start() throws FailToLoadSiteException{
		this.driver.get(karvyUrl);
		pageLoad();
		
		if(!isSiteWorking()) {
			this.close();
			throw new FailToLoadSiteException("Site is down or not reachable");
		}
		
		return this
		.login()
		.reportConsole()
		.filetype()
		.captureReferenceNo();
	}
	
	private KarvyScrap login() throws LoginException{
		driver.findElement(By.id(usernameLocator)).sendKeys(input.getUsername());
		driver.findElement(By.id(passwordLocator)).sendKeys(input.getPassword());
		
		JavascriptExecutor jse = (JavascriptExecutor)this.driver;
		jse.executeScript("document.getElementById('"+captchaLocator+"').setAttribute('type', 'text');");
		
		String captchaText = driver.findElement(By.id(captchaLocator)).getAttribute("value");
		
		driver.findElement(By.id(captchaInputLocator)).sendKeys(captchaText);
		jse.executeScript("document.getElementById('"+loginSubmitLocator+"').click();");
		
		if(isAlertPresent()) {
			this.close();
			throw new LoginException("Username or password invalid");
		}
				
		
		pageLoad();
		
		if(driver.getCurrentUrl().equalsIgnoreCase(karvyUrl)) {
			this.close();
			throw new LoginException("Username or password invalid");
		}	
		return this;
	}
	
	public KarvyScrap reportConsole() {
		this.driver.get(karvyReportConsoleUrl);
		pageLoad();
		return this;
	}
	
	public KarvyScrap filetype() {
		
		if(this.input.getFiletype().equalsIgnoreCase("mfsd246")) {
			this.mfsd246();
		}
		return this;
	}
	public void mfsd246() {
		this.driver.get(mfsd246Url);
		Assert.hasLength(this.input.getAmc(), "For mfsd246 amc can't be null");
	    Assert.notNull(this.input.getFoliolist(), "For mfsd246 folio list can't be null");
	    Assert.isTrue(!this.input.getFoliolist().isEmpty(),"Folio list size can't be zero");
	    Assert.isTrue(this.input.getFoliolist().size()<=500,"Folio list size can't be grater than 500");
	    
	    if(this.input.getAsOnDate()) {
	    	JavascriptExecutor jse = (JavascriptExecutor)this.driver;
	    	jse.executeScript("document.getElementById('"+mfsd246AsondateLocator+"').click();");
	    }else{
	    	  Assert.notNull(this.input.getFromdate(), "From date required");
	    	  Assert.notNull(this.input.getTodate(), "To Date required");
	    }
	    
	    pageLoad();
	}
	
	public KarvyScrap captureReferenceNo() {
		
		return this;
	}
	
	public void close() {
		if(this.driver!=null)
			this.driver.quit();
	}
	
	
	public String refNo() {
		return this.refNo;
	}
	
	private boolean pageReadyState() {
	     JavascriptExecutor j = (JavascriptExecutor) driver;
	     
	     return j.executeScript("return document.readyState")
	      .toString().equals("complete");
	}
	
	private void pageLoad() {
		while(!pageReadyState()) {
			 try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean isSiteWorking() {
		return this.driver.getPageSource().contains("User ID") && this.driver.getPageSource().contains("Password");
	}
	
	private boolean isAlertPresent() 
	{ 
	    try { 
	        this.driver.switchTo().alert(); 
	        return true; 
	    } catch (NoAlertPresentException Ex){ 
	        return false; 
	    }   
	}
}
