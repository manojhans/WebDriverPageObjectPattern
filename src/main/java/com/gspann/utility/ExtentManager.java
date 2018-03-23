package com.gspann.utility;

import com.relevantcodes.extentreports.ExtentReports;



import java.io.File;

/**
 * @author Manoj Hans
 **/
public class ExtentManager {
    
	public static ExtentReports instance;
    
    public synchronized static ExtentReports getInstance() {
        if (instance == null) {
            instance =new ExtentReports(System.getProperty("user.dir") + "/test-output/ExecutionReport.html");
            try {
            	instance.loadConfig(new File("./src/main/resources/config.xml"));
            } catch (Exception e) {
                System.out.println("Not taking ExtendReporting");
            }
        }
        return instance;
    }
}
