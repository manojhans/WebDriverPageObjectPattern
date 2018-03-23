package com.gspann.utility;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import org.openqa.selenium.internal.BuildInfo;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manoj Hans
 **/
public class ExtentTestManager { 

    private static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
    private static ExtentReports extent = ExtentManager.getInstance();
    
    public synchronized static ExtentTest getTest() {
        return extentTestMap.get((int) (long) (Thread.currentThread().getId()));
    }
    public synchronized static void endTest() {
       extent.endTest(extentTestMap.get((int) (long) (Thread.currentThread().getId())));
    }
    public static synchronized ExtentTest startTest(String testName) {
        return startTest(testName, "", "");
    }

    public synchronized static ExtentTest startTest(String testName, String desc, String deviceId) {
        ExtentTest test = extent.startTest(testName, desc).assignCategory(deviceId);
        extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
        Map<String, String> sysInfo = new HashMap<String, String>();
        BuildInfo info=new BuildInfo();
        sysInfo.put("Selenium Java Version",info.getReleaseLabel());
        sysInfo.put("Environment", "Prod");
        sysInfo.put("User Name","Manoj Hans");
        sysInfo.put("RunnerMode", "TestNG");
        extent.addSystemInfo(sysInfo);
        return test;
    }
}