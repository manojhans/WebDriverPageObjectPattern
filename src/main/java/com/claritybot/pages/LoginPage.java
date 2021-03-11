package com.claritybot.pages;

import com.claritybot.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

/**
 * @author Manoj Hans
 **/
public class LoginPage extends BasePage {

    private final By getUsername = By.id("email");
    private final By getPassword = By.id("passwd");
    private final By getLoginBtn = By.id("SubmitLogin");
    private final By getLoginError = By.xpath("//div[@class='alert alert-danger']//li");

    public LoginPage(WebDriver driver) {
        super(driver);
        if (isElementPresent(getLoginBtn)) {
            throw new NoSuchWindowException("This is not a login page and page is: " + getTitle());
        }
    }

    public LoginPage typeUsername(String username) {
        type(getUsername, username);
        return this;
    }

    public LoginPage typePassword(String password) {
        type(getPassword, password);
        return this;
    }

    public LoginPage clickLoginBtn() {
        click(getLoginBtn);
        return this;
    }

    public boolean verifyLoginError(String value) {
        return textContains(getLoginError, value);
    }

    public UserPage signIn(String username, String password) {
        type(getUsername, username);
        type(getPassword, password);
        click(getLoginBtn);
        return new UserPage(driver);
    }
}
