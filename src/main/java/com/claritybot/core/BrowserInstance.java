package com.claritybot.core;

import com.claritybot.pages.HomePage;
import com.claritybot.utility.PropertyReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Manoj Hans
 **/
public class BrowserInstance {

    private final static Logger logger = LogManager.getLogger(BrowserInstance.class);
    private final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    protected void initiateDriver(String browser) throws MalformedURLException {
        DesiredCapabilities caps;
        String grid = PropertyReader.getProperty("Grid");
        logger.info("Initializing " + browser);
        if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            caps = DesiredCapabilities.firefox();
            driver.set(grid.equalsIgnoreCase("yes") ?
                new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps) :
                new FirefoxDriver());
        } else if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            caps = DesiredCapabilities.chrome();
            driver.set(grid.equalsIgnoreCase("yes") ?
                new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps) :
                new ChromeDriver());
        } else if (browser.equalsIgnoreCase("ie")) {
            WebDriverManager.iedriver().setup();
            caps = DesiredCapabilities.internetExplorer();
            caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
            caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            driver.set(grid.equalsIgnoreCase("yes") ?
                new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps) :
                new InternetExplorerDriver());
        } else {
            WebDriverManager.firefoxdriver().setup();
            caps = DesiredCapabilities.firefox();
            driver.set(grid.equalsIgnoreCase("yes") ?
                new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps) :
                new FirefoxDriver());
        }
    }

    public HomePage appStart() {
        getDriver().manage().window().maximize();
        getDriver().get(PropertyReader.getProperty("BaseURL"));
        return new HomePage(getDriver());
    }

    public WebDriver getDriver() {
        return driver.get();
    }
}
