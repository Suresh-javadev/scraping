package org.ans.scraping;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.ans.scraping.exception.FailToLoadSiteException;
import org.ans.scraping.exception.LoginException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope(scopeName="prototype")
public class CamsScrap implements Closeable{
	
	@Value("${cams.base.url}")
	private String camsUrl;
	@Value("${cams.login.url}")
	private String camsLoginUrl;
	
	private WebDriver driver;
	private String refNo="";
	private FileRequestInput input;
	
	public CamsScrap setDriver(WebDriver driver,FileRequestInput input) {
		this.driver = driver;
		this.input = input;
		
		Assert.hasLength(this.input.getUsername(), "Username is required");
		Assert.hasLength(this.input.getFiletype(), "Filetype is required");
		
		return this;
	}
	
	public CamsScrap start() throws FailToLoadSiteException, LoginException, IOException{
		this.driver.get(camsUrl);
		pageLoad();
		
		if(!isSiteWorking()) {
			this.close();
			throw new FailToLoadSiteException("Site is down or not reachable");
		}
		
		return this
		.login()
		.filetype();
		//.captureReferenceNo();
	}
	
	private CamsScrap login() throws LoginException{
		pageLoad();
		
		acceptModalTandC();
		
		this.driver.get(camsLoginUrl);
		
		pageLoad();
		
		WebElement email=this.driver.findElement(By.id("mat-input-0"));
		email.sendKeys(this.input.getUsername());
		
		
		WebElement proceedButton =  this.driver.findElement(By.xpath("//input[@type='Submit' and @value='Submit']"));
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		executor.executeScript("arguments[0].click();", proceedButton);
		pageLoad();
		
		WebDriverWait wait = new WebDriverWait(this.driver, 20);
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("mat-select")));
		//mat-pseudo-checkbox
		WebElement activateMfselect =  this.driver.findElement(By.tagName("mat-select"));
		executor.executeScript("arguments[0].click();", activateMfselect);
		
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("mat-pseudo-checkbox")));
		
		WebElement selectAllMf =  this.driver.findElement(By.tagName("mat-pseudo-checkbox"));
		executor.executeScript("arguments[0].click();", selectAllMf);
		//cdk-overlay-container
		pageLoad();
		WebElement overlay =  this.driver.findElement(By.className("cdk-overlay-container"));
		executor.executeScript("arguments[0].click();", overlay);
		
		
		return this;
	}
	
	public CamsScrap filetype() throws IOException {
		
		if(this.input.getFiletype().equalsIgnoreCase("wbr22")) {
			this.wbr22();
		}else if(this.input.getFiletype().equalsIgnoreCase("wbr2")) {
			this.wbr2();
		}
		return this;
	}
	
	private void acceptModalTandC() {
		
		try {
			WebElement modalAccept = this.driver.findElement(By.id("mat-radio-2-input"));
		
			if(modalAccept !=null) {
				JavascriptExecutor executor = (JavascriptExecutor)driver;
				executor.executeScript("arguments[0].click();", modalAccept);
				
				WebElement proceedButton =  this.driver.findElement(By.xpath("//input[@type='button' and @value='PROCEED']"));
				
				executor.executeScript("arguments[0].click();", proceedButton);
			}
			
		}catch(NoSuchElementException e) {}

	}
	
	/**
	 * Folio wise trxn file
	 * @throws IOException
	 */
	public void wbr2() throws IOException {
		WebDriverWait wait = new WebDriverWait(this.driver, 20);
		
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		
		//report section
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='mat-tab-label-0-1' and @class='mat-tab-label mat-ripple ng-star-inserted']")));
		WebElement customerServicesSection =  this.driver.findElement(By.xpath("//div[@id='mat-tab-label-0-1' and @class='mat-tab-label mat-ripple ng-star-inserted']"));
		executor.executeScript("arguments[0].click();", customerServicesSection);
		executor.executeScript("arguments[0].click();", customerServicesSection);
		
		//select report type wbr2
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul//li[contains(.,'WBR2.')]")));
		WebElement menuReport2 =  this.driver.findElement(By.xpath("//ul//li[contains(.,'WBR2.')]"));
		executor.executeScript("arguments[0].click();", menuReport2);
				
		//select email file link
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-select//div//div//span//span[contains(.,'Email an encrypted')]")));
		WebElement emailAlink =  this.driver.findElement(By.xpath("//mat-select//div//div//span//span[contains(.,'Email an encrypted')]"));
		executor.executeScript("arguments[0].click();", emailAlink);
		
		
		//select email option link
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-option//span[contains(.,'Email a download link')]")));
		WebElement emailAlinkOption =  this.driver.findElement(By.xpath("//mat-option//span[contains(.,'Email a download link')]"));
		executor.executeScript("arguments[0].click();", emailAlinkOption);
		
		//mat-checkbox-2
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='checkbox' and @class='mat-checkbox-input cdk-visually-hidden']")));
		WebElement encryptionCheckbox =  this.driver.findElement(By.xpath("//input[@type='checkbox' and @class='mat-checkbox-input cdk-visually-hidden']"));
		executor.executeScript("arguments[0].click();", encryptionCheckbox);
		
	    pageLoad();
	}
	
	/**
	 * <p>AUM file
	 * @throws IOException
	 */
	public void wbr22() throws IOException {
		//filesection selection -- mat-tab-label-0-1 = customer services
		WebDriverWait wait = new WebDriverWait(this.driver, 20);
		
		JavascriptExecutor executor = (JavascriptExecutor)driver;
		//report section
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mat-tab-label-0-1")));
		WebElement customerServicesSection =  this.driver.findElement(By.id("mat-tab-label-0-1"));
		executor.executeScript("arguments[0].click();", customerServicesSection);
		
		//select report type wbr22
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul//li[contains(.,'WBR22.')]")));
		WebElement menuReport22 =  this.driver.findElement(By.xpath("//ul//li[contains(.,'WBR22.')]"));
		executor.executeScript("arguments[0].click();", menuReport22);

		//mat-checkbox-2
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='checkbox' and @class='mat-checkbox-input cdk-visually-hidden']")));
		WebElement encryptionCheckbox =  this.driver.findElement(By.xpath("//input[@type='checkbox' and @class='mat-checkbox-input cdk-visually-hidden']"));
		executor.executeScript("arguments[0].click();", encryptionCheckbox);
		
		
		//select email file link
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-select//div//div//span//span[contains(.,'Email an encrypted')]")));
		WebElement emailAlink =  this.driver.findElement(By.xpath("//mat-select//div//div//span//span[contains(.,'Email an encrypted')]"));
		executor.executeScript("arguments[0].click();", emailAlink);
	    pageLoad();
	}
	
	public CamsScrap captureReferenceNo() {
		//this logic can change file to file need fix accordingly
		List<WebElement> allParag=this.driver.findElements(By.tagName("p"));
		
		String txt="execution queue with the reference number ";
		for(WebElement p:allParag) {
			if(p.getText().contains(txt) && this.input.getFiletype().equalsIgnoreCase("mfsd246")) {
				String s=p.getText();
				int index=s.indexOf(txt);
				String ref=s.substring(index+txt.length(), index+txt.length()+15);
		
				this.refNo = ref.substring(0,ref.indexOf(" ."));
				
				break;
			}else if(p.getText().contains(txt) && this.input.getFiletype().equalsIgnoreCase("mfsd203")) {
				String s=p.findElement(By.tagName("b")).getText().trim();
				
				this.refNo =s;
			}
		}
		
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
		return this.driver.getPageSource().contains("Terms and Conditions") || this.driver.getPageSource().contains("Distributor Mailback Service");
	}
	
	private boolean isAlertPresent(){ 
	    try { 
	        this.driver.switchTo().alert(); 
	        return true; 
	    } catch (NoAlertPresentException Ex){ 
	        return false; 
	    }   
	}
}
