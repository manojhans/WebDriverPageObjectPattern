package com.gspann.listeners;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
 
/**
 * @author Manoj Hans
 **/
public class AnnotationTransformer implements IAnnotationTransformer {
	
	private static final Logger log = LogManager.getLogger(AnnotationTransformer.class);
   
	@Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
    	log.info("Setting the retry analyzer for "+testMethod.getName());
    	annotation.setRetryAnalyzer(Retry.class);
    }
}