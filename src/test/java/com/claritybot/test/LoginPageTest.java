package com.claritybot.test;

import com.claritybot.annotation.Steps;
import com.claritybot.core.AppTestBase;
import com.claritybot.steps.LoginPageScenarios;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * @author Manoj Hans
 */
public class LoginPageTest extends AppTestBase {

    @Steps(LoginPageScenarios.verifyLoginWithInvalidCredentials)
    @Test(dataProvider = DEFAULT_JSONPROVIDER)
    public void verifyLoginWithInvalidCredentials(Map<String, String> data) {
        var loginPage = appStart()
            .clickSignInLink()
            .typeUsername(data.get("username"))
            .typePassword(data.get("password"))
            .clickLoginBtn();
        assert loginPage.verifyLoginError("Authentication failed.") : "Expected Results: Login error should be visible";
    }

    @Steps(LoginPageScenarios.verifyLoginWithInvalidCredentials)
    @Test(dataProvider = DEFAULT_JSONPROVIDER)
    public void failLoginForcefullyForReport(Map<String, String> data) {
        var loginPage = appStart()
            .clickSignInLink()
            .typeUsername(data.get("username"))
            .typePassword(data.get("password"))
            .clickLoginBtn();
        assert loginPage.verifyLoginError("Authentication failed.") : "Expected Results: Login error should be visible";
    }

    @Steps(LoginPageScenarios.verifyLoginWithValidCredentials)
    @Test(dataProvider = DEFAULT_EXCELPROVIDER)
    public void verifyLoginWithValidCredentials(String username, String password) {
        var userPage = appStart()
            .clickSignInLink()
            .signIn(username, password);
        assert userPage.verifyLoginUser("Example test") : "Expected Results: Login should be successful";
    }
}
