package org.ans.scraping;

import org.ans.scraping.exception.FailToLoadSiteException;
import org.ans.scraping.exception.LoginException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScrapController {

	@Autowired
	private ApplicationContext context;
	
	@PostMapping("/scrap/karvy")
	public ResponseEntity<ScrapResponse> scrap(@RequestBody ScrapingRequest req) {
		ScrapResponse resp=new ScrapResponse();
		WebDriver driver= LoadDriver.driver();
		KarvyScrap scrap=context.getBean(KarvyScrap.class);
		try {			
				scrap
				.setDriver(driver,req)
				.start()
				.close();
				
				resp.setRefNo(scrap.refNo());
				resp.setStatus(ScrapResponse.Status.SUCCESS);
		}catch(FailToLoadSiteException | LoginException e) {
			resp.setStatus(ScrapResponse.Status.ERROR);
			resp.setMessage(e.getMessage());
		}catch(RuntimeException e) {
			scrap.close();
			
			resp.setStatus(ScrapResponse.Status.ERROR);
			
			if(e.getMessage().contains("Alert text : User Name and Password does not match"))
				resp.setMessage("User Name and Password does not match");
			else
				resp.setMessage(e.getMessage());
		}catch(Exception e) {
			scrap.close();
			
			resp.setStatus(ScrapResponse.Status.ERROR);
			resp.setMessage(e.getMessage());
		}
		
		return ResponseEntity.ok(resp);
	}

}
