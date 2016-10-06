package com.selenium.automatedTest.engine;

/**
 * Created with IntelliJ IDEA.
 * User: barnest
 * Date: 17/06/16
 * Time: 19:41
 * To change this template use File | Settings | File Templates.
 */
public class Result {
    private String errorMessage = "";
    private String outcome = "";
    private String output = "";

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getOutcome() {
        return outcome;
    }

    public String getOutput() {
        return output;
    }

    public void setErrorMessage(String s) {
        errorMessage = s;
    }

    public void setOutcome(String s) {
        outcome = s;
    }

    public void setOutput(String s) {
        output = s;
    }
}
