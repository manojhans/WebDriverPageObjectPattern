package com.gspann.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;
import com.gspann.core.BasePage;

/**
 * @author Manoj Hans
 **/
public class UserPage extends BasePage{
	
	private By getVerifyUser=By.xpath("//a[@title='View my customer account']/span");

	public UserPage(WebDriver driver) {
		super(driver);
		if(!isElementPresent(getVerifyUser)){
			throw new NoSuchWindowException("This is not a user page and page is: "+getTitle());
		}
	}
	public boolean verifyLoginUser(String value){
		return textEqualTo(getVerifyUser, value);
	}

}
