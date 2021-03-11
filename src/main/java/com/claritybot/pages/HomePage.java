package com.claritybot.pages;

import com.claritybot.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebDriver;

/**
 * @author Manoj Hans
 **/
public class HomePage extends BasePage {

    private final By getSignInLink = By.xpath("//div[@class='header_user_info']/a");

    public HomePage(WebDriver driver) {
        super(driver);
        if (isElementPresent(getSignInLink)) {
            throw new NoSuchWindowException("This is not a home page and page is: " + getTitle());
        }
    }

    public LoginPage clickSignInLink() {
        click(getSignInLink);
        return new LoginPage(driver);
    }
}
