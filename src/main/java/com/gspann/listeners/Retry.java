package com.gspann.listeners;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import com.gspann.utility.PropertyReader;

/**
 * @author Manoj Hans
 **/
public class Retry implements IRetryAnalyzer {

	 private static final Logger log = LogManager.getLogger(Retry.class);
	 private int count = 0;
	 private static int maxTry = 0;
	 //PropertyReader prop=new PropertyReader("config.properties");
	    
	public Retry() {
		maxTry =Integer.parseInt(PropertyReader.getProperty("MaxFailedTestsRetryCount"));
    }
   
    @Override
    public boolean retry(ITestResult iTestResult) {
        if (!iTestResult.isSuccess()) {                 
            if (count < maxTry) {   
            	log.info(" Re-running "+iTestResult.getMethod().getMethodName()+" once again.");                   
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