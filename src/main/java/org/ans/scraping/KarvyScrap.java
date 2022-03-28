package org.ans.scraping;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class KarvyScrap {
	
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
	 

	private WebDriver driver;
	private String refNo;
	
	public KarvyScrap setDriver(WebDriver driver) {
		this.driver = driver;
		return this;
	}
	
	public KarvyScrap start(String url) {
		this.driver.get(url);
		pageLoad();
		return this;
	}
	
	public KarvyScrap login(String username,String password) {
		driver.findElement(By.id(usernameLocator)).sendKeys(username);
		driver.findElement(By.id(passwordLocator)).sendKeys(password);
		String captchaText = driver.findElement(By.id(captchaLocator)).getText();
		driver.findElement(By.id(captchaInputLocator)).sendKeys(captchaText);
		driver.findElement(By.id(loginSubmitLocator)).submit();
		pageLoad();
		return this;
	}
	
	public KarvyScrap queryConsole() {
		
		pageLoad();
		return this;
	}
	
	public KarvyScrap fileSelection(String fileType) {
		
		return this;
	}
	
	public KarvyScrap fileRequestDetails(String file) {
		
		return this;
	}
	
	public KarvyScrap captureReferenceNo(String file) {
		
		return this;
	}
	
	public KarvyScrap close() {
		if(this.driver!=null)
			this.driver.quit();
		
		return this;
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
}
