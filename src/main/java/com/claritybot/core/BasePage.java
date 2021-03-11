package com.claritybot.core;

import com.claritybot.utility.ExtentTestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static com.relevantcodes.extentreports.LogStatus.INFO;

/**
 * @author Manoj Hans
 **/
public class BasePage {

    private final static Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    private final WebDriverWait wait;
    private static final long DEFAULT_WAIT_TIMEOUT_SEC = 10;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, DEFAULT_WAIT_TIMEOUT_SEC);
    }

    protected void infoLog(String info) {
        ExtentTestManager.getTest().log(INFO, info);
        logger.info(info);
    }

    protected void type(By loc, String value) {
        var element = waitForElementPresent(loc);
        infoLog("element found: " + loc);
        infoLog("Typing '" + value + "' into " + loc);
        element.sendKeys(value);
    }

    protected void click(By loc) {
        var element = waitForElementPresent(loc);
        infoLog("clicking on element: " + loc);
        element.click();
    }

    protected boolean textEqualTo(By loc, String value) {
        return waitForElementPresent(loc).getText().equals(value);
    }

    protected boolean textContains(By loc, String value) {
        return waitForElementPresent(loc).getText().contains(value);
    }

    protected String getTitle() {
        return driver.getTitle();
    }

    protected WebElement waitForElementPresent(By loc) {
        infoLog("Waiting for element: " + loc);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(loc));
    }

    protected boolean isElementPresent(By loc) {
        try {
            waitForElementPresent(loc);
            return false;
        } catch (TimeoutException t) {
            return true;
        }
    }
}
