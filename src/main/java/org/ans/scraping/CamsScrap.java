package org.ans.scraping;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.ans.scraping.exception.FailToLoadSiteException;
import org.ans.scraping.exception.LoginException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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
		.filetype()
		.captureReferenceNo();
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
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/app-reports/div/div[1]/div[1]/form/div/div[2]/mat-form-field/div/div[1]/div[3]/mat-select/div/div[1]")));
		//mat-pseudo-checkbox
		WebElement activateMfselect =  this.driver.findElement(By.xpath("/html/body/app-root/div/app-reports/div/div[1]/div[1]/form/div/div[2]/mat-form-field/div/div[1]/div[3]/mat-select/div/div[1]"));
		executor.executeScript("arguments[0].click();", activateMfselect);
		
		sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("mat-pseudo-checkbox")));
		
		WebElement selectAllMf =  this.driver.findElement(By.tagName("mat-pseudo-checkbox"));
		executor.executeScript("arguments[0].click();", selectAllMf);
		//cdk-overlay-container
		
		sleep(10000);
		
		pageLoad();
		
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
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[1]/div/mat-tab-group/mat-tab-header/div[2]/div/div/div[2]/div[1]")));
		WebElement customerServicesSection =  this.driver.findElement(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[1]/div/mat-tab-group/mat-tab-header/div[2]/div/div/div[2]/div[1]"));
		executor.executeScript("arguments[0].click();", customerServicesSection);	
		sleep(2000);
		executor.executeScript("arguments[0].click();", customerServicesSection);
		//select report type wbr2
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[1]/ul/li[2]")));
		WebElement menuReport2 =  this.driver.findElement(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[1]/ul/li[2]"));
		executor.executeScript("arguments[0].click();", menuReport2);
		
		sleep(1000);
		//select email file link
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-select//div//div//span//span[contains(.,'Email an encrypted')]")));
		WebElement emailAlink =  this.driver.findElement(By.xpath("//mat-select//div//div//span//span[contains(.,'Email an encrypted')]"));
		executor.executeScript("arguments[0].click();", emailAlink);
		
		sleep(1000);
		//select email option link
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//mat-option//span[contains(.,'Email a download link')]")));
		WebElement emailAlinkOption =  this.driver.findElement(By.xpath("//mat-option//span[contains(.,'Email a download link')]"));
		executor.executeScript("arguments[0].click();", emailAlinkOption);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[2]/form/div/div/div[3]/div[1]/mat-checkbox/label/div")));
		WebElement encryptionCheckbox =  this.driver.findElement(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[2]/form/div/div/div[3]/div[1]/mat-checkbox/label/div"));
		executor.executeScript("arguments[0].click();", encryptionCheckbox);
		
		sleep(1000);
		////*[@id="mat-input-11"]
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MMM-yyyy");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[2]/form/div/div/div[29]/div/div[1]/div[1]/mat-form-field/div/div[1]/div[3]/input")));
		WebElement fromDate =  this.driver.findElement(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[2]/form/div/div/div[29]/div/div[1]/div[1]/mat-form-field/div/div[1]/div[3]/input"));
	
		executor.executeScript("arguments[0].removeAttribute('readonly','readonly')",fromDate);
		sleep(1000);
		executor.executeScript("arguments[0].setAttribute('value','')",fromDate);
		sleep(1000);
		fromDate.sendKeys(Keys.CONTROL + "a");
		fromDate.sendKeys(Keys.DELETE);
		fromDate.sendKeys(sdf.format(this.input.getFromdate()));
		sleep(1000);
		
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[2]/form/div/div/div[29]/div/div[1]/div[2]/mat-form-field/div/div[1]/div[3]/input")));
		WebElement toDate =  this.driver.findElement(By.xpath("/html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[2]/form/div/div/div[29]/div/div[1]/div[2]/mat-form-field/div/div[1]/div[3]/input"));
		sleep(1000);
		executor.executeScript("arguments[0].removeAttribute('readonly','readonly')",toDate);
		sleep(1500);
		executor.executeScript("arguments[0].setAttribute('value','')",toDate);
		sleep(2500);
		
		toDate.sendKeys(Keys.CONTROL + "a");
		toDate.sendKeys(Keys.DELETE);
		toDate.sendKeys(sdf.format(this.input.getTodate()));
		
		//select form submit
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='submit' and @value='Next']")));
		WebElement formsubmit =  this.driver.findElement(By.xpath("//input[@type='submit' and @value='Next']"));
		executor.executeScript("arguments[0].click();", formsubmit);
		
		//zippass1
		// /html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[3]/div/div/form/div/div/div[8]/div[1]/mat-form-field/div/div[1]/div[3]/input
		
		sleep(1000);
		//select form submit
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password' and @formcontrolname='pwd1']")));
		WebElement zippass1 =  this.driver.findElement(By.xpath("//input[@type='password' and @formcontrolname='pwd1']"));
		zippass1.sendKeys(this.input.getZipPassword());
		
		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='password' and @formcontrolname='pwd2']")));
		WebElement zippass2 =  this.driver.findElement(By.xpath("//input[@type='password' and @formcontrolname='pwd2']"));
		zippass2.sendKeys(this.input.getZipPassword());
		
		
		// /html/body/app-root/div/app-reports/div/div[2]/div/div[2]/div[2]/div[3]/div/div/form/div/div/div[10]/div[2]/input[1]
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@type='Submit' and @value='IMMEDIATE']")));
		WebElement imediateSubmit =  this.driver.findElement(By.xpath("//input[@type='Submit' and @value='IMMEDIATE']"));
		executor.executeScript("arguments[0].click();", imediateSubmit);
		
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
		List<WebElement> allParag=this.driver.findElements(By.tagName("b"));
		
		for(WebElement p:allParag) {
			if(p.getText().contains(this.input.getFiletype().toUpperCase())) {
				
				this.refNo = p.getText().replace(" ", "").trim().replace("WB", "");
				
				break;
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
	
	private void sleep(int mili) {
		try {
			Thread.sleep(mili);
		}catch(Exception e) {}
	}
}
