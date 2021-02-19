package com.claritybot.steps;

/**
 * @author Manoj Hans
 **/
public interface LoginPageScenarios {

    String verifyLoginWithValidCredentials = "1. Enter valid credentials<br>"
        + "2. click login button";

    String verifyLoginWithInvalidCredentials = "1. Enter invalid credentials<br>"
        + "2. Click login button<br>"
        + "3. Check for error message";
}
