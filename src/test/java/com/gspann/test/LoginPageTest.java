package com.gspann.test;

import java.util.Map;
import org.testng.annotations.Test;
import com.gspann.annotation.Steps;
import com.gspann.core.AppTestBase;
import com.gspann.pages.LoginPage;
import com.gspann.pages.UserPage;
import com.gspann.steps.LoginPageScenarios;

/**
 * @author Manoj Hans
 */
public class LoginPageTest extends AppTestBase{

	@Steps(LoginPageScenarios.verifyLoginWithInvalidCredentials)
	@Test(dataProvider=DEFAULT_JSONPROVIDER)
	public void verifyLoginWithInvalidCredentials(Map <String, String> data){
		LoginPage loginPage= appStart()
		.clickSignInLink()
		.typeUsername(data.get("username"))
		.typePassword(data.get("password"))
		.clickLoginBtn();
		assert loginPage.verifyLoginError("Authentication failed."):"Expected Results: Login error should be visible";
	}
	
	@Steps(LoginPageScenarios.verifyLoginWithInvalidCredentials)
	@Test(dataProvider=DEFAULT_JSONPROVIDER)
	public void failLoginForcefullyForReport(Map <String, String> data){
		LoginPage loginPage=appStart()
				.clickSignInLink()
				.typeUsername(data.get("username"))
				.typePassword(data.get("password"))
				.clickLoginBtn();
		assert loginPage.verifyLoginError("Authentication failed."):"Expected Results: Login error should be visible";
	}
	
	@Steps(LoginPageScenarios.verifyLoginWithValidCredentials)
	@Test(dataProvider=DEFAULT_EXCELPROVIDER)
	public void verifyLoginWithValidCredentials(String username,String password){
		UserPage userPage=appStart()
				.clickSignInLink()
				.signIn(username,password);
		assert userPage.verifyLoginUser("Example test"):"Expected Results: Login should be successful";
	}
}
