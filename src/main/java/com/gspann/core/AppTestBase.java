package com.gspann.core;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gspann.constants.ConstantVariable;
import com.gspann.listeners.Retry;
import com.gspann.utility.ExtentManager;
import com.gspann.utility.ExtentTestManager;
import com.gspann.utility.PropertyReader;
import com.gspann.utility.ReadExcelData;
import com.gspann.utility.ReadJsonData;

/**
 * @author Manoj Hans
 **/
@Listeners({com.gspann.listeners.ReportListener.class, org.uncommons.reportng.HTMLReporter.class,
	com.gspann.listeners.AnnotationTransformer.class})
public class AppTestBase extends BrowserInstance {
	
	private final static Logger LOGGER = LogManager.getLogger(AppTestBase.class);  
	private PropertyReader prop;
	private ReadJsonData readJsonData=new ReadJsonData();
	private ReadExcelData readExcelData=new ReadExcelData();
	protected static final String DEFAULT_JSONPROVIDER = "testDataJSON";
	protected static final String DEFAULT_EXCELPROVIDER = "testDataExcel";
	
	@Parameters({"configFile"})
	@BeforeSuite
	public void init(@Optional("config.properties") String configFile){
		 try {
			 prop=new PropertyReader(configFile);
		     } catch (Exception e) {
		      e.printStackTrace();
		      throw new RuntimeException("Problem with config file");
		    }
	}
	
	@BeforeTest(alwaysRun = true)
	public void retryFailedCases(ITestContext context) {
		for (ITestNGMethod method : context.getAllTestMethods()) {
			method.setRetryAnalyzer(new Retry());
		}
	}

	@Parameters({"browser"})
	@BeforeMethod
	public void openBrowser(@Optional("firefox") String browser) throws MalformedURLException{
		initiateDriver(browser);
	}
	
	@AfterMethod
	public void tearDown(ITestResult result){
	ExtentTestManager.endTest();
	ExtentManager.getInstance().flush();
	if(Objects.nonNull(getDriver())){
		LOGGER.info("Cleaning up WebDriver...");
		getDriver().quit();
	}
  }	
	
	@DataProvider(name = "testDataExcel")
	public Object[][] testDataProviderFromExcel(Method testMethod) throws Exception {
		String sheetName = testMethod.getName();
		String filePath = "src/main/resources/"
				+ testMethod
						.getDeclaringClass()
						.getName()
						.replace(ConstantVariable.DOT, ConstantVariable.FORWARD_SLASH)
				+ ".xlsx";
		LOGGER.debug("Test data is loaded from file " + filePath
				+ " and the sheet is " + sheetName);
		Object[][] testObjArray;
		if(testMethod.getName().length()>31){
		testObjArray = readExcelData.getTableArray(filePath,
				testMethod.getName().substring(0, 31));
		} else{
		testObjArray = readExcelData.getTableArray(filePath,
					testMethod.getName());	
		}
		return testObjArray;
	}
	
	@DataProvider(name = "testDataJSON")
	public Object[][] testDataProviderFromJSON(Method testMethod) throws Exception {
		String keyValue = testMethod.getName();
		String filePath = "src/main/resources/"
				+ testMethod
						.getDeclaringClass()
						.getName()
						.replace(ConstantVariable.DOT, ConstantVariable.FORWARD_SLASH)
				+ ".json";
		Object[][] testObjArray=readJsonData.getJsonValues(filePath, keyValue);
		return testObjArray;
	}
}