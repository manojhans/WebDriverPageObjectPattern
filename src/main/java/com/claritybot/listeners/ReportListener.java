package com.claritybot.listeners;

import br.eti.kinoshita.testlinkjavaapi.TestLinkAPI;
import br.eti.kinoshita.testlinkjavaapi.constants.ExecutionStatus;
import br.eti.kinoshita.testlinkjavaapi.model.Build;
import br.eti.kinoshita.testlinkjavaapi.model.TestCase;
import br.eti.kinoshita.testlinkjavaapi.model.TestPlan;
import br.eti.kinoshita.testlinkjavaapi.util.TestLinkAPIException;
import com.claritybot.annotation.Steps;
import com.claritybot.annotation.TestCaseId;
import com.claritybot.core.BrowserInstance;
import com.claritybot.utility.ExtentTestManager;
import com.claritybot.utility.PropertyReader;
import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import static java.lang.String.format;
import static org.testng.Assert.assertTrue;

/**
 * @author Manoj Hans
 **/
public class ReportListener implements ITestListener {

    private static final String ESCAPE_PROPERTY = "org.uncommons.reportng.escape-output";
    private static final Logger logger = LogManager.getLogger(ReportListener.class);
    private WebDriver driver;

    private String reproSteps(ITestResult tr) {
        var testAnnotation = tr.getMethod()
            .getConstructorOrMethod()
            .getMethod()
            .getAnnotation(Steps.class);
        if (Objects.nonNull(testAnnotation)) {
            return testAnnotation.value();
        } else {
            return "Steps are missing from the annotation, please mention the steps";
        }
    }

    public void onTestStart(ITestResult result) {
        driver = ((BrowserInstance) result.getInstance()).getDriver();
        ExtentTestManager.startTest(result.getName());
        var className = result.getMethod().getTestClass().toString()
            .split("=")[1].replace("[", "").replace("]", "");
        var cap = ((RemoteWebDriver) driver)
            .getCapabilities();
        ExtentTestManager.getTest().assignCategory(
            cap.getBrowserName() + " | " + cap.getPlatform() + " | "
                + className);
    }

    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.getTest().log(LogStatus.PASS, result.getName() + " Pass");
    }

    public void onTestFailure(ITestResult result) {
        var reproSteps = reproSteps(result).split("<br>");
        ExtentTestManager.getTest().log(LogStatus.INFO, "Repro Steps: ");
        for (var steps : reproSteps) {
            ExtentTestManager.getTest().log(LogStatus.INFO, steps);
        }
        ExtentTestManager.getTest().log(LogStatus.FAIL, "Failed: " + result.getThrowable());
        Reporter.log("<h4>Repro Steps</h4><br>"
            + reproSteps(result) + "<br>");
        Reporter.log("<a href=" + screenshot(result) + ">screenshot</a><br>");
        ExtentTestManager.getTest().log(
            LogStatus.FAIL,
            ExtentTestManager.getTest().addScreenCapture(screenshot(result))
        );
    }

    public String screenshot(ITestResult tr) {
        System.setProperty(ESCAPE_PROPERTY, "false");
        var date = new Date();
        var dateFormat = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ssaa");
        var destination =
            Paths.get("test-output/screenshots", tr.getName() + dateFormat.format(date) + ".png").toFile();
        var parentDir = destination.getParentFile();
        if (!parentDir.exists()) {
            assertTrue(
                parentDir.mkdirs(),
                "Could not create directory \"" + parentDir.getAbsolutePath() + "\"."
            );
        }
        ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE).renameTo(destination);
        return destination.getAbsolutePath();
    }

    public void onTestSkipped(ITestResult result) {
        ExtentTestManager.getTest().log(LogStatus.SKIP, result.getName() + " Skipped");
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    public void onStart(ITestContext context) {}

    public void onFinish(ITestContext context) {}

    private void uploadResultsToTestLink(ITestContext testContext) {
        var testLinkHost = PropertyReader.getProperty("TestLinkHost");
        var testLinkProjectName = PropertyReader.getProperty("TestLinkProjectName");
        var testLinkPlanName = PropertyReader.getProperty("TestLinkTestPlanName");
        var testLinkBuildName = PropertyReader.getProperty("TestLinkBuildName");

        if (Objects.nonNull(testLinkHost) && !testLinkHost.isEmpty()
            && Objects.nonNull(testLinkProjectName)
            && !testLinkProjectName.isEmpty()
            && Objects.nonNull(testLinkPlanName)
            && !testLinkPlanName.isEmpty()
            && Objects.nonNull(testLinkBuildName)
            && !testLinkBuildName.isEmpty()) {

            var devKey = PropertyReader.getProperty("TestLinkApiKey");
            TestLinkAPI testLinkApi;

            try {
                var testLinkURL = new URL(testLinkHost + "/lib/api/xmlrpc.php");
                testLinkApi = new TestLinkAPI(testLinkURL, devKey);

                var testPlan = testLinkApi.getTestPlanByName(
                    testLinkPlanName, testLinkProjectName);
                var testBuild = getTestBuild(testLinkBuildName, testLinkApi,
                    testPlan
                );

                reportResult(testLinkApi, testPlan, testBuild,
                    ExecutionStatus.PASSED, testContext.getPassedTests()
                        .getAllMethods()
                );
                reportResult(testLinkApi, testPlan, testBuild,
                    ExecutionStatus.FAILED, testContext.getFailedTests()
                        .getAllMethods()
                );
                reportResult(testLinkApi, testPlan, testBuild,
                    ExecutionStatus.NOT_RUN, testContext.getSkippedTests()
                        .getAllMethods()
                );
            } catch (MalformedURLException | TestLinkAPIException e) {
                logger.error(
                    "Could not establish connection to TestLink ", e);
            }
        }
    }

    private Build getTestBuild(
        String testLinkBuildName,
        TestLinkAPI testLinkApi, TestPlan testPlan
    ) {
        return Arrays
            .stream(testLinkApi.getBuildsForTestPlan(testPlan.getId()))
            .filter(b -> testLinkBuildName.equalsIgnoreCase(b.getName()))
            .findFirst()
            .orElseThrow(
                () -> new TestLinkAPIException("Could not find build."));
    }

    private void reportResult(
        TestLinkAPI testLinkApi, TestPlan testPlan,
        Build testBuild, ExecutionStatus status,
        Collection<ITestNGMethod> testNgMethods
    ) {
        for (var testMethod : testNgMethods) {
            var testMethodName = testMethod.getMethodName();
            var testCaseIds = getExternalIdsFromAnnotations(
                testMethod,
                testMethodName
            );

            if (testCaseIds.length == 0) {
                logger.debug(format(
                    "Method [%s] hasn't associated cases from TestLink",
                    testMethodName
                ));
                continue;
            }

            logger.debug(format("Update execution result of test method [%s] to TestLink cases %s",
                testMethodName, Arrays.toString(testCaseIds)
            ));

            for (var extId : testCaseIds) {
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
                        "", null, null, null, null, null, null
                    );
                } catch (TestLinkAPIException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    private String[] getExternalIdsFromAnnotations(
        ITestNGMethod testMethod,
        String testMethodName
    ) {
        var testCaseIds = new String[0];
        try {
            testCaseIds = StringUtils.stripAll(getTestCaseIds(
                testMethod,
                testMethodName
            ));
            testCaseIds = Arrays.stream(testCaseIds).filter(a -> !a.isEmpty())
                .toArray(String[]::new);
        } catch (NoSuchMethodException e) {
            logger.error("Could not find method object by '{}' name. {}",
                testMethodName, e
            );
        }

        return testCaseIds;
    }

    private String[] getTestCaseIds(
        ITestNGMethod testMethod,
        String testMethodName
    ) throws NoSuchMethodException {
        var method = Arrays.stream(testMethod.getRealClass().getMethods())
            .filter(m -> testMethodName.equals(m.getName())).findFirst()
            .orElseThrow(NoSuchMethodException::new);
        var annotation = method.getAnnotation(TestCaseId.class);

        if (Objects.nonNull(annotation)) {
            return annotation.value().trim().split("[,| ]");
        } else {
            return new String[]{};
        }
    }
}
