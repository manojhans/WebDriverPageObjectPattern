package com.claritybot.utility;

import com.relevantcodes.extentreports.ExtentReports;

import java.io.File;
import java.util.Objects;

/**
 * @author Manoj Hans
 **/
public class ExtentManager {

    public static ExtentReports instance;

    public synchronized static ExtentReports getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ExtentReports(System.getProperty("user.dir") + "/test-output/ExecutionReport.html");
            instance.loadConfig(new File("./src/main/resources/config.xml"));
        }
        return instance;
    }
}
