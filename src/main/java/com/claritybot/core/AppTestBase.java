package com.claritybot.core;

import com.claritybot.constants.ConstantVariable;
import com.claritybot.listeners.Retry;
import com.claritybot.utility.ExtentManager;
import com.claritybot.utility.ExtentTestManager;
import com.claritybot.utility.PropertyReader;
import com.claritybot.utility.ReadExcelData;
import com.claritybot.utility.ReadJsonData;
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

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Objects;

/**
 * @author Manoj Hans
 **/
@Listeners({
    com.claritybot.listeners.ReportListener.class, org.uncommons.reportng.HTMLReporter.class,
    com.claritybot.listeners.AnnotationTransformer.class
})
public class AppTestBase extends BrowserInstance {

    private final static Logger logger = LogManager.getLogger(AppTestBase.class);
    private final ReadJsonData readJsonData = new ReadJsonData();
    private final ReadExcelData readExcelData = new ReadExcelData();
    protected static final String DEFAULT_JSONPROVIDER = "testDataJSON";
    protected static final String DEFAULT_EXCELPROVIDER = "testDataExcel";

    @Parameters({"configFile"})
    @BeforeSuite
    public void init(@Optional("config.properties") String configFile) {
        try {
            new PropertyReader(configFile);
        } catch (Exception e) {
            throw new RuntimeException("Problem with config file");
        }
    }

    @BeforeTest(alwaysRun = true)
    public void retryFailedCases(ITestContext context) {
        for (ITestNGMethod method : context.getAllTestMethods()) {
            method.setRetryAnalyzerClass(Retry.class);
        }
    }

    @Parameters({"browser"})
    @BeforeMethod
    public void openBrowser(@Optional("firefox") String browser) throws MalformedURLException {
        initiateDriver(browser);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        ExtentTestManager.endTest();
        ExtentManager.getInstance().flush();
        if (Objects.nonNull(getDriver())) {
            logger.info("Cleaning up WebDriver...");
            getDriver().quit();
        }
    }

    @DataProvider(name = "testDataExcel")
    public Object[][] testDataProviderFromExcel(Method testMethod) {
        var sheetName = testMethod.getName();
        var filePath = getClass()
            .getClassLoader()
            .getResource(testMethod
                .getDeclaringClass()
                .getName()
                .replace(ConstantVariable.DOT, ConstantVariable.FORWARD_SLASH)
                + ".xlsx").getFile();
        logger.debug("Test data is loaded from file %s and the sheet is %s", filePath, sheetName);
        return testMethod.getName().length() > 31 ? readExcelData.getTableArray(
            filePath,
            testMethod.getName().substring(0, 31)
        ) : readExcelData.getTableArray(
            filePath,
            testMethod.getName()
        );
    }

    @DataProvider(name = "testDataJSON")
    public Object[][] testDataProviderFromJSON(Method testMethod) throws Exception {
        var keyValue = testMethod.getName();
        var filePath = getClass()
            .getClassLoader()
            .getResource(testMethod
                .getDeclaringClass()
                .getName()
                .replace(ConstantVariable.DOT, ConstantVariable.FORWARD_SLASH)
                + ".json").getFile();
        return readJsonData.getJsonValues(filePath, keyValue);
    }
}
