package com.selenium.automatedTest.engine;

import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import org.apache.commons.io.FileUtils;
import org.junit.runner.JUnitCore;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.Select;
import java.io.IOException;
import java.io.File;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.openqa.selenium.JavascriptExecutor;

import static com.selenium.automatedTest.engine.Test.reportEvent;

/**
 * Created with IntelliJ IDEA.
 * User: barnest
 * Date: 06/10/16
 * Time: 08:29
 * This contains all actions on an element, e.g. click, select dropdown etc. .
 * ***********IMPORTANT***********PLEASE READ IF YOU ARE ADDING A NEW ACTION*********************************
 * If you add a new action, you must:                                                                       *
 * (1) Update the front-end html editor dropdown to include it in there.                                    *
 * (2) If the action needs to use the currentElement passed in, ensure you check it is not null.            *
 * Fail the step if the currentElement is null - do in same way as has been done for other actions          *
 * like this e.g. checkattribute.                                                                           *
 * (3) You must handle exceptions within each action method and fail the step as required if makes sense to *
 * do so                                                                                                    *
 * (4) The method name MUST all be in lowercase.                                                            *
 * (5) All actions to start with calling                                                                    *
 * resetActionResult();                                                                                     *
 * and end with:                                                                                            *
 * return actionResult;                                                                                     *
 * (6) To fail the step simply do:                                                                          *
 * actionResult.setOutcome("FAIL");                                                                         *
 * You must also set an error message:                                                                      *
 * actionResult.setErrorMessage("your error message. . .");                                                 *
 * See existing tests for examples. . .
 * (7) All actions must have the same parameter interface (WebElement currentElement, String inputvalue).  *
 * *********************************************************************************************************
 */


public class Action {
    public static String copy = "web";
    private static Result actionResult = new Result();

    private static void resetActionResult() {
        actionResult.setOutput("");
        actionResult.setOutcome("PASS");
        actionResult.setErrorMessage("");
    }
    //*********************************************************************************************
    //* ALL TEST ACTIONS BELOW                                                                    *                     *
    //*********************************************************************************************

    public static Result endtest(WebElement currentElement, String inputvalue) {
        resetActionResult();
        Test.setEndTest(true);

        return actionResult;
    }

    public static Result openbrowser(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Test.openBrowser();
            if (!inputvalue.equals("")) {
                navigate(currentElement, inputvalue);
            }
        } catch (Exception actione) {
            actionResult.setErrorMessage("Action failed: " + actione.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    /*public static Result sqlupdate(WebElement currentElement, String inputvalue) throws IOException, SQLException {
        resetActionResult();
        try {
            Class.forName("com.informix.jdbc.IfxDriver");
        } catch (Exception e) {
        }
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid Database.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        String connectVal = currentElement.toString();
        com.informix.jdbc.IfxSqliConnect conn;
        conn = (IfxSqliConnect) DriverManager.getConnection(connectVal);
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(inputvalue);
        }catch (SQLException e) {
            actionResult.setErrorMessage("Action failed: " + e.getMessage());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

  /*  public static Result sqlselect(WebElement currentElement, String inputvalue, String byinnertext) throws IOException, SQLException {
        resetActionResult();
        try {
            Class.forName("com.informix.jdbc.IfxDriver");
        } catch (Exception e) {
        }
      /*  if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid Database.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        String connectVal = currentElement.toString();
        com.informix.jdbc.IfxSqliConnect conn;
        conn = (IfxSqliConnect) DriverManager.getConnection(connectVal);
        try (Statement stmt = conn.createStatement()) {
            ResultSet res = stmt.executeQuery(inputvalue);
            ResultSetMetaData md = res.getMetaData();
            String output = "";
            int colCount = md.getColumnCount();
            while (res.next()) {
                for (int j = 1; j <= colCount; j++) {
                    String col_name = md.getColumnName(j);
                    String itemVal = res.getString(col_name);
                    output = output + "\n" + col_name + "    =    " + itemVal;
                    actionResult.setOutput(output);
                }
            }
        }catch (SQLException e) {
            actionResult.setErrorMessage("Action failed: " + e.getMessage());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }*/

    public static Result resizebrowser(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Test.getDriver().manage().window().setSize(new Dimension(400, 600));
        } catch (Exception actione) {
            actionResult.setErrorMessage("Action failed: " + actione.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    public static Result switchtodriversession(WebElement currentElement, String inputvalue) {
        resetActionResult();
        String switchResult = Test.switchToDriverInstance(inputvalue);
        if (!switchResult.equals("")) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage(switchResult);
        }
        return actionResult;
    }

    public static Result getproperty(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            String returnedProperty = AppProperties.get(inputvalue);
            if (returnedProperty != null) {
                actionResult.setOutput(AppProperties.get(inputvalue));
            } else {
                actionResult.setErrorMessage("Property '" + inputvalue + "' not found in properties file.");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setErrorMessage("Action failed: " + actione.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    public static Result setparameter(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            //Split input string into two input parameters:
            String[] parms = inputvalue.split("=", 2);
            if (parms.length == 2) {
                String parameterName = parms[0];
                String parameterValue = parms[1];
                Test.setCallParameter(parameterName, parameterValue);
            } else {
                actionResult.setErrorMessage("'" + inputvalue + "' is invalid input for 'SetParameter' action. Must pass in input of the format 'parametername=parametervalue'");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage(actione.toString());
        }

        return actionResult;
    }

    public static Result getparameter(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            //Get parameter assumes parameter is required:
            if (Test.callParameterExists(inputvalue)) {
                actionResult.setOutput(Test.getCallParameter(inputvalue));
            } else {
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Parameter '" + inputvalue + "' is a required parameter but has not been passed in");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage(actione.toString());
        }
        return actionResult;
    }

    public static Result getoptionalparameter(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            //Split input string into two input parameters:
            String[] parms = inputvalue.split("=", 2);
            if (parms.length == 2) {
                String parameterName = parms[0];
                String parameterDefaultValue = parms[1];
                if (Test.callParameterExists(parameterName)) {
                    actionResult.setOutput(Test.getCallParameter(parameterName));
                } else {
                    //Get optional parameter does not require the parameter has been passed. Will default to the value passed in.
                    reportEvent("INFO", "No parameter '" + parameterName + "' passed in. Defaulting to default value " + parameterDefaultValue);
                    actionResult.setOutput(parameterDefaultValue);
                }
            } else {
                actionResult.setErrorMessage("'" + inputvalue + "' is invalid input for 'GetOptionalParameter' action. Must pass in input of the format 'parametername=parameterdefaultvalue'");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage(actione.toString());
        }
        return actionResult;
    }

    public static Result doubleclick(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        // Only perform action if the element we have located is unique:
        try {
            Actions action = new Actions(Test.getDriver());
            action.doubleClick(currentElement);
            action.perform();
        } catch (MoveTargetOutOfBoundsException m) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("doubleClick failed. You may need to change locator to ensure element is uniquely identifed. Number of instances of this element are: " + Test.getCurrentElementCount() + ". If that does not work, try locating the element differently - e.g. more specifically within html doc. Or check css with developers. .");
        } catch (TimeoutException te) {
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("doubleClick failed with error " + e.toString());
        }
        return actionResult;
    }

    public static Result click(WebElement currentElement, String inputvalue) {
        resetActionResult();
        // Pass in input value of 0 if the element you are clicking does not cause the page to reload. Also if it triggers and alert to come up. Otherwise any of the post-
        // click checking (i.e. managing the browser timeout and waiting for page to load, will fail on unhandled alert exception which will then get rid of the alert!
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        // Only perform action if the element we have located is unique:
        try {
            // Set pageloadtimeout to longer than normal to allow click action to complete (for non ie browsers which actually bother to wait for the page. . .)
            if (!inputvalue.equals("0")) {
                Test.getDriver().manage().timeouts().pageLoadTimeout(Test.getPageLoadAfterActionTimeOutWait(), TimeUnit.SECONDS);
            }
            currentElement.click();

            if (!inputvalue.equals("0")) {
                //Set pageloadtimeout back to normal
                Test.getDriver().manage().timeouts().pageLoadTimeout(Test.getPageLoadTimeOutWait(), TimeUnit.SECONDS);
                waitForPageToLoadIE();
            }
        } catch (MoveTargetOutOfBoundsException m) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Click failed. You may need to change locator to ensure element is uniquely identifed. Number of instances of this element are: " + Test.getCurrentElementCount() + ". If that does not work, try locating the element differently - e.g. more specifically within html doc. Or check css with developers.");
        } catch (UnhandledAlertException uaenow) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Clicking this action causes an alert message to come up. Please pass in 0 to the input value to ensure this action does not fail so you can handle the" +
                    " alert in the next step");
        } catch (TimeoutException te) {
        } catch (Exception eee) {
            if (eee.toString().toLowerCase().contains("is not clickable")) {
                System.out.println(eee.toString());
                reportEvent("INFO", "Chrome issue with clicks. Trying to click the location.");
                Point coordinates = currentElement.getLocation();
                Robot robot = null;
                try {
                    robot = new Robot();
                    robot.mouseMove(coordinates.getX(), coordinates.getY() + 120);
                } catch (AWTException awte) {
                    actionResult.setOutcome("FAIL");
                    actionResult.setErrorMessage("Click failed with error " + awte.toString());
                }
            } else {
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Click failed with error " + eee.toString());
            }
        } finally {
            if (!inputvalue.equals("0")) {
                try {
                    //Set pageloadtimeout back to normal
                    Test.getDriver().manage().timeouts().pageLoadTimeout(Test.getPageLoadTimeOutWait(), TimeUnit.SECONDS);
                } catch (NoSuchWindowException fin) {
                    actionResult.setOutcome("FAIL");
                    actionResult.setErrorMessage("Failed to reset timeout. Try passing in 0 as the input value. Window is closed so click action cannot wait for page to load");
                } catch (Exception fin2) {
                    actionResult.setOutcome("FAIL");
                    actionResult.setErrorMessage("Failed to reset timeout. Try passing in 0 as the input value: " + fin2.toString());
                }
            }
        }
        return actionResult;
    }

    public static Result exists(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        //Nothing happens. If we got here the element was successfully found
        //However return "exists":
        actionResult.setOutput("exists");
        return actionResult;
    }

    public static Result scrolltobottom(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Actions action = new Actions(Test.getDriver());
            action./*sendKeys(Keys.CONTROL).*/sendKeys(Keys.END).perform();
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to set script value: " + e.toString());
        }
        return actionResult;
    }

    public static Result scrolltotop(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Actions action = new Actions(Test.getDriver());
            action.sendKeys(Keys.CONTROL).sendKeys(Keys.HOME).perform();
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to set script value: " + e.toString());
        }
        return actionResult;
    }

    public static Result scrolltoelement(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            ((JavascriptExecutor) Test.getDriver()).executeScript("arguments[0].scrollIntoView(true);", currentElement);
            Thread.sleep(500);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to set script value: " + e.toString());
        }
        return actionResult;
    }




    public static Result hover(WebElement currentElement, String inputvalue) {
        resetActionResult();
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            Actions action = new Actions(Test.getDriver());
            action.moveToElement(currentElement).build().perform();
            //  Thread.sleep(200000);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to find element: " + e.toString());
        }
        return actionResult;
    }

    public static Result sendreturn(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            currentElement.sendKeys(Keys.RETURN);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to set script value: " + e.toString());
        }
        return actionResult;
    }

    public static Result alertcheck(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            // Check the presence of alert
            Alert alert = Test.getDriver().switchTo().alert();
            // if present consume the alert
            alert.accept();
            actionResult.setErrorMessage("Alert seen and accepted");

        } catch (NoAlertPresentException ex) {
            // Alert not present
            actionResult.setErrorMessage("No Alert seen");
        }
        return actionResult;
    }

    public static Result alertvaluecheck(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            // Check the presence of alert
            Alert alert = Test.getDriver().switchTo().alert();
            // if present consume the alert

            String actualAlertText = alert.getText().trim().replaceAll("\n", " ");
            if (actualAlertText.contains(inputvalue)) {
                actionResult.setOutput("Alert exists");
            } else {
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Expected alert text '" + inputvalue + "' does not contain actual alert text '" + actualAlertText + "'");
            }
            alert.accept();
        } catch (NoAlertPresentException ex) {
            // Alert not present
            actionResult.setErrorMessage("No Alert seen");
        }
        return actionResult;
    }


    public static Result setscriptvalue(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            actionResult.setOutput(inputvalue);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to set script value: " + e.toString());
        }
        return actionResult;
    }

    public static Result checkexpressiontrue(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            String evaluatedScriptResult = inputvalue;
            actionResult.setOutput(evaluatedScriptResult);
            if (evaluatedScriptResult.toLowerCase().equals("true")) {

            } else {
                if (evaluatedScriptResult.toLowerCase().equals("false")) {
                    actionResult.setErrorMessage("Expression not evaluated to true");
                } else {
                    actionResult.setErrorMessage("Could not evaluate script expression. Please check your expression.");
                }
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to check expression true: " + e.toString());
        }
        return actionResult;
    }


    public static Result checknumericexpressiontrue(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            String evaluatedScriptResult = inputvalue;
            actionResult.setOutput(evaluatedScriptResult);
            if (evaluatedScriptResult.toLowerCase().equals("true")) {
            } else {
                if (evaluatedScriptResult.toLowerCase().equals("false")) {
                    actionResult.setErrorMessage("Expression not evaluated to true");
                } else {
                    actionResult.setErrorMessage("Could not evaluate script expression. Please check your expression.");
                }
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to check numeric expression true: " + e.toString());
        }
        return actionResult;
    }

    public static Result getcurrenttime(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Date date = new Date();
            String dateFormat = "yyyyMMdd_HHmmss";
            if (!inputvalue.equals("")) {
                dateFormat = inputvalue;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            String formattedDate = sdf.format(date);
            actionResult.setOutput(formattedDate);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to get current time: " + e.toString());
        }
        //Set up timestamp:
        //Nothing happens. If we got here the element was successfully found
        return actionResult;
    }

    public static Result closealliesessions(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Runtime.getRuntime().exec("taskkill /F /IM " + "iexplore.exe");  //kill existing IE browsers
            if (AppProperties.get("browser").toLowerCase().equals("internetexplorer")) {

                Test.openBrowser();
            }
        } catch (Exception e) {
            actionResult.setErrorMessage("Failed to close all IE browsers: " + e.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    public static Result close(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Test.getDriver().close();
            // Set the current window to the earliest instance.
            try {
                for (String handle : Test.getDriver().getWindowHandles()) {
                    Test.getDriver().switchTo().window(handle);
                    break;
                }
            } catch (Exception er) {
                reportEvent("INFO", "After closing browser, no window is now selected as unable to find one in browser session.");
            }
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to close browser window: " + e.toString());
        }
        return actionResult;
    }

    public static Result wait(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Test.reportEvent("INFO", "Start of wait for: " + inputvalue + " seconds");
            long waitTimeMilliSecs = Long.parseLong(inputvalue) * 1000;
            try {
                Thread.sleep(waitTimeMilliSecs);
                Test.reportEvent("INFO", "End of wait for: " + inputvalue + " seconds");
            } catch (InterruptedException e) {
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Wait failed: " + e.toString());
            }
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + e.toString());
        }
        return actionResult;
    }

    /// action by Thomas on 30 jan 2014

    public static Result doesnotexist(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means element not found.
        Test.getDriver().manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        if (currentElement == null) {
            actionResult.setOutput("doesnotexist");
            return actionResult;
        }
        //Nothing happens. If we got here the element was found
        actionResult.setOutput("exists");
        return actionResult;
    }

    public static Result sendkeys(WebElement currentElement, String inputvalue) {
        // Will sendkeys - so append to whatever is there already.
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            currentElement.sendKeys(inputvalue);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + e.toString());
        }
        return actionResult;
    }

    public static Result sendrandom(WebElement currentElement, String inputvalue) {
        // Will sendkeys - so append to whatever is there already.
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            Random randomGenerator = new Random();
            String ranNum = "";
            char num;
            for (int i = 0; i <= 6; i++) {
                num = (char) (randomGenerator.nextInt(25) + 97);
                ranNum = num + ranNum;
            }
            currentElement.sendKeys(ranNum);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + e.toString());
        }
        return actionResult;
    }

    public static Result sendrandomemail(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            Random randomGenerator = new Random();
            String ranNum = "";
            char num;
            for (int i = 0; i <= 16; i++) {
                num = (char) (randomGenerator.nextInt(25) + 97);
                ranNum = num + ranNum;
            }
            String fullEmail = ranNum + "@testing.com";
            currentElement.sendKeys(fullEmail);
            File file = new File("C:\\SeleniumResults/copyFile.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(fullEmail);
            bw.close();
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + e.toString());
        }
        return actionResult;
    }

    public static Result sendrandomemailconfirm(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        BufferedReader br = null;
        try {
            FileInputStream fis = null;
            br = new BufferedReader(new FileReader("C:\\SeleniumResults/copyFile.txt"));
            currentElement.sendKeys(br.readLine());
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + e.toString());
        }
        return actionResult;
    }


    public static Result sendkeyswithnewline(WebElement currentElement, String inputvalue) {
        //This will do same as sendkeys but will add a new line character to the end.
        //To be used several times in a row to set a multiline box (e.g. comments box on a page) to
        //have several lines of text.
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            String textWithNewLineOnEnd = inputvalue + "\n";
            currentElement.sendKeys(textWithNewLineOnEnd); //NOTE should ideally use SET below as will clear out the field first
        } catch (Exception e) {
            actionResult.setErrorMessage("Failed to send keys with new line: " + e.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    public static Result set(WebElement currentElement, String inputvalue) {
        // Similar to sendkeys. But clears the existing value down first. Also
        // to be used for setting checkboxes on and off.
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            if (currentElement.getAttribute("type").toUpperCase().equals("CHECKBOX")) {
                if (inputvalue.toUpperCase().equals("ON")) {
                    if (!currentElement.isSelected()) {
                        //switch on if not selected
                        currentElement.click();
                    }
                } else if (inputvalue.toUpperCase().equals("OFF")) {
                    if (currentElement.isSelected()) {
                        //switch off if selected
                        currentElement.click();
                    }
                }
            }
            //otherwise just set to the value passed in
            else {
                currentElement.clear();
                currentElement.sendKeys(inputvalue);
            }
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to perform set action: " + e.toString());
        }
        return actionResult;
    }

    public static Result clear(WebElement currentElement, String inputvalue) {
        //  clears the existing value

        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
              //  String content = getElementInnerText(currentElement);
               // for (int i = 0; i < content.length(); i++) {
                   // currentElement.sendKeys(Keys.DELETE);
                   // currentElement.sendKeys(Keys.BACK_SPACE);
                    currentElement.sendKeys(Keys.CONTROL + "a");
                    currentElement.sendKeys(Keys.DELETE);
              //  }
                //currentElement.clear();

        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to perform set action: " + e.toString());
        }
        return actionResult;
    }

    public static Result refresh(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Test.getDriver().navigate().refresh();
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to set script value: " + e.toString());
        }
        return actionResult;
    }

    public static Result checkvalue(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            String currentValue = getvalue(currentElement, inputvalue).getOutput();
            //Only continue if getting value was successful:
            if (actionResult.getOutcome().equals("PASS")) {
                actionResult.setOutput(currentValue);
                if (currentValue.matches(inputvalue)) {
                    //Do nothing
                } else {
                    actionResult.setErrorMessage("Expected Value: '" + inputvalue + "' does not equal actual value: '" + currentValue + "'");
                    actionResult.setOutcome("FAIL");
                }
            }
            // else the actionResult will have been set with FAIL and error message
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + e.toString());
        }
        return actionResult;
    }

    public static Result getvalue(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            String currentValue = "";
            //For selects (i.e. dropdowns:)
            if (currentElement.getTagName().toUpperCase().equals("SELECT")) {
                //For a dropdown we want to check current value. Should only really be used
                //for single selection dropdowns. For multi-selects will get first selected value.

                List<WebElement> allOptions = currentElement.findElements(By.tagName("option"));
                for (WebElement option : allOptions) {
                    if (option.isSelected()) {
                        currentValue = option.getText();
                        break;
                    }
                }
            } else if (currentElement.getAttribute("type").toUpperCase().equals("CHECKBOX")) {
                if (currentElement.isSelected()) {
                    currentValue = "ON";
                } else {
                    currentValue = "OFF";
                }
            }
            //otherwise for normal elements with values, just get the value of attribute "value":
            else {
                currentValue = currentElement.getAttribute("value");
            }
            if (currentValue == null) {
                currentValue = "";
            }
            actionResult.setOutput(currentValue);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + e.toString());
        }
        return actionResult;
    }

    public static Result deselectall(WebElement currentElement, String inputvalue) {
        //FOr use on multi-select dropdown
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            Select currentSelect = new Select(currentElement);
            currentSelect.deselectAll();
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Deselecting all failed '" + e.toString() + "'");
        }
        return actionResult;
    }

    public static Result select(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            new Select(currentElement).selectByVisibleText(inputvalue);
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Selecting value '" + inputvalue + "' failed. Check allowed values.");
        }
        return actionResult;
    }

    public static Result checktitle(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            long endTimeMillis = System.currentTimeMillis() + Test.getCurrentTimeOutWait() * 1000;
            long newCurrentTime = System.currentTimeMillis();
            boolean foundTitleMatch = false;
            do {
                if (Test.getDriver().getTitle().matches(inputvalue)) {
                    foundTitleMatch = true;
                }
                newCurrentTime = System.currentTimeMillis();
            }
            while (!foundTitleMatch && newCurrentTime < endTimeMillis);
            if (!foundTitleMatch) {
                actionResult.setErrorMessage("Expected title: '" + inputvalue + "' does not equal actual title: '" + Test.getDriver().getTitle() + "'");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception e) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + e.toString());
        }
        return actionResult;
    }

    public static Result checktext(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            if (!getElementInnerText(currentElement).matches(inputvalue)) {
                actionResult.setErrorMessage("Expected text: '" + inputvalue + "' does not equal actual text: '" + getElementInnerText(currentElement) + "'");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result checktextwithwhitespace(WebElement currentElement, String inputvalue) {
        // This is same as checktext above, but does not remove hidden new line characters
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            if (!currentElement.getText().matches(inputvalue)) {
                actionResult.setErrorMessage("Expected text: '" + inputvalue + "' does not equal actual text: '" + currentElement.getText() + "'");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result containstext(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            if (!getElementInnerText(currentElement).contains(inputvalue)) {
                actionResult.setErrorMessage("Expected text: '" + inputvalue + "' not contained within '" + getElementInnerText(currentElement) + "'");
                actionResult.setOutcome("FAIL");

            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result notcontainstext(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            if (getElementInnerText(currentElement).contains(inputvalue)) {
                actionResult.setErrorMessage("Expected text: '" + inputvalue + "' is contained within '" + getElementInnerText(currentElement) + "'");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result gettext(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            actionResult.setOutput(getElementInnerText(currentElement));
            HashMap<DataFormat, Object> map = new HashMap<DataFormat, Object>();
            map.put(DataFormat.PLAIN_TEXT, getElementInnerText(currentElement));
            Clipboard.getSystemClipboard().setContent(map);
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }
    public static Result copytext(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            String content = getElementInnerText(currentElement);
            File file = new File("C:\\SeleniumResults/copyFile.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result pastetext(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        BufferedReader br = null;
        try {
            File file = new File("C:\\SeleniumResults/copyFile.txt");
            String sCurrentLine;
            br = new BufferedReader(new FileReader("C:\\SeleniumResults/copyFile.txt"));
            currentElement.sendKeys(br.readLine());
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result getipaddress(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String getIPAddress = ip.getHostAddress();
            actionResult.setOutput(getIPAddress);
        } catch (UnknownHostException e) {

            actionResult.setErrorMessage("Unable to return ip address. Unknown host error occurred: " + e.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    public static Result gettagname(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            actionResult.setOutput(currentElement.getTagName());
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result getlocation(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            actionResult.setOutput(currentElement.getLocation().toString());
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result getsize(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            actionResult.setOutput(currentElement.getSize().toString());
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result switchtowindowbytitle(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            boolean currentWindowOpen = true;
            String currentWindowHandle = "";
            try {
                currentWindowHandle = Test.getDriver().getWindowHandle();

            } catch (NoSuchWindowException nswe) {
                // The window has been closed so default to first instance (done below)
                currentWindowOpen = false;
            }
            int currentTimeOutWait = Test.getCurrentTimeOutWait();
            long endTimeMillis = System.currentTimeMillis() + currentTimeOutWait * 1000;
            long newCurrentTime = System.currentTimeMillis();
            boolean foundWindow = false;
            do {
                for (String handle : Test.getDriver().getWindowHandles()) {
                    if (currentWindowOpen == false) {
                        currentWindowHandle = handle;
                        currentWindowOpen = true;
                    }
                    try {
                        Test.getDriver().switchTo().window(handle);

                    } catch (TimeoutException toe) {
                    }
                    String name = Test.getDriver().getTitle();
                    if (name.matches(inputvalue)) {
                        foundWindow = true;
                        break;
                    }
                }
                newCurrentTime = System.currentTimeMillis();
            }
            while (newCurrentTime < endTimeMillis && foundWindow == false);
            if (foundWindow == false) {
                if (currentWindowOpen) {
                    try {
                        Test.getDriver().switchTo().window(currentWindowHandle); //switch back to current window
                    } catch (TimeoutException toea) {
                    }
                }
                actionResult.setErrorMessage("No window with title matching '" + inputvalue + "' was found. Window not switched.");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }


    public static Result switchtoframebyname(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        try {
            Test.getDriver().switchTo().frame(inputvalue);
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result switchtoframebyid(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        try {
            Test.getDriver().switchTo().frame(Test.getDriver().findElement(By.id(inputvalue)));
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result switchtoframebyclass(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        try {
            Test.getDriver().switchTo().frame(Test.getDriver().findElement(By.className(inputvalue)));
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result switchtoparent(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        try {
            Test.getDriver().switchTo().defaultContent();

        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result getallframenames(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        try {
            List<WebElement> ele = Test.getDriver().findElements(By.tagName("iframe"));
            System.out.println("Number of frames in a page :" + ele.size());
            for (WebElement el : ele) {
                // Returns the Id of a frame.
                System.out.println("Frame Id :" + el.getAttribute("id"));
                // Returns the Name of a frame.
                System.out.println("Frame name :" + el.getAttribute("name"));
                System.out.println("Frame title :" + el.getAttribute("title"));
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result switchtoframebytitle(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            int currentTimeOutWait = Test.getCurrentTimeOutWait();
            long endTimeMillis = System.currentTimeMillis() + currentTimeOutWait * 1000;
            long newCurrentTime = System.currentTimeMillis();
            boolean foundFrame = false;
            do {
                try {
                    Thread.sleep(1000);
                    //
                    List<WebElement> frameset = Test.getDriver().findElements(By.tagName("frame"));
                    if (frameset.size() > 0) {
                        for (WebElement framename : frameset) {
                            if (framename.getAttribute("name").matches(inputvalue)) {
                                Test.getDriver().switchTo().defaultContent();
                                Test.getDriver().switchTo().frame(framename);
                                foundFrame = true;
                            }
                        }
                    }
                } catch (TimeoutException toe) {
                } catch (Exception e) {
                    Test.reportEvent("INFO", e.toString());
                }
                newCurrentTime = System.currentTimeMillis();
            }
            while (newCurrentTime < endTimeMillis && foundFrame == false);
            if (foundFrame == false) {
                actionResult.setErrorMessage("No frame with title matching '" + inputvalue + "' was found.");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result switchtoframebyinstance(WebElement currentElement, String inputvalue) {
        //To make consistent with window instance numbering, starting indexing at 1 instead of zero. So script should pass in 1 for frame 0
        resetActionResult();
        try {
            if (inputvalue.equals("")) {
                inputvalue = "1";
            }
            int frameInstance = 1;
            try {
                frameInstance = Integer.parseInt(inputvalue);
                frameInstance = frameInstance - 1;
            } catch (Exception e) {
                frameInstance = 1;
                Test.reportEvent("INFO", "Frame instance set to 1 as passed in instance not numeric");
            }
            int currentTimeOutWait = Test.getCurrentTimeOutWait();
            long endTimeMillis = System.currentTimeMillis() + currentTimeOutWait * 1000;
            long newCurrentTime = System.currentTimeMillis();
            boolean foundFrame = false;
            do {
                try {
                    Thread.sleep(1000);
                    Test.getDriver().switchTo().defaultContent();
                    Test.getDriver().switchTo().frame(frameInstance);
                    foundFrame = true;
                } catch (TimeoutException toe) {
                } catch (Exception e) {
                    Test.reportEvent("INFO", e.toString());
                }
                newCurrentTime = System.currentTimeMillis();
            } while (newCurrentTime < endTimeMillis && foundFrame == false);

            if (foundFrame == false) {
                actionResult.setErrorMessage("No frame of instance '" + inputvalue + "' was found.");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }

        return actionResult;
    }

    public static Result switchtoframebylocator(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            int currentTimeOutWait = Test.getCurrentTimeOutWait();
            long endTimeMillis = System.currentTimeMillis() + currentTimeOutWait * 1000;
            long newCurrentTime = System.currentTimeMillis();
            boolean foundFrame = false;
            do {
                try {
                    Thread.sleep(1000);
                    Test.getDriver().switchTo().defaultContent();
                    Test.getDriver().switchTo().frame(currentElement);
                    foundFrame = true;
                } catch (TimeoutException toe) {
                } catch (Exception e) {
                    Test.reportEvent("INFO", e.toString());
                }
                newCurrentTime = System.currentTimeMillis();
            } while (newCurrentTime < endTimeMillis && foundFrame == false);
            if (foundFrame == false) {
                actionResult.setErrorMessage("No frame found for passed in locator was found.");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }


    public static Result switchtowindowbyinstance(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            boolean currentWindowOpen = true;
            String currentWindowHandle = "";
            try {
                currentWindowHandle = Test.getDriver().getWindowHandle();

            } catch (NoSuchWindowException nswe) {
                // The window has been closed so default to first instance (done below)
                currentWindowOpen = false;

            }

            int currentTimeOutWait = Test.getCurrentTimeOutWait();
            long endTimeMillis = System.currentTimeMillis() + currentTimeOutWait * 1000;
            long newCurrentTime = System.currentTimeMillis();
            boolean foundWindow = false;
            if ((inputvalue == null) || inputvalue.equals("")) {
                inputvalue = "1";
                Test.reportEvent("INFO", "No window instance passed in. Assuming first window instance required. .");
            }
            int windowInstanceToSwitchTo = Integer.parseInt(inputvalue);
            do {
                int currentWindowInstance = 0;
                for (String handle : Test.getDriver().getWindowHandles()) {
                    if (currentWindowOpen == false) {
                        currentWindowHandle = handle;
                        currentWindowOpen = true;
                    }
                    currentWindowInstance = currentWindowInstance + 1;
                    if (currentWindowInstance == windowInstanceToSwitchTo) {
                        try {
                            Test.getDriver().switchTo().window(handle);
                        } catch (TimeoutException toea) {
                        }
                        foundWindow = true;
                        break;
                    }
                }
                newCurrentTime = System.currentTimeMillis();
            } while (newCurrentTime < endTimeMillis && foundWindow == false);
            if (foundWindow == false) {
                if (currentWindowOpen) {
                    try {
                        Test.getDriver().switchTo().window(currentWindowHandle); //switch back to current window
                    } catch (TimeoutException toeb) {
                    }
                }
                actionResult.setErrorMessage("No window with instance '" + windowInstanceToSwitchTo + "' was found. Window not switched.");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result verifyhidden(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            if (currentElement.isDisplayed()) {
                actionResult.setErrorMessage("Element is visible");
                actionResult.setOutcome("FAIL");

            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result verifyvisible(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            if (!currentElement.isDisplayed()) {
                actionResult.setErrorMessage("Element is not visible");
                actionResult.setOutcome("FAIL");

            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result getattribute(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            actionResult.setOutput(getElementAttribute(currentElement, inputvalue));
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result closeandopen(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        try {
            Test.openBrowser();
            if (!inputvalue.equals("")) {
                navigate(currentElement, inputvalue);
            }
            Test.getDriver().close();
        } catch (Exception actione) {
            actionResult.setErrorMessage("Action failed: " + actione.toString());
            actionResult.setOutcome("FAIL");
        }
        Test.getDriver().close();
        return actionResult;
    }

    public static Result checkattribute(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //For this action, a locator must be passed in - if element is null means no locator was passed in.
        if (currentElement == null) {
            actionResult.setErrorMessage("Must pass in a valid locator for this action.");
            actionResult.setOutcome("FAIL");
            return actionResult;
        }
        try {
            //Split input string into two input parameters:
            String[] parms = inputvalue.split("=", 2);
            if (parms.length == 2) {
                String attributeToCheck = parms[0];
                String expectedAttributeValue = parms[1];
                String returnAttribute = getElementAttribute(currentElement, attributeToCheck);
                actionResult.setOutput(returnAttribute);
                // Only continue if successfully retrieved the attribute.
                try {
                    if (actionResult.getOutcome().equals("PASS")) {
                        if (!returnAttribute.matches(expectedAttributeValue)) {
                            actionResult.setErrorMessage("Expected Attribute Value: '" + expectedAttributeValue + "' does not equal actual value: '" + returnAttribute + "'");
                            actionResult.setOutcome("FAIL");
                        }
                    }
                } catch (Exception exc) {
                    actionResult.setErrorMessage(exc.toString());
                    actionResult.setOutcome("FAIL");
                }
            } else {
                actionResult.setErrorMessage("Invalid input for 'CheckAttribute' action. Must pass in input of the format 'attribute=expectedattributevalue'");
                actionResult.setOutcome("FAIL");
            }
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }


    public static Result navigate(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            String url = inputvalue;
            // Set pageloadtimeout to longer than normal to allow navigate action to complete.
            Test.getDriver().manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
            actionResult.setOutput(url);
            try {
                Test.getDriver().get(url);
            } catch (TimeoutException te) {
                Test.reportEvent("INFO", "The page load has timed out. Please ensure the wait on the next step is long to allow for the page to finish loading. .");
            } catch (Error e) {
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Failed to navigate to: '" + inputvalue + "'. Error: " + e.toString());
            }
            //Set pageloadtimeout back to normal
            Test.getDriver().manage().timeouts().pageLoadTimeout(Test.getPageLoadTimeOutWait(), TimeUnit.SECONDS);
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
        }
        return actionResult;
    }

    public static Result drivehtml(WebElement currentElement, String inputvalue) {
        resetActionResult();
        String passedInValue = inputvalue;
        int fileCount = countOccurencesCalledScript("", inputvalue);
        if (fileCount > 1) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Script with name '" + passedInValue + "' is not unique. Must be unique under calledscripts.");
            return actionResult;
        }
        inputvalue = locateCalledScript("", inputvalue);
        if (inputvalue.equals("")) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed to locate called script " + passedInValue + ". The script must exist under automatedTest/calledscripts. Please just pass in the name (no path) e.g. login.html");
            return actionResult;
        }
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = sdf.format(date);

        String defaultPath = AppProperties.get("defaultTestPath");
        String nameOfCalledTest = new File(inputvalue).getName();
        String driveResultListFile = System.getProperty("concordion.output.dir") +
                Test.getCallingTestLocation() + Test.getCallingTestName() +
                "_Calls\\" + nameOfCalledTest.substring(0, nameOfCalledTest.indexOf(".html"))
                + "_data_driven_run" + formattedDate + ".html";
        File driveResultFile = new File(driveResultListFile);
        String driveResultFileName = driveResultFile.getName();
        String resultsLinkPath = "./" + Test.getCallingTestName() + "_Calls/" + nameOfCalledTest.substring(0, nameOfCalledTest.indexOf(".html"))
                + "_data_driven_run" + formattedDate + ".html";
        driveResultFile.getParentFile().mkdirs();
        try {
            driveResultFile.createNewFile();
        } catch (IOException e) {
            reportEvent("INFO", "Did not create file " + driveResultListFile);
        }
        String resultListText = "";
        resultListText = "<html>" + System.getProperty("line.separator") +
                "<body>" + System.getProperty("line.separator") +
                "<h1>Data Driven Test Results for " + driveResultFileName.substring(0, driveResultFileName.indexOf(".html")) + "</h1>" + System.getProperty("line.separator") +
                "<table border ='1'>" + System.getProperty("line.separator");
        Result driveResult = new Result();
        driveResult.setOutcome("PASS");
        driveResult.setOutput("");
        driveResult.setErrorMessage("");
        try {
            // Will loop through the excelData stored in most recent "setupdatadrive" step.
            HashMap<String, HashMap<String, String>> currentExcelData = new HashMap<String, HashMap<String, String>>();
            currentExcelData.putAll(Test.getExcelData());
            HashMap<String, String> driveResults = new HashMap<String, String>();
            HashMap<String, String> driveResultLinks = new HashMap<String, String>();
            int numberOfRows = Test.getExcelDataNumberOfRows();
            int i = 1;
            HashMap<String, String> header = currentExcelData.get(String.valueOf(0));
            //parameter headers:
            int headerParameterColumn = 1;
            resultListText = resultListText + "<tr>" + System.getProperty("line.separator");
            while (headerParameterColumn <= header.size()) {
                String colNum = String.valueOf(headerParameterColumn);
                String currentParameter = header.get(colNum);
                headerParameterColumn = headerParameterColumn + 1;
                resultListText = resultListText + "<th>" + currentParameter + "</th>" + System.getProperty("line.separator");
            }
            resultListText = resultListText + "<th>Result</th>" + System.getProperty("line.separator") +
                    "</tr>" + System.getProperty("line.separator");
            while (i <= numberOfRows) {
                String currentRowNumber = String.valueOf(i);
                HashMap<String, String> currentRow = currentExcelData.get(currentRowNumber);
                //go through all data in the row and set up parameters:
                int currentParameterColumn = 1;
                resultListText = resultListText + "<tr>" + System.getProperty("line.separator");
                while (currentParameterColumn <= header.size()) {
                    String parameterInstance = String.valueOf(currentParameterColumn);
                    String currentKey = header.get(parameterInstance);
                    String currentValue = currentRow.get(currentKey);
                    Test.setCallParameter(currentKey, currentValue);
                    currentParameterColumn = currentParameterColumn + 1;
                    resultListText = resultListText + "<td>" + currentValue + "</td>" + System.getProperty("line.separator");
                }
                callhtml(currentElement, passedInValue);
                driveResultLinks.put(currentRowNumber, "." + Test.getCalledTestLinkPath());
                driveResults.put(currentRowNumber, actionResult.getOutcome());
                //Add result and link to the run results for that row
                resultListText = resultListText + "<td>" + actionResult.getOutcome() +
                        " <a href='." + Test.getCalledTestLinkPath() + "' target='_blank'>Results</a></td>"
                        + System.getProperty("line.separator");
                resultListText = resultListText + "</tr>" + System.getProperty("line.separator");
                Test.setCalledTestLinkPath("");
                Test.clearCallParameters();
                if (!actionResult.getOutcome().equals("PASS")) {
                    driveResult.setOutcome("FAIL");
                }
                i = i + 1;
            }
            resultListText = resultListText + "</table>" + System.getProperty("line.separator") + "</body>" + System.getProperty("line.separator") + "</html>";
            // Now we want to create a results file:
            Test.writeToFile(driveResultFile, resultListText);
            actionResult.setOutcome(driveResult.getOutcome());
            actionResult.setErrorMessage(driveResult.getErrorMessage());
            actionResult.setOutput(driveResult.getOutput());
            Test.setCalledTestLinkPath(resultsLinkPath);
        } catch (Exception actione) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Failed: " + actione.toString());
            Test.clearCallParameters();
        }
        return actionResult;
    }

    public static Result callhtml(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //inputvalue will contain the relative path to the script to run e.g. demo/Quick.html
        try {
            //inputvalue will just be file name of script e.g. login.html.
            // Do not pass in any path. The script must be located under automatedTest/calledscripts
            String passedInValue = inputvalue;
            int fileCount = countOccurencesCalledScript("", inputvalue);
            if (fileCount > 1) {
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Script with name '" + passedInValue + "' is not unique. Must be unique under calledscripts/.");
                return actionResult;
            }
            inputvalue = locateCalledScript("", inputvalue);
            if (inputvalue.equals("")) {
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Failed to locate called script " + passedInValue + ". The script must exist under automatedTest/calledscripts. Please just pass in the name (no path) e.g. login.html");
                return actionResult;
            }
            boolean scriptExists = true;
            String defaultPath = AppProperties.get("defaultTestPath");
            String specsPath = AppProperties.get("specsPath");
            String testScript = defaultPath + inputvalue;
            // Create time-stamped copy of the file
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String formattedDate = sdf.format(date);
            String currentTestLocation = specsPath + testScript;
            currentTestLocation = currentTestLocation.replaceAll("/", "\\\\");
            File f1 = new File(currentTestLocation);
            String currentFilePath = f1.getParentFile().getPath();
            String currentFileNameNoIdentifier = "";
            try {
                currentFileNameNoIdentifier = f1.getName().substring(0, f1.getName().indexOf(".html"));
            } catch (Exception ob) {
                scriptExists = false;
            }
            File f2 = null;
            if (scriptExists) {
                String newCurrentTestLocation = specsPath + Test.getCallingTestLocation() + "\\" + Test.getCallingTestName() + "_Calls/" + currentFileNameNoIdentifier + formattedDate + ".html";
                newCurrentTestLocation = newCurrentTestLocation.replaceAll("/", "\\\\");
                f2 = new File(newCurrentTestLocation);
                try {
                    FileUtils.copyFile(f1, f2);
                } catch (FileNotFoundException ex) {
                    scriptExists = false;
                } catch (Exception g) {
                    System.out.println("[ERROR] Reason: " + g.toString());
                }
            }
            if (scriptExists) {
                testScript = f2.getPath();  // This is the full path
                // We need the path from //com/. .
                testScript = testScript.substring(testScript.indexOf(defaultPath.replaceAll("/", "\\\\")));
                String resultsLinkPath = "./" + Test.getCallingTestName() + "_Calls/" + currentFileNameNoIdentifier + formattedDate + ".html";
                CalledTest.currentResource = testScript;
                Test.setCalledTest(testScript);
                // save calling test name and calling test location as these will get changed when call a different script:
                int savedCallStackInstance = Test.getCallStackInstance();
                String savedCallingTestName = Test.getCallingTestName();
                String savedCallingTestLocation = Test.getCallingTestLocation();
                try {
                    Test.setCallStackInstance(savedCallStackInstance + 1);
                    JUnitCore.runClasses(CalledTest.class);
                    Test.setCalledTestLinkPath(resultsLinkPath);
                    if (CalledTest.getCurrentTestStatus().equals("Passed")) {
                        actionResult.setOutcome("PASS");
                        actionResult.setErrorMessage("");
                        //leave output- as this is a way of returning output to the calling test.
                    } else {
                        actionResult.setOutcome("FAIL");
                        actionResult.setErrorMessage(inputvalue + " failed");
                        actionResult.setOutput("");
                    }
                    // Remove breadcrumbs from result file (as don't make sense and could confuse navigation through tests - especially if looking at a previous run):
                    File resultFile = new File(System.getProperty("concordion.output.dir").replaceAll("/", "\\\\") + testScript);
                    //Test.updateHtmlFile(resultFile,"","",false);
                    Test.updateHtmlFile(resultFile, "CALLED TEST: ", new File(testScript).getName(), false);
                } catch (Throwable error) {
                    actionResult.setOutput("");
                    actionResult.setOutcome("FAIL");
                    actionResult.setErrorMessage("Failed to call script: " + error.toString());
                } finally {
                    FileUtils.forceDelete(f2);
                    FileUtils.forceDelete(f2.getParentFile());
                    Test.setCallingTestName(savedCallingTestName);
                    Test.setCallingTestLocation(savedCallingTestLocation);
                    Test.setCallStackInstance(savedCallStackInstance);
                }
            } else {
                actionResult.setOutput("");
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Script does not exist. Please pass in relative path including html, e.g. demo/Quick.html");
            }
        } catch (Exception actione) {
            actionResult.setOutput("");
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Call to script failed: " + actione.toString());
        }
        //Nothing happens. If we got here the element was successfully found
        return actionResult;
    }

    public static Result callmethod(WebElement currentElement, String inputvalue) {
        resetActionResult();
        //inputvalue will contain the key to the class which contains the "run" method to run
        try {

            boolean methodExists = true;
            Runner currentRunner = RunnerList.runners.get(inputvalue.toLowerCase());
            if (currentRunner == null) {
                methodExists = false;
            }
            if (methodExists) {
                int savedCallStackInstance = Test.getCallStackInstance();
                try {
                    Test.setCallStackInstance(savedCallStackInstance + 1); //update the call stack instance, just in case the method does stuff with script variables (which it should not. . )
                    actionResult = currentRunner.run(Test.getDriver(), Test.getCallParameters());
                    reportEvent(actionResult.getOutcome(), "Overall outcome of step calling method '" + inputvalue + "'");
                    reportEvent("DIVIDER", "");
                } catch (Throwable error) {
                    actionResult.setOutput("");
                    actionResult.setOutcome("FAIL");
                    actionResult.setErrorMessage("Failed to call method: " + error.toString());
                } finally {
                    Test.setCallStackInstance(savedCallStackInstance);
                }
            } else {
                actionResult.setOutput("");
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Method does not exist. Please check /engine/RunnerList.java for list of valid methods.");
            }
        } catch (Exception actione) {
            actionResult.setOutput("");
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage("Call to method failed: " + actione.toString());
        }
        return actionResult;
    }

    //********************************************************************************************
    //Methods below are all internal methods which should NOT be called as an action:            *
    //********************************************************************************************
    public static void waitForPageToLoadIE() {
        //********************************************************************************************
        //This method is an internal method which should NOT be called as an action - do not copy    *
        // this method to create a new action. It is not of correct format                           *
        //********************************************************************************************
        //This is a bit of a hack for IE which does not wait for page to refresh  . .
        // Wait for page to go stale basically. .
        //wait for page to load:
        if (!AppProperties.get("browser").equalsIgnoreCase("internetexplorer")) {
            return; //only do for internetexplorer browser
        }
        try {

            long endTimeMillis = System.currentTimeMillis() + Test.getPageLoadAfterActionTimeOutWait() * 1000;
            long newCurrentTime = System.currentTimeMillis();
            boolean elementGoneStale = false;
            WebElement page = null;
            try {
                page = Test.getDriver().findElement(By.xpath("//html"));
            } catch (NoSuchWindowException nswee) {
                return;
            } catch (NoSuchElementException nseea) {
                Thread.sleep(500);
                page = Test.getDriver().findElement(By.xpath("//html"));
            }

            do {
                try {
                    Thread.sleep(100);
                    page.isEnabled();

                } catch (StaleElementReferenceException xsee) {
                    elementGoneStale = true;
                } catch (NoSuchElementException xnsee) {
                    elementGoneStale = true;
                } catch (Exception xallo) {
                    elementGoneStale = true;
                }
                newCurrentTime = System.currentTimeMillis();
            }
            while (elementGoneStale == false && newCurrentTime < endTimeMillis);


            try {
                page = Test.getDriver().findElement(By.xpath("//html"));
            } catch (NoSuchWindowException nsweeb) {
                return;
            } catch (NoSuchElementException nseeab) {
                Thread.sleep(500);
                page = Test.getDriver().findElement(By.xpath("//html"));
            }
            if (newCurrentTime < endTimeMillis) {
                do {
                    try {
                        Thread.sleep(100);
                        page.isEnabled();

                        elementGoneStale = false;
                    } catch (StaleElementReferenceException see) {
                        elementGoneStale = true;
                        page = Test.getDriver().findElement(By.xpath("//html"));
                    } catch (NoSuchElementException nsee) {
                        elementGoneStale = true;
                    } catch (Exception allo) {
                        elementGoneStale = true;
                    }
                    newCurrentTime = System.currentTimeMillis();
                }
                while (elementGoneStale == true && newCurrentTime < endTimeMillis);
            }

        } catch (Exception eple) {
            reportEvent("INFO", "Failed to wait for page to load: " + eple.toString());
        }

    }

    public static String getElementInnerText(WebElement e) {
        //********************************************************************************************
        //This method is an internal method which should NOT be called as an action - do not copy    *
        // this method to create a new action. It is not of correct format                           *
        //********************************************************************************************
        //This strips out trailing and leading spaces and whitespace stuff
        // Note -  we are stripping out new line characters (\n) because otherwise
        // when you are checking text, we cannot put new line characters into the script and therefore
        // replacing them as " " as this is what it looks like is returned when you do a "gettext" anyway
        //Also regular expression stuff like .* was not working cos was comparing against stuff with new line
        //characters in.
        try {
            return e.getText().trim().replaceAll("\n", " ");

        } catch (Exception exc) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage(exc.toString());
            return "";
        }

    }

    private static String getElementAttribute(WebElement e, String attribute) {
        //********************************************************************************************
        //This method is an internal method which should NOT be called as an action - do not copy    *
        // this method to create a new action. It is not of correct format                           *
        //********************************************************************************************
        String attributeValue = "";
        try {
            if (attribute.toUpperCase().equals("ISSELECTED")) {
                attributeValue = String.valueOf(e.isSelected());
            } else if (attribute.toUpperCase().equals("ISDISPLAYED")) {
                attributeValue = String.valueOf(e.isDisplayed());
            } else if (attribute.toUpperCase().equals("ISENABLED")) {
                attributeValue = String.valueOf(e.isEnabled());
            } else {
                attributeValue = e.getAttribute(attribute);
            }


        } catch (Exception f) {
            actionResult.setOutcome("FAIL");
            actionResult.setErrorMessage(f.toString());


        }
        if (attributeValue == null) {
            attributeValue = "";
        }
        return attributeValue;

    }

    public static Result getActionResult() {
        //********************************************************************************************
        //This method is an internal method which should NOT be called as an action - do not copy    *
        // this method to create a new action. It is not of correct format                           *
        //********************************************************************************************
        return actionResult;
    }

    private static String closeAlertAndCheckItsText(boolean acceptAlert, String expectedAlertText) {
        //********************************************************************************************
        //This method is an internal method which should NOT be called as an action - do not copy    *
        // this method to create a new action. It is not of correct format                           *
        //********************************************************************************************
        //Will return blank if passes. Otherwise will return error reason
        try {
            Alert alert = Test.getDriver().switchTo().alert();
            String actualAlertText = alert.getText().trim().replaceAll("\n", " ");
            if (acceptAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            if (actualAlertText.matches(expectedAlertText)) {
                return "";
            } else {
                return "Expected alert text '" + expectedAlertText + "' does not match actual alert text '" + actualAlertText + "'";
            }
        } catch (NoAlertPresentException nape) {
            return "No alert present";
        } catch (Exception e) {
            return "Failed to close alert: " + e.toString();
        } catch (Error err) {
            return "Failed to close alert: " + err.toString();
        }

    }


    public static int countOccurencesCalledScript(String directoryPath, String htmlname) {
        //Recursive function to locate a called or driven test
        //returns the path of the script to be called. E.g. calledscripts/login.html
        String defaultTestPath = AppProperties.get("defaultTestPath");
        int fileCount = 0;
        File dir;
        dir = new File(directoryPath);
        String locatedScript = "";
        try {

            File[] files = dir.listFiles();
            for (File file : files) {

                if (file.isDirectory()) {

                    fileCount = fileCount + countOccurencesCalledScript(file.getPath(), htmlname);

                } else {
                    //html file name

                    if (file.getName().equalsIgnoreCase(htmlname)) {
                        fileCount = fileCount + 1;
                        locatedScript = file.getPath().replaceAll("\\\\", "/");
                        locatedScript = locatedScript.substring(locatedScript.indexOf(defaultTestPath));
                        //Now we want the path from automatedTest so remove the defaultTestPath:
                        locatedScript = locatedScript.substring(defaultTestPath.length());
                        reportEvent("INFO", "located script " + locatedScript);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            locatedScript = "";
            reportEvent("ERROR", "Failed to locate html script to call or drive: " + e.toString());
        }

        return fileCount;

    }

    public static String locateCalledScript(String directoryPath, String htmlname) {
        //Recursive function to locate a called or driven test
        //returns the path of the script to be called. E.g. calledscripts/test.html
        String defaultTestPath = AppProperties.get("defaultTestPath");
        File dir;
        if (directoryPath.equals("")) {
            dir = new File(AppProperties.get("sourceSpecsPath") + defaultTestPath + "calledscripts/");
        } else {
            dir = new File(directoryPath);
        }
        String locatedScript = "";
        try {

            File[] files = dir.listFiles();
            for (File file : files) {

                if (file.isDirectory()) {

                    locatedScript = locateCalledScript(file.getPath(), htmlname);
                    if (!locatedScript.equals("")) {
                        break;
                    }
                } else {
                    //html file name
                    if (file.getName().equalsIgnoreCase(htmlname)) {
                        locatedScript = file.getPath().replaceAll("\\\\", "/");
                        locatedScript = locatedScript.substring(locatedScript.indexOf(defaultTestPath));
                        //Now we want the path from automatedTest so remove the defaultTestPath:
                        locatedScript = locatedScript.substring(defaultTestPath.length());
                        //reportEvent("INFO","located script " + locatedScript);    reported in the countoccurrences method
                        break;
                    }
                }
            }
        } catch (Exception e) {
            locatedScript = "";
            reportEvent("ERROR", "Failed to locate html script to call or drive: " + e.toString());
        }

        return locatedScript;
    }

    ///action for writing to the selenium runtimedata file
    public static Result writetoruntimedatafile(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            String[] a = inputvalue.split(",");
            if (a == null || a.length != 3) {
                throw new Exception("The parameters is null or length not equal to 3");
            }

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
            String formattedDate = sdf.format(date);
            String Fullinputvalue = inputvalue + "," + formattedDate;
            Test.WritetoRunTimeData(Fullinputvalue);
        } catch (Exception actione) {
            actionResult.setErrorMessage("Action failed: " + actione.toString());
            actionResult.setOutcome("FAIL");
        }

        return actionResult;
    }

    ///action for writing to the selenium runtimedata file
    public static Result readfromruntimedatafile(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            String[] a = inputvalue.split(",");
            if (a == null || a.length != 2) {
                throw new Exception("The parameters is null or length not equal to 2");
            }
            actionResult.setOutput(Test.ReadFromRunTimeData(inputvalue));
        } catch (Exception e) {
            actionResult.setErrorMessage("Unable to return the run time data value " + e.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    public static Result slider(WebElement currentElement, String inputvalue) {
        resetActionResult();
        try {
            Actions action = new Actions(Test.getDriver());
            int width=currentElement.getSize().getWidth();
            int number = Integer.parseInt(inputvalue);
            action.dragAndDropBy(currentElement, number,0).build().perform();
            action.moveToElement(currentElement, ((width*number)/100), 0).click();
        } catch (Exception actione) {
            actionResult.setErrorMessage("Action failed: " + actione.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    public static Result runSQL(WebElement currentElement, String inputvalue) throws SQLException {
        resetActionResult();
        try {
            String[] a = inputvalue.split(",");
            if (a == null || a.length != 2) {
                throw new SQLException("The parameters is null or length not equal to 2");
            }
            actionResult.setOutput(Test.ReadFromRunTimeData(inputvalue));
        } catch (SQLException e) {
            actionResult.setErrorMessage("Unable to return the run time data value " + e.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }

    public static Result readCSV(WebElement currentElement, String inputvalue) throws SQLException {
        resetActionResult();
        String csvFile = "C:/Users/tom.barnes/Desktop/list.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] country = line.split(cvsSplitBy);

                System.out.println("Country [code= " + country[4]
                        + " , name=" + country[5] + "]");
            }
        } catch (Exception e) {
            actionResult.setErrorMessage("Unable to return the run time data value " + e.toString());
            actionResult.setOutcome("FAIL");
        }
        return actionResult;
    }
}