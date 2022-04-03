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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@Scope(scopeName="prototype")
public class KarvyScrap implements Closeable{
	
	
	@Value("${basepath}")
	private String basepath;	
	
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
	
	//MFSD246
	@Value("${karvy.MFSD246.url}")
	private String mfsd246Url;
	@Value("${karvy.mfsd246.asondate.locator}")
	private String mfsd246AsondateLocator;
	@Value("${karvy.mfsd246.amcselect.locator}")
	private String mfsd246AmcSelectLocator;
	@Value("${karvy.mfsd246.email0.locator}")
	private String mfsd246EmailLocator;
	@Value("${karvy.mfsd246.fileupload.locator}")
	private String mfsd246FileuploadLocator;
	@Value("${karvy.mfsd246.filedbf.locator}")
	private String mfsd246dbffileLocator;
	@Value("${karvy.mfsd246.submit.locator}")
	private String mfsd246submitLocator;
	
	
	//mfsd203 aum
	@Value("${karvy.MFSD203.url}")
	private String mfsd203Url;
	@Value("${karvy.mfsd203.asondateradio.locator}")
	private String karvyMfsd203asonradioLocator;
	@Value("${karvy.mfsd203.asondateinput.locator}")
	private String karvyMfsd203asoninputLocator;
	@Value("${karvy.mfsd203.allamcselect.locator}")
	private String karvyMfsd203allAmcSelctLocator;
	@Value("${karvy.mfsd203.emailselect.locator}")
	private String karvyMfsd203emailselctLocator;
	@Value("${karvy.mfsd203.withzerobal.locator}")
	private String karvyMfsd203withzerobalLocator;
	@Value("${karvy.mfsd203.dbffile.locator}")
	private String karvyMfsd203dbffileLocator;
	@Value("${karvy.mfsd203.zippass1.locator}")
	private String karvyMfsd203zippas1Locator;
	@Value("${karvy.mfsd203.zippass2.locator}")
	private String karvyMfsd203zippas2Locator;
	@Value("${karvy.mfsd203.submit.locator}")
	private String karvyMfsd203submitLocator;

	
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
	
	public KarvyScrap start() throws FailToLoadSiteException, LoginException, IOException{
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
	
	public KarvyScrap filetype() throws IOException {
		
		if(this.input.getFiletype().equalsIgnoreCase("mfsd246")) {
			this.mfsd246();
		}else if(this.input.getFiletype().equalsIgnoreCase("mfsd203")) {
			this.mfsd203();
		}
		return this;
	}
	
	/**
	 * Folio wise trxn file
	 * @throws IOException
	 */
	public void mfsd246() throws IOException {
		this.driver.get(mfsd246Url);
		pageLoad();
		Assert.hasLength(this.input.getAmc(), "For mfsd246 amc can't be null");
	    Assert.notNull(this.input.getFoliolist(), "For mfsd246 folio list can't be null");
	    Assert.isTrue(!this.input.getFoliolist().isEmpty(),"Folio list size can't be zero");
	    Assert.isTrue(this.input.getFoliolist().size()<=500,"Folio list size can't be grater than 500");
	    JavascriptExecutor jse = (JavascriptExecutor)this.driver;
	    
	    if(this.input.getAsOnDate()) {
	    	
	    	jse.executeScript("document.getElementById('"+mfsd246AsondateLocator+"').click();");
	    }else{
	    	  Assert.notNull(this.input.getFromdate(), "From date required");
	    	  Assert.notNull(this.input.getTodate(), "To Date required");
	    }
	    
	    
	    Select amc = new Select(driver.findElement(By.id(mfsd246AmcSelectLocator)));
	    amc.selectByValue(this.input.getAmc());
	    
	    
	    
	    WebElement uploadElement = this.driver.findElement(By.id(mfsd246FileuploadLocator));

	    String filePath=FileCreationForFolioUpload.createFileFromFolio( this.input.getFoliolist());
        // enter the file path onto the file-selection input field
	    String filepath=basepath+filePath;
        uploadElement.sendKeys(filepath);
        
    	jse.executeScript("document.getElementById('"+mfsd246EmailLocator+"').click();");
    	
    	jse.executeScript("document.getElementById('"+mfsd246dbffileLocator+"').click();");
    	
    	
    	driver.findElement(By.id("ctl00_MiddleContent_filefrmt_txtZipPwd")).sendKeys(input.getZipPassword());
    	
    	driver.findElement(By.id("ctl00_MiddleContent_filefrmt_txtconfirmzippwd")).sendKeys(input.getZipPassword());
    	
    	jse.executeScript("document.getElementById('"+mfsd246submitLocator+"').click();");
	    
	    pageLoad();
	}
	
	/**
	 * <p>AUM file
	 * @throws IOException
	 */
	public void mfsd203() throws IOException {
		this.driver.get(mfsd203Url);
		pageLoad();
	    JavascriptExecutor jse = (JavascriptExecutor)this.driver;
	    
	    if(this.input.getAsOnDate()) {
	    	
	    	jse.executeScript("document.getElementById('"+karvyMfsd203asonradioLocator+"').click();");
	    	
	    	if(this.input.getTodate()!=null) {
	    		SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
	    		try {
	    			String date= sdf.format(this.input.getTodate())	;
	    			driver.findElement(By.id(karvyMfsd203asoninputLocator)).sendKeys(date);
	    		}catch(Exception e) {}
	    	}	
	    	
	    }else{
	    	  Assert.isTrue(false, "as on date should be true for aum file");
	    }
	    
	    //select all amc
	    jse.executeScript("document.getElementById('"+karvyMfsd203allAmcSelctLocator+"').click();");
	    
	    //select email
	    jse.executeScript("document.getElementById('"+karvyMfsd203emailselctLocator+"').click();");
        
	    //select with zero balance
    	jse.executeScript("document.getElementById('"+karvyMfsd203withzerobalLocator+"').click();");
    	
    	//select dbf file
    	jse.executeScript("document.getElementById('"+karvyMfsd203dbffileLocator+"').click();");
    	
 
    	driver.findElement(By.id(karvyMfsd203zippas1Locator)).sendKeys(input.getZipPassword());
    	
    	driver.findElement(By.id(karvyMfsd203zippas2Locator)).sendKeys(input.getZipPassword());
    	
    	jse.executeScript("document.getElementById('"+karvyMfsd203submitLocator+"').click();");
	    
	    pageLoad();
	}
	
	public KarvyScrap captureReferenceNo() {
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
