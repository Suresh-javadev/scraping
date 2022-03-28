package org.ans.scraping;

import org.ans.scraping.exception.FailToLoadSiteException;
import org.ans.scraping.exception.LoginException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapController {

	@Autowired
	private KarvyScrap scrap;
	
	@PostMapping("/scrap")
	public String scrap(@RequestBody ScrapingRequest req) {
		WebDriver driver= LoadDriver.driver();
		
		try {
				scrap
				.setDriver(driver,req)
				.start()
				.close();
		}catch(FailToLoadSiteException | LoginException e) {
			return e.getMessage();
		}catch(Exception e) {
			scrap.close();
			return e.getMessage();
		}
		
		return scrap.refNo();
	}

}
