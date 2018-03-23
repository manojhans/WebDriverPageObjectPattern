package com.gspann.listeners;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;

import com.gspann.annotation.Steps;
import com.gspann.annotation.TestCaseId;
import com.gspann.core.BrowserInstance;
import com.gspann.utility.ExtentTestManager;
import com.gspann.utility.PropertyReader;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @author Manoj Hans
 **/
public class ReportListener implements ITestListener {
	private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
	private static final Logger log = LogManager.getLogger(ReportListener.class);
	private WebDriver driver;
	private String methodName = "";
	private String description = "";
	private By by;
	private String[] reproSteps;
	
	private String reproSteps(ITestResult tr) {
		ITestNGMethod testNGMethod = tr.getMethod();
		Method method = testNGMethod.getConstructorOrMethod().getMethod();
		Steps testAnnotation = (Steps) method
				.getAnnotation(Steps.class);
		if (Objects.nonNull(testAnnotation))
			return testAnnotation.value();
		else
			return "Steps are missing from the annotation, please mention the steps";
	}
	
	public void onTestStart(ITestResult result) {
		methodName = result.getName();
		description = result.getMethod().getDescription();
		Object executedClass = result.getInstance();
		driver = ((BrowserInstance)executedClass).getDriver();
		ExtentTestManager.startTest(result.getName());
		String className = result.getMethod().getTestClass().toString()
				.split("=")[1].replace("[", "").replace("]", "");
		Capabilities cap1 = ((RemoteWebDriver) driver)
				.getCapabilities();
		ExtentTestManager.getTest().assignCategory(
				cap1.getBrowserName() + " | " + cap1.getPlatform() + " | "
						+ className);
	}

	public void onTestSuccess(ITestResult result) {
		ExtentTestManager.getTest().log(LogStatus.PASS,result.getName() + " Pass");
	}

	public void onTestFailure(ITestResult result) {
		reproSteps=reproSteps(result).split("<br>");
		ExtentTestManager.getTest().log(LogStatus.INFO,"Repro Steps: ");
		for(String steps:reproSteps){
		ExtentTestManager.getTest().log(LogStatus.INFO,steps);
		}
		ExtentTestManager.getTest().log(LogStatus.FAIL,"Failed: " + result.getThrowable());
		try {
			Reporter.log("<h4>Repro Steps</h4><br>"
					+ reproSteps(result) + "<br>");
			Reporter.log("<a href=" + screenshot(result) + ">screenshot</a><br>");
			ExtentTestManager.getTest().log(LogStatus.FAIL,
		    ExtentTestManager.getTest().addScreenCapture(screenshot(result)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String screenshot(ITestResult tr) throws IOException {
		System.setProperty(ESCAPE_PROPERTY, "false");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ssaa");
		File destination=Paths.get("test-output/screenshots", tr.getName()+dateFormat.format(date)+".png").toFile();
		File parentDir = destination.getParentFile();
		if (!parentDir.exists()) {
            Assert.assertTrue(parentDir.mkdirs(), "Could not create directory \"" + parentDir.getAbsolutePath() + "\".");
        }
		TakesScreenshot scrShot = (TakesScreenshot)driver;
        File snapshot = (File)scrShot.getScreenshotAs(OutputType.FILE);
        snapshot.renameTo(destination);
		return destination.getAbsolutePath();
	}

	public void onTestSkipped(ITestResult result) {
		ExtentTestManager.getTest().log(LogStatus.SKIP,result.getName()+" Skipped");
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

	public void onStart(ITestContext context) {}

	public void onFinish(ITestContext context) {}

	private void uploadResultsToTestLink(ITestContext testContext) {
		String testLinkHost = PropertyReader.getProperty("TestLinkHost");
		String testLinkProjectName = PropertyReader.getProperty("TestLinkProjectName");
		String testLinkPlanName = PropertyReader.getProperty("TestLinkTestPlanName");
		String testLinkBuildName = PropertyReader.getProperty("TestLinkBuildName");

		if (Objects.nonNull(testLinkHost) && !testLinkHost.isEmpty()
				&& Objects.nonNull(testLinkProjectName)
				&& !testLinkProjectName.isEmpty()
				&& Objects.nonNull(testLinkPlanName)
				&& !testLinkPlanName.isEmpty()
				&& Objects.nonNull(testLinkBuildName)
				&& !testLinkBuildName.isEmpty()) {

			String devKey = PropertyReader.getProperty("TestLinkApiKey");
			TestLinkAPI testLinkApi = null;

			try {
				URL testLinkURL = new URL(testLinkHost + "/lib/api/xmlrpc.php");
				testLinkApi = new TestLinkAPI(testLinkURL, devKey);

				TestPlan testPlan = testLinkApi.getTestPlanByName(
						testLinkPlanName, testLinkProjectName);
				Build testBuild = getTestBuild(testLinkBuildName, testLinkApi,
						testPlan);

				reportResult(testLinkApi, testPlan, testBuild,
						ExecutionStatus.PASSED, testContext.getPassedTests()
								.getAllMethods());
				reportResult(testLinkApi, testPlan, testBuild,
						ExecutionStatus.FAILED, testContext.getFailedTests()
								.getAllMethods());
				reportResult(testLinkApi, testPlan, testBuild,
						ExecutionStatus.NOT_RUN, testContext.getSkippedTests()
								.getAllMethods());
			} catch (MalformedURLException | TestLinkAPIException e) {
				log.error(
						"Could not establish connection to TestLink ",e);
			}
		}
	}

	private Build getTestBuild(String testLinkBuildName,
			TestLinkAPI testLinkApi, TestPlan testPlan) {
		return Arrays
				.stream(testLinkApi.getBuildsForTestPlan(testPlan.getId()))
				.filter(b -> testLinkBuildName.equalsIgnoreCase(b.getName()))
				.findFirst()
				.orElseThrow(
						() -> new TestLinkAPIException("Could not find build."));
	}

	private void reportResult(TestLinkAPI testLinkApi, TestPlan testPlan,
			Build testBuild, ExecutionStatus status,
			Collection<ITestNGMethod> testNgMethods) {
		for (ITestNGMethod testMethod : testNgMethods) {
			String testMethodName = testMethod.getMethodName();
			String[] testCaseIds = getExternalIdsFromAnnotations(testMethod,
					testMethodName);

			if (testCaseIds.length == 0) {
				log.debug(String.format(
						"Method [%s] hasn't associated cases from TestLink",
						testMethodName));
				continue;
			}

			log.debug(String
					.format("Update execution result of test method [%s] to TestLink cases %s",
							testMethodName, Arrays.toString(testCaseIds)));

			for (String extId : testCaseIds) {
				try {
					TestCase testLinkCase = testLinkApi
							.getTestCaseByExternalId(extId, null);

					// #reportTCResult(Integer testCaseId, Integer
					// testCaseExternalId, Integer testPlanId,
					// ExecutionStatus status, Integer buildId, String
					// buildName, String notes, Boolean guess, String bugId,
					// Integer platformId, String platformName, Map<String,
					// String> customFields, Boolean overwrite
					testLinkApi.reportTCResult(testLinkCase.getId(), null,
							testPlan.getId(), status, testBuild.getId(), null,
							"", null, null, null, null, null, null);
				} catch (TestLinkAPIException e) {
					log.error(e.getMessage());
				}
			}
		}
	}

	private String[] getExternalIdsFromAnnotations(ITestNGMethod testMethod,
			String testMethodName) {
		String[] testCaseIds = new String[0];
		try {
			testCaseIds = StringUtils.stripAll(getTestCaseIds(testMethod,
					testMethodName));
			testCaseIds = Arrays.stream(testCaseIds).filter(a -> !a.isEmpty())
					.toArray(String[]::new);
		} catch (NoSuchMethodException e) {
			log.error("Could not find method object by '{}' name. {}",
					testMethodName, e);
		}

		return testCaseIds;
	}

	private String[] getTestCaseIds(ITestNGMethod testMethod,
			String testMethodName) throws NoSuchMethodException {
		Method method = Arrays.stream(testMethod.getRealClass().getMethods())
				.filter(m -> testMethodName.equals(m.getName())).findFirst()
				.orElseThrow(NoSuchMethodException::new);
		TestCaseId annotation = method.getAnnotation(TestCaseId.class);

		if (Objects.nonNull(annotation)) {
			return annotation.value().trim().split("[,| ]");
		} else {
			return new String[] {};
		}
	}

}
