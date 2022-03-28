package org.ans.scraping;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LoadDriver {

	private LoadDriver() {}
	
	public static WebDriver driver() {	
		 WebDriverManager.chromedriver().setup();
	   return new ChromeDriver();
	}
}
