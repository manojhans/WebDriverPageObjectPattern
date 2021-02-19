package com.claritybot.listeners;

import com.claritybot.utility.PropertyReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * @author Manoj Hans
 **/
public class Retry implements IRetryAnalyzer {

    private static final Logger logger = LogManager.getLogger(Retry.class);
    private int count = 0;
    private static int maxTry = 0;

    public Retry() {
        maxTry = Integer.parseInt(PropertyReader.getProperty("MaxFailedTestsRetryCount"));
    }

    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {
            if (count < maxTry) {
                logger.info(" Re-running " + iTestResult.getMethod().getMethodName() + " once again.");
                count++;
                iTestResult.setStatus(ITestResult.FAILURE);
                return true;
            } else {
                iTestResult.setStatus(ITestResult.FAILURE);
            }
        } else {
            iTestResult.setStatus(ITestResult.SUCCESS);
        }
        return false;
    }
}
