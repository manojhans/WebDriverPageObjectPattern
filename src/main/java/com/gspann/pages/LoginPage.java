package com.gspann.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

import com.gspann.core.BasePage;
import com.gspann.utility.ExtentTestManager;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Manoj Hans
 **/
public class LoginPage extends BasePage{

	private By getUsername= By.id("email");
	private By getPassword=By.id("passwd");
	private By getloginBtn=By.id("SubmitLogin");
	private By getLoginError=By.xpath("//div[@class='alert alert-danger']//li");
	
	public LoginPage(WebDriver driver) {
		super(driver);
		if(!isElementPresent(getloginBtn)){
			throw new NoSuchWindowException("This is not a login page and page is: "+getTitle());
		}
	}
	
	public LoginPage typeUsername(String username){
		type(getUsername, username);
		return this;
	}
	public LoginPage typePassword(String password){
		type(getPassword, password);
		return this;
	}
	public LoginPage clickLoginBtn(){
		click(getloginBtn);
		return this;
	}
	
	public boolean verifyLoginError(String value){
		return textContains(getLoginError, value);
	}
	
	public UserPage signIn(String username,String password){
		type(getUsername, username);
		type(getPassword, password);
		click(getloginBtn);
		return new UserPage(driver);
	}

}
