package com.gspann.core;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gspann.listeners.Retry;
import com.gspann.pages.HomePage;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.velocity.texen.util.FileUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.gspann.constants.ConstantVariable;
import com.gspann.utility.ExtentManager;
import com.gspann.utility.ExtentTestManager;
import com.gspann.utility.PropertyReader;
import com.gspann.utility.ReadExcelData;

/**
 * @author Manoj Hans
 **/
public class BrowserInstance {

	private final static Logger LOGGER = LogManager.getLogger(BrowserInstance.class);
	private ThreadLocal<WebDriver> driver=new ThreadLocal<WebDriver>();
	private DesiredCapabilities caps;
	private String grid;
	
	public void initiateDriver(String browser) throws MalformedURLException{
		grid=PropertyReader.getProperty("Grid");
		LOGGER.info("Initializing "+browser);
		if(browser.equalsIgnoreCase("firefox")){
			System.setProperty("webdriver.gecko.driver",System.getProperty("user.dir")+"/drivers/geckodriver.exe");
			caps=DesiredCapabilities.firefox();
			driver.set(grid.equalsIgnoreCase("yes") ?
	             new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps):
                 new FirefoxDriver());
		}
		else if(browser.equalsIgnoreCase("chrome")){
			System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+"/drivers/chromedriver.exe");
			caps=DesiredCapabilities.chrome();
			driver.set(grid.equalsIgnoreCase("yes") ?
		             new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps):
	                 new ChromeDriver());
			}
		else if(browser.equalsIgnoreCase("ie")){
			System.setProperty("webdriver.ie.driver",System.getProperty("user.dir")+"/drivers/IEDriverServer.exe");
			caps=DesiredCapabilities.internetExplorer();
			caps.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING,true);
			caps.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
			driver.set(grid.equalsIgnoreCase("yes") ?
		             new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps):
	                 new InternetExplorerDriver());
		}
		else{
			caps=DesiredCapabilities.firefox();
			driver.set(grid.equalsIgnoreCase("yes") ?
		             new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), caps):
	                 new FirefoxDriver());
			}		
	}
	
	public HomePage appStart(){
		getDriver().manage().window().maximize();
		getDriver().get(PropertyReader.getProperty("BaseURL"));
		return new HomePage(getDriver());
	}
	
	public WebDriver getDriver(){
		return driver.get();
	}
}
