package org.ans.scraping;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapController {
	@Value("${karvy.login.url}")
	private String karvyUrl;
	@Autowired
	private KarvyScrap scrap;
	
	@GetMapping("/scrap")
	public String scrap() {
		WebDriver driver= LoadDriver.driver();
				scrap
				.setDriver(driver)
				.start(karvyUrl)
				.login("testusername", "ndtfdsf")
				.close();
		
		return "hello";
	}

}
