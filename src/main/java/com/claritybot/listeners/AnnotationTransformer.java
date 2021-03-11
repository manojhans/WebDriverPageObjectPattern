package com.claritybot.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static java.lang.String.format;

/**
 * @author Manoj Hans
 **/
public class AnnotationTransformer implements IAnnotationTransformer {

    private static final Logger logger = LogManager.getLogger(AnnotationTransformer.class);

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        logger.info(format("Setting the retry analyzer for %s", testMethod.getName()));
        annotation.setRetryAnalyzer(Retry.class);
    }
}
