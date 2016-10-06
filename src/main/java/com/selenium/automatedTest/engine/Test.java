package com.selenium.automatedTest.engine;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.SessionNotFoundException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: barnest
 * Date: 21/09/16
 * Time: 13:41
 */
public class Test {
    //static objects for lifetime of each test:
    private static WebDriver driver;
    private static String calledTest = "";
    private static String callingTestLocation = "";
    private static String callingTestName = "";
    private static String calledTestLinkPath = "";
    private static boolean endTest = false;
    private static String testLogName = "";
    private static String testLogFullPath = "";
    private static BufferedWriter testLogWriter;
    private static WebElement rootElement = null;
    private static WebElement currentRoot = null;
    private static HashMap<String, String> callParameters = new HashMap<String, String>();
    private static HashMap<String, String> scriptVariables = new HashMap<String, String>();
    private static HashMap<String, HashMap<String, String>> callStackScriptVariableInstances = new HashMap<String, HashMap<String, String>>();
    private static HashMap<String, ScriptEngine> callStackScriptEngines = new HashMap<String, ScriptEngine>();
    private static HashMap<String, ScriptEngine> callStackNumberScriptEngines = new HashMap<String, ScriptEngine>();
    private static HashMap<String, WebDriver> driverInstances = new HashMap<String, WebDriver>();
    private static HashMap<String, HashMap<String, String>> excelData = new HashMap<String, HashMap<String, String>>();
    private static int callStackInstance = 0;
    private static ScriptEngineManager factory = new ScriptEngineManager();
    private static ScriptEngine jsScriptEngine = factory.getEngineByName("JavaScript");
    private static ScriptEngineManager numberFactory = new ScriptEngineManager();
    private static ScriptEngine jsScriptNumberEngine = numberFactory.getEngineByName("JavaScript");
    private static int currentElementCount = 0;
    private static int timeOutWait = 10;       //default timeout for searching for element
    private static int currentTimeOutWait = 10;   //current timeout for searching for element

    private static int pageLoadTimeOutWait = 3;   //Have to put this in cos of dodgy IE driver behaviour. .
    // where it is still downloading something on the page and just causing the test to hang. . This means the
    // engine needs to increase this page load timeout as some actions (such as navigate) do actually wait for the page to load (even in IE browser!)

    private static int pageLoadAfterActionTimeOutWait = 10;  //This is the timeout wait for after a "click" action is performed.
    // It allows the page to load. This has been put in for the IE browser which does not wait for a page to load after a click action.
    // So if the browser is IE, after a click event the engine will wait for the page to go "stale". In some instances a click
    // does not cause the page to reload therefore has been kept low at 5 seconds. The tester can pass in 0 as the input value
    // for a click action if they don't want the script to wait for the page to load after a click. But in most cases,
    // this 5 second wait along with the discipline of ensuring every time you perform a click or something which causes
    // the page to reload you check for a new element on the page, this should make the tests as robust as possible. .

    //get methods
    public static WebDriver getDriver() {
        return driver;
    }

    public static String getCallParameter(String s) {
        if (callParameters.containsKey(s)) {
            return callParameters.get(s);
        } else {
            return "";
        }
    }

    public static int getPageLoadAfterActionTimeOutWait() {
        return pageLoadAfterActionTimeOutWait;
    }

    public static HashMap<String, HashMap<String, String>> getExcelData() {
        //return (HashMap<String,HashMap<String,String>>) excelData.clone();
        return excelData;
    }

    public static int getExcelDataNumberOfRows() {
        return excelData.size() - 1;
    }

    public static HashMap<String, String> getCallParameters() {
        return callParameters;
    }

    public static boolean getEndTest() {
        return endTest;
    }

    public static String getTestLogName() {
        return testLogName;
    }

    public static int getCallStackInstance() {
        return callStackInstance;
    }

    public static String getCalledTestLinkPath() {
        return calledTestLinkPath;
    }

    public static String getCalledTest() {
        return calledTest;
    }

    public static String getCallingTestLocation() {
        return callingTestLocation;
    }

    public static String getCallingTestName() {
        return callingTestName;
    }

    public static WebElement getCurrentRoot() {
        if (currentRoot == null) {
            return driver.findElement(By.xpath("/html"));
        } else {
            return currentRoot;
        }
    }

    public static WebElement getRootElement() {
        if (rootElement == null) {
            return driver.findElement(By.xpath("/html"));
        } else {
            return rootElement;
        }
    }

    public static int getCurrentElementCount() {
        return currentElementCount;
    }

    public static int getPageLoadTimeOutWait() {
        return pageLoadTimeOutWait;
    }

    public static String getTestLogFullPath() {
        return testLogFullPath;
    }

    public static void setEndTest(boolean b) {
        endTest = b;
    }

    //set methods
    public static void setCalledTest(String s) {
        calledTest = s;
    }

    public static void setDriver(WebDriver d) {
        driver = d;
    }

    public static void clearDriverInstances() {
        driverInstances.clear();
    }

    public static void closeDownTestLogWriter() {
        // Always close files.
        try {
            testLogWriter.close();
            testLogWriter = null;
        } catch (Exception ex) {
            System.out.println("ERROR, Error closing test log file " + ex.toString());
        }
    }

    public static void setCallParameter(String k, String v) {
        callParameters.put(k, v);
    }

    public static void setTestLogName(String s) {
        testLogName = s;
    }

    public static boolean callParameterExists(String s) {
        if (callParameters.containsKey(s)) {
            return true;
        } else {
            return false;
        }
    }

    public static void setCallStackInstance(int i) {
        if (callStackInstance > 0 && i < callStackInstance) {
            //Going down the call stack means we must clear down the variables at current level
            String callStack = String.valueOf(callStackInstance);
            scriptVariables.clear();
            initialiseJSScriptEngine();
            callStackScriptVariableInstances.put(callStack, scriptVariables);
            callStackScriptEngines.put(callStack, jsScriptEngine);
            callStackNumberScriptEngines.put(callStack, jsScriptNumberEngine);
        }
        callStackInstance = i;
        setCallStackInstances();
    }

    public static void setCalledTestLinkPath(String s) {
        calledTestLinkPath = s;
    }

    public static void setRootElement(WebElement e) {
        rootElement = e;
    }

    public static void setCurrentRoot(WebElement e) {
        currentRoot = e;
    }

    public static void setCallingTestLocation(String s) {
        callingTestLocation = s;
    }

    public static void setCallingTestName(String s) {
        callingTestName = s;
    }

    public static void clearScriptVariables() {
        scriptVariables.clear();
    }

    public static void clearCallParameters() {
        callParameters.clear();
    }

    public static void clearExcelData() {
        excelData.clear();
    }

    public static void setCallStackInstances() {
        String callStack = String.valueOf(callStackInstance);
        if (callStackScriptVariableInstances.containsKey(callStack)) {
            scriptVariables = callStackScriptVariableInstances.get(callStack);
            jsScriptEngine = callStackScriptEngines.get(callStack);
            jsScriptNumberEngine = callStackNumberScriptEngines.get(callStack);
        } else {
            scriptVariables.clear();
            scriptVariables = new HashMap<String, String>();
            initialiseJSScriptEngine();
            callStackScriptVariableInstances.put(callStack, scriptVariables);
            callStackScriptEngines.put(callStack, jsScriptEngine);
            callStackNumberScriptEngines.put(callStack, jsScriptNumberEngine);
        }
    }

    public static void clearCallStackInstances() {
        callStackScriptVariableInstances.clear();
        callStackScriptEngines.clear();
        callStackNumberScriptEngines.clear();
    }

    public static void quitAllDrivers() {
        Iterator<Map.Entry<String, WebDriver>> entries = driverInstances.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, WebDriver> entry = entries.next();
            WebDriver currentDriver = entry.getValue();
            currentDriver.quit();
        }
        clearDriverInstances();
    }

    public static void initialiseJSScriptEngine() {
        factory = null;
        jsScriptEngine = null;
        numberFactory = null;
        jsScriptNumberEngine = null;
        factory = new ScriptEngineManager();
        jsScriptEngine = factory.getEngineByName("JavaScript");
        numberFactory = new ScriptEngineManager();
        jsScriptNumberEngine = numberFactory.getEngineByName("JavaScript");
    }


    public static String switchToDriverInstance(String s) {
        try {
            int i = Integer.parseInt(s);
            if (i > driverInstances.size()) {
                return "Driver instance " + s + " does not exist";
            }
            Test.setDriver(driverInstances.get(s));
            Test.getDriver().findElement(By.xpath("//html")).click();//put this in to force focus on the driver instance
            return "";
        } catch (Exception e) {
            return "Failed to switch to driver instance " + s;
        }
    }

    public static Result actOnElement(HashMap<String, String> params) throws Exception {
        currentElementCount = 0;
        String bytype = "";
        String bytext = "";
        String byinnertext = "";
        String action = "";
        String inputvalue = "";
        int wait = 0;
        String resultname = "";
        String stepcomment = "";

        if (params.containsKey("comment")) {
            stepcomment = params.get("comment");
        }
        if (params.containsKey("bytype")) {
            bytype = params.get("bytype");
        }
        if (params.containsKey("bytext")) {
            bytext = params.get("bytext");
        }
        if (params.containsKey("byinnertext")) {
            byinnertext = params.get("byinnertext");
        }
        if (params.containsKey("action")) {
            action = params.get("action");
            int rootActionPosition = action.toUpperCase().indexOf("*ROOT");
            if (rootActionPosition >= 0) {
                action = action.substring(0, rootActionPosition);
                currentRoot = getRootElement();  //action is action againt the defined "root element" which should be set by "setasrootelement" action
                reportEvent("INFO", "Performing this action on the root element: " + currentRoot.toString());
            } else {
                currentRoot = null;  //action is just normal action. Not action against the defined "root element"
            }
        }
        if (params.containsKey("inputvalue")) {
            inputvalue = params.get("inputvalue");
        }
        if (params.containsKey("wait")) {
            String swait = params.get("wait");
            if (swait != null && !swait.equals("")) {
                if (swait.equals("0")) {
                    //If "0" is passed in this means that for this step, the script should NOT
                    //wait at all. The tester will do this if they step is checking something on a page
                    //they know has already loaded.
                    wait = 0;
                } else {
                    //If not zero, then we will increase the default timeout wait by the number specified by the tester
                    wait = Integer.parseInt(swait) + timeOutWait;
                }
            } else {
                wait = timeOutWait;
            }
        }
        if (params.containsKey("resultname")) {
            resultname = params.get("resultname");
        }
        //TODO "needsUniqueElement" stuff a bit better! E.g. put in array list or something. .

        //Now check if the action is one which requires a unique element to have been located. Currently none.
        boolean needsUniqueElement = false;
//        if (action.toLowerCase().equals("xxx")) {
//            needsUniqueElement = true;
//        }
        //More here
        //.
        //.
        //.
        //etc

        setDriverImplicitTimeOutWait(wait);
        //Starting step reporting:
        reportEvent("INFO", "Starting step: " + stepcomment);
        WebElement currentElement = null;
        //convert to uppercase
        bytype = bytype.toUpperCase();
        boolean bcontinue = true;
        //Set up result object and initialise to failure and incomplete
        Result actionResult = new Result();
        actionResult.setOutput("");
        actionResult.setErrorMessage("Action incomplete");
        actionResult.setOutcome("FAIL");
        if (action.contains("$")) {
            action = evaluateScript(action, "");
        }
        if (inputvalue.contains("$")) {
            if (action.toLowerCase().equals("evaluatenumber") || action.toLowerCase().equals("checknumericexpressiontrue")) {
                inputvalue = evaluateScript(inputvalue, "numeric");
            } else {
                if ((action.toLowerCase().equals("checkattribute") || action.toLowerCase().equals("setparameter") ||
                        action.toLowerCase().equals("getoptionalparameter")) && inputvalue.contains("=")) {
                    int positionEquals = inputvalue.indexOf("=");
                    String valueAfterEquals = inputvalue.substring(positionEquals + 1);
                    String valueUpToEquals = inputvalue.substring(0, positionEquals + 1);
                    valueAfterEquals = evaluateScript(valueAfterEquals, "");
                    inputvalue = valueUpToEquals + valueAfterEquals;
                } else {
                    inputvalue = evaluateScript(inputvalue, "");
                }
            }
        }
        if (bytype.contains("$")) {
            bytype = evaluateScript(bytype, "");
        }
        if (bytext.contains("$")) {
            bytext = evaluateScript(bytext, "");
        }
        if (byinnertext.contains("$")) {
            byinnertext = evaluateScript(byinnertext, "");
        }
        if (bcontinue) {
            By currentBy = null;
            //Set up element locator
            if (bytype.equals("PARTIALLINKTEXT")) {
                currentBy = By.partialLinkText(bytext);
            } else if (bytype.equals("LINKTEXT")) {
                currentBy = By.linkText("regexp:" + bytext);
            } else if (bytype.equals("XPATH")) {
                currentBy = By.xpath(bytext);
            } else if (bytype.equals("NAME")) {
                currentBy = By.name(bytext);
            } else if (bytype.equals("CLASSNAME") || bytype.equals("CLASS")) {
                currentBy = By.className(bytext);
            } else if (bytype.equals("ID")) {
                currentBy = By.id(bytext);
            } else if (bytype.equals("CSS")) {
                currentBy = By.cssSelector(bytext);
            } else if (bytype.equals("TAGNAME")) {
                currentBy = By.tagName(bytext);
            } else if (bytype.equals("")) {
                //do nothing
            }
            //. .  TODO complete all locator types
            else {
                bcontinue = false;
                actionResult.setErrorMessage("Not a valid locator type: " + bytype);
            }
            //Check element exists and set it to the currentElement
            if (bcontinue && !bytype.equals("")) {
                //Set up text for element in case need to report error:
                String elementText = bytype + "('" + bytext + "')";
                if (!byinnertext.equals("")) {
                    elementText = elementText + " with innertext '" + byinnertext + "'";
                }
                long endTimeMillis = System.currentTimeMillis() + currentTimeOutWait * 1000;
                long newCurrentTime = System.currentTimeMillis();
                boolean foundElementInCorrectState = false;
                boolean elementStale = false;
                int loops = 0;
                do {
                    if (byinnertext.equals("")) {
                        currentElementCount = getElementCount(currentBy);
                        if (currentElementCount > 0) {

                            if (currentRoot == null) {
                                currentElement = Test.getDriver().findElement(currentBy);
                            } else {
                                currentElement = Test.getCurrentRoot().findElement(currentBy);
                            }
                            if (currentElementCount > 1) {
                                reportEvent("WARNING", "Element locator is not unique. There are " + currentElementCount + " elements identified by this locator. Please change if this is not what you were expecting. . ");
                            }
                            //todo - output to log file
                        } else {
                            bcontinue = false;
                        }

                    } else { //also using innertext to uniquely identify the element
                        currentElement = findElementByInnertext(currentBy, byinnertext);
                        if (!(currentElement == null)) {
                            //todo - output to log file
                        } else {
                            bcontinue = false;
                        }
                    }
                    if (bcontinue == false) {
                        break;
                    }
                    //Check element is in correct state or is not refreshing. .
                    elementStale = false;
                    try {
                        if (currentElement.isEnabled() && currentElement.isDisplayed()) {
                            foundElementInCorrectState = true;
                        } else {
                            if (action.toLowerCase().equals("click") ||
                                    action.toLowerCase().equals("deselectall") ||
                                    action.toLowerCase().equals("doubleclick") ||
                                    action.toLowerCase().equals("select") ||
                                    action.toLowerCase().equals("sendkeys") ||
                                    action.toLowerCase().equals("sendkeyswithnewline") ||
                                    action.toLowerCase().equals("set")) {
                                reportEvent("INFO", "Element not enabled or not displayed. Trying location of element again if not timed out. .");
                            } else {
                                //for all other actions we only care we found the element on the page:
                                foundElementInCorrectState = true;
                            }
                        }
                    } catch (StaleElementReferenceException seree) {
                        //Hopefully will catch any StaleElementReferenceExceptions for elements which are refreshing. .
                        elementStale = true;
                        reportEvent("INFO", "Trying location of element again . .  element was stale. .");
                    } catch (Exception lee) {
                        reportEvent("INFO", "Trying location of element again if not timed out. . error was " + lee.toString());
                    }
                    newCurrentTime = System.currentTimeMillis();
                    loops = loops + 1;
                } while ((newCurrentTime < endTimeMillis || elementStale) && !foundElementInCorrectState && loops < 5);

                //Note even if the element is not in correct state, still continue. .it may get to correct state by the time we get to action and will throw error there if not.
                //The above loop to wait for correct state is just trying to give elements which are refreshing a chance to load properly.

                if (bcontinue == false && !action.toLowerCase().equals("doesnotexist")) {
                    //Report error and do not continue if no element was found UNLESS the action was to check for non-existence
                    actionResult.setErrorMessage("Element not found: " + elementText);
                }
                //If were checking for non-existence:
                if (action.toLowerCase().equals("doesnotexist")) {
                    if (bcontinue == false) {
                        bcontinue = true;    //Will continue to action method to set the action to PASS
                    } else {
                        //element found - this is error as was checking it did not exist
                        bcontinue = false;
                        actionResult.setErrorMessage("Element unexpectedly found: " + elementText);
                    }
                }
            }
        }
        if (needsUniqueElement) {
            //check element count
            if (currentElementCount != 1) {
                bcontinue = false;
                actionResult.setErrorMessage("For action: '" + action + "', must identify a unique element. Number of elements found with this locator are: " + currentElementCount + ". Please ensure the element is specified uniquely.");
            }
        }

        if (bcontinue == true) {
            //Now perform the action by invoking the Action class method according to what was passed in as "action":
            Class[] cArg = new Class[2];
            cArg[0] = WebElement.class;
            cArg[1] = String.class;
            Method method = Action.class.getMethod(action.toLowerCase(), cArg);
            if (method == null) {
                //method does not exist
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage("Method: '" + action + "' does not exist. Please check script.");
            } else {
                try {
                    Result elementAction = (Result) method.invoke(method, currentElement, inputvalue);
                    actionResult.setOutput(elementAction.getOutput());
                    actionResult.setOutcome(elementAction.getOutcome()); //will only PASS if gets to here
                    actionResult.setErrorMessage(elementAction.getErrorMessage());
                } catch (Exception e) {
                    actionResult.setErrorMessage(e.toString());
                }
            }
        }
        setDriverImplicitTimeOutWait(timeOutWait);
        if (!resultname.equals("")) {
            Result setScriptValueResult = setScriptValue(resultname, actionResult.getOutput());
            if (!setScriptValueResult.getOutcome().equals("PASS")) {
                actionResult.setOutcome("FAIL");
                actionResult.setErrorMessage(actionResult.getErrorMessage() + " " + setScriptValueResult.getErrorMessage());
            }
        }
        String messageText = "Action: " + action;
        if (!bytype.equals("")) {
            messageText = messageText + " performed on '" + bytype + "': '" + bytext + "'";
        }
        if (!byinnertext.equals("")) {
            messageText = messageText + " with innertext '" + byinnertext + "'";
        }
        if (!inputvalue.equals("")) {
            messageText = messageText + " for: '" + inputvalue + "'";
        }
        String stepErrorMessage = actionResult.getErrorMessage();
        if (!stepErrorMessage.equals("")) {
            reportEvent("INFO", stepErrorMessage);
        }
        currentRoot = null;
        reportEvent(actionResult.getOutcome(), messageText);
        reportEvent("DIVIDER", "");
        return actionResult;
    }

    public static boolean isScriptValue(String s) {
        //TODO also need to ensure second character is not a number
        if (s.startsWith("$") && !s.substring(1, 2).matches("[0-9]")) {
            return true;
        } else {
            return false;
        }
    }

    public static Result convertToScriptValue(String s) {
        Result convertResult = new Result();
        convertResult.setOutcome("PASS");
        convertResult.setOutput("");
        convertResult.setErrorMessage("");
        if (scriptVariables.containsKey(s)) {
            convertResult.setOutput(scriptVariables.get(s));
        } else {
            convertResult.setOutcome("FAIL");
            convertResult.setErrorMessage("Script Value: '" + s + "' does not exist");
        }
        return convertResult;
    }

    public static boolean isElementPresent(By by) {
        try {
            if (currentRoot == null) {
                Test.getDriver().findElement(by);
            } else {
                Test.getCurrentRoot().findElement(by);
            }
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static WebElement findElementByInnertext(By by, String byinnertext) {

        int potentialsSize = 0;
        int currentPotentialsSize = 0;
        WebElement myDynamicElement = null;

        //For this method we need to handle the timeout ourselves. .
        long endTimeMillis = System.currentTimeMillis() + currentTimeOutWait * 1000;
        long newCurrentTime = System.currentTimeMillis();
        boolean establishedThereAreNoPotentials = false;

        WebElement elementToReturn = null;
        do {
            establishedThereAreNoPotentials = false;

            try {
                //
                //if timeout wait is not zero then assume the page may still be loading
                //so we need to ensure all objects loaded before we start using the elements in the
                //below list. Otherwise may get StaleElementException.
                int loopedThrough = 0;


                if (currentTimeOutWait > 0) {
                    //wait until the number of potentials stops going up
                    do {
                        potentialsSize = currentPotentialsSize;
//                        myDynamicElement = null;
//                        myDynamicElement = (new WebDriverWait(Test.getDriver(), currentTimeOutWait))
//                                .until(ExpectedConditions.presenceOfElementLocated(by));
//

                        if (isElementThere(by)) {
                            List<WebElement> currentpotentials;
                            if (currentRoot == null) {
                                currentpotentials = Test.getDriver().findElements(by);

                            } else {
                                currentpotentials = Test.getCurrentRoot().findElements(by);
                            }
                            currentPotentialsSize = currentpotentials.size();

                        }

                        if (currentPotentialsSize > 0 && loopedThrough > 0) {
                            try {

                                Thread.sleep(1000);  //Wait every 1 seconds to see if any more loaded

                            } catch (InterruptedException e) {
                                //return null

                            }

                        }
                        loopedThrough = loopedThrough + 1;


                        newCurrentTime = System.currentTimeMillis();

                    }
                    while (currentPotentialsSize > potentialsSize && currentPotentialsSize > 0 && newCurrentTime < endTimeMillis && endTest == false);
                    // Only do while the number is increasing, that there are any potentials at all and that we have not exceeded the timeoutwait time
                    if (currentPotentialsSize == 0) {
                        establishedThereAreNoPotentials = true;
                    }

                }

                // If we established above that there are no potentials then don't continue:

                if (establishedThereAreNoPotentials == false) {

                    //if (searchedForPotentials  == false && currentTimeOutWait != timeOutWait && currentTimeOutWait > 0) {
//                        myDynamicElement = null;
//
//                        myDynamicElement = (new WebDriverWait(Test.getDriver(), currentTimeOutWait))
//                                .until(ExpectedConditions.presenceOfElementLocated(by));


                    //}
                    if (isElementThere(by)) {
                        List<WebElement> potentials;
                        if (currentRoot == null) {
                            potentials = Test.getDriver().findElements(by);

                        } else {
                            potentials = Test.getCurrentRoot().findElements(by);
                        }

                        for (WebElement potential : potentials) {
                            if (Action.getElementInnerText(potential).matches(byinnertext)) {
                                currentElementCount = 1;
                                return potential;

                            }
                        }

                    }


                }


                //return null;
            } catch (NoSuchElementException e) {
                //return null;
            } catch (TimeoutException toe) {

            } catch (Exception ex) {
                reportEvent("INFO", ex.toString());
                //return null;

            }
            newCurrentTime = System.currentTimeMillis();

        } while (newCurrentTime < endTimeMillis && endTest == false);

        return elementToReturn;
    }

    public static int getElementCount(By by) {
        //WebElement myDynamicElement = null;

        // if (currentTimeOutWait != timeOutWait && currentTimeOutWait > 0) {
        // Have to do this expectedcondition stuff because the implicit timeout wait does not seem to work
        // in IE: The below ensures it waits for the currentTimeOutWait
//            try {
//                myDynamicElement = (new WebDriverWait(Test.getDriver(), currentTimeOutWait))
//                        .until(ExpectedConditions.presenceOfElementLocated(by));
//
//            }  catch (Exception e){
//
//            }

        // }


        int numElements = 0;
        if (isElementThere(by)) {
            try {
                List<WebElement> potentials;
                if (currentRoot == null) {
                    potentials = Test.getDriver().findElements(by);
                } else {
                    potentials = Test.getCurrentRoot().findElements(by);
                }

                numElements = potentials.size();

            } catch (NoSuchElementException e) {

            }

        }


        return numElements;
    }

    public static WebElement getElementInstance(By by, int instanceIndex) {
        int i = 1;
        try {
            List<WebElement> potentials;
            if (currentRoot == null) {
                potentials = Test.getDriver().findElements(by);
            } else {
                potentials = Test.getCurrentRoot().findElements(by);
            }

            for (WebElement potential : potentials) {
                if (instanceIndex == 1) {
                    return potential;
                }
                i = i + 1;

            }
            return null;

        } catch (NoSuchElementException e) {
            return null;

        }


    }

    public static boolean isElementThere(By by) {
        //Here we will explicitly wait for an element. Returns true if finds if after currentTimeOutWait. Otherwise
        //return false. We do this here intead of using a WebDriverWait object because the by may be against either the
        //driver object or an element object. Also we want to control the way this works.
        boolean foundElement = false;

        long endTimeMillis = System.currentTimeMillis() + currentTimeOutWait * 1000;
        long newCurrentTime = System.currentTimeMillis();
        boolean firstTimeThrough = true;
        do {

            try {
                if (!firstTimeThrough) {
                    Thread.sleep(1000);
                } else {
                    firstTimeThrough = false;

                }
                if (currentRoot == null) {
                    Test.getDriver().findElement(by);
                } else {
                    Test.getCurrentRoot().findElement(by);
                }
                foundElement = true;
                break;
            } catch (NoSuchWindowException nswe) {
                setEndTest(true);
                reportEvent("ERROR", "No such window exception occurred. Test will end at next possible opportunity. .");

            } catch (SessionNotFoundException nswe) {
                setEndTest(true);
                reportEvent("ERROR", "Session not found exception occurred. Driver window was closed. Test will end at next possible opportunity. .");

            } catch (UnreachableBrowserException ube) {
                setEndTest(true);
                reportEvent("ERROR", "No such window exception occurred. Test will end at next possible opportunity. .");

            } catch (NoSuchElementException e) {
                //return null;
            } catch (TimeoutException toe) {
            } catch (Exception ex) {
                reportEvent("INFO", ex.toString());
                //return null;

            }
            newCurrentTime = System.currentTimeMillis();


        } while (newCurrentTime < endTimeMillis && foundElement == false && endTest == false);

        return foundElement;
    }

    public static Result setScriptValue(String k, String v) {
        Result ssvResult = new Result();
        ssvResult.setErrorMessage("");
        ssvResult.setOutput("");
        ssvResult.setOutcome("PASS");
        try {
            if (k.startsWith("$")) {
                //TODO also second char must not be number
                scriptVariables.put(k, v);
                //Script Engine Variables:
                //-String
                jsScriptEngine.put(k, v);
                //-Numeric  - this set holds numeric version
                try {
                    double currentDouble = Double.parseDouble(v);
                    jsScriptNumberEngine.put(k, currentDouble);

                } catch (NumberFormatException nfe) {
                    jsScriptNumberEngine.put(k, v);  //otherwise just put as string
                }

                String messageText = "Added " + k + " with value '" + v + "' to the script values list";
                reportEvent("INFO", messageText);
            } else {
                ssvResult.setOutcome("FAIL");
                ssvResult.setErrorMessage("Script Value name must begin with '$'");
            }

        } catch (Error e) {
            ssvResult.setOutcome("FAIL");
            ssvResult.setErrorMessage("Failed to set script value with name '" + k + "' and value '" + v + "':" + e.toString());

        }
        return ssvResult;


    }

    public static void reportEvent(String type, String messageText) {
        //Type should be "INFO", "WARNING" or "ERROR" - (or "PASS or "FAIL"). IF you pass in "DIVIDER" will just output
        // "----------------------------------------------------------------------------------"
        String eventText;
        if (type.toUpperCase().equals("DIVIDER")) {
            eventText = "--------------------------------------------------------------------------------";
        } else {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = sdf.format(date);
            String currentMethod = Thread.currentThread().getStackTrace()[2].getMethodName();
            String currentMethodJavaFile = Thread.currentThread().getStackTrace()[2].getFileName();
            //Remove ".java" from end:
            currentMethodJavaFile = currentMethodJavaFile.substring(0, currentMethodJavaFile.length() - 5);
            currentMethod = currentMethodJavaFile + "." + currentMethod;
            eventText = formattedDate + ": " + type.toUpperCase() + ": " + currentMethod + ": " + messageText;

        }

        System.out.println(eventText);
        try {
            testLogWriter.write(eventText + System.getProperty("line.separator"));
        } catch (IOException io) {
            System.out.println("ERROR, Error writing to test log file");
        } catch (NullPointerException npe) {
            //do nothing. Log Writer not initialised yet.
        }
    }

    public static int getTimeOutWait() {
        return timeOutWait;
    }

    public static int getCurrentTimeOutWait() {
        return currentTimeOutWait;
    }

    public static void setDriverImplicitTimeOutWait(int timeInSecs) {
        Test.currentTimeOutWait = timeInSecs;
        //Test.getDriver().manage().timeouts().implicitlyWait(timeInSecs, TimeUnit.SECONDS);  // should only set once. Do this is setuptest in StepBase
    }

    public static void openBrowser() {
        DesiredCapabilities cap;
        int numberOfDriverInstances = driverInstances.size();
        //TODO parameterise the browsers rather than hard-coding here
        if (AppProperties.get("browser").equalsIgnoreCase("firefox")) {
            //Runtime.getRuntime().exec("taskkill /F /IM " + "firefox.exe");  //kill existing browsers - not needed at moment as we close browser in tear down now. .
            Test.reportEvent("INFO", "Running testcase in firefox browser");
            Test.setDriver(new FirefoxDriver());

        } else if (AppProperties.get("browser").equalsIgnoreCase("internetexplorer")) {
            System.setProperty("webdriver.ie.driver", AppProperties.get("resourcesPath") + "IEDriverServer.exe");
            Test.reportEvent("INFO", "Running testcase in internetexplorer browser");
            cap = DesiredCapabilities.internetExplorer();

            Test.setDriver(new InternetExplorerDriver(cap));
        } else if (AppProperties.get("browser").equalsIgnoreCase("chrome")) {
            System.setProperty("webdriver.chrome.driver", AppProperties.get("resourcesPath") + "chromedriver.exe");
            Test.reportEvent("INFO", "Running testcase in chrome browser");
            cap = DesiredCapabilities.chrome();
            Test.setDriver(new ChromeDriver(cap));


        }

        // Set the default timeout wait
        //Test.getDriver().manage().timeouts().implicitlyWait(Test.getTimeOutWait(), TimeUnit.SECONDS);
        Test.getDriver().manage().window().maximize();
        // Have to put the following in because of dodgy IE behaviour where it says is is still loading the page
        // and just hangs unless you put this in.
        Test.getDriver().manage().timeouts().pageLoadTimeout(Test.getPageLoadTimeOutWait(), TimeUnit.SECONDS);

        numberOfDriverInstances = numberOfDriverInstances + 1;
        String driverInstance = String.valueOf(numberOfDriverInstances);
        driverInstances.put(driverInstance, Test.getDriver());
    }

    public static String evaluateScript(String scriptString, String type) {
        String evaluatedString = scriptString;  // If below fails, will return the same string, unevaluated.

        try {
            Object myObject = null;
            if (type.equals("")) {
                myObject = jsScriptEngine.eval(scriptString);
                evaluatedString = myObject.toString();
            } else if (type.toLowerCase().equals("numeric")) {
                myObject = jsScriptNumberEngine.eval(scriptString);
                evaluatedString = myObject.toString();
            }
            reportEvent("INFO", "Formula '" + scriptString + "' evaluated to: " + evaluatedString);

        } catch (ScriptException e) {
            //Just catch the exception. So can still pass in stuff with $ signs and it will just not evaluate it
            reportEvent("INFO", "Formula '" + scriptString + "' cannot be evaluated");
        }
        return evaluatedString;
    }

    public static void updateInitialTestResultFile(File fileToUpdate, String textForHeading, String textForRunLog) throws FileNotFoundException, IOException {

        // The name of the file:
        String fileName = fileToUpdate.getPath();
        File file = new File(fileName);
       /* if(!file.exists()) {
            file.createNewFile();
        }*/
        boolean foundKey = false;
        String newText = "";


        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            //Build the text of the file line by line - replace line of this selenium with latest result if exists. Otherwise append to end:
            while ((line = bufferedReader.readLine()) != null) {

                if (line.toLowerCase().contains("</h1>") && !foundKey) {
                    int endOfH1Tag = line.toLowerCase().indexOf("<h1>") + 4;
                    newText = newText + line.substring(0, endOfH1Tag) + textForHeading +
                            "</h1>" + System.getProperty("line.separator");
                    foundKey = true;
                    if (!textForRunLog.equals("")) {
                        newText = newText + textForRunLog + System.getProperty("line.separator");
                    }
                } else {
                    newText = newText + line + System.getProperty("line.separator");
                }
            }
            // Close file.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            reportEvent("INFO", "Unable to update test results header and and run log link " + fileName);

        } catch (IOException ex) {
            reportEvent("INFO",
                    "Error reading file '"
                            + fileName + "'"
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }

        // Now update the newText to the file:

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            bufferedWriter.write(newText);

            // Always close files.
            bufferedWriter.close();
        } catch (IOException ex) {
            reportEvent("INFO",
                    "Error writing to file '"
                            + fileName + "'"
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }


    }

    public static void updateHtmlFile(File fileToUpdate, String textToAddToStartOfHeading, String textToChangeHeadingTo, boolean movingPreviousRun) {

        // The name of the file:
        String fileName = fileToUpdate.getPath();

        boolean foundKey = false;
        String newText = "";


        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            //Build the text of the file line by line - replace line of this selenium with latest result if exists. Otherwise append to end:
            while ((line = bufferedReader.readLine()) != null) {

                if (line.toLowerCase().contains("</h1>") && !foundKey) {
                    int endOfH1Tag = line.toLowerCase().indexOf("<h1>") + 4;
                    if (!textToChangeHeadingTo.equals("")) {
                        //Actually want to replace heading. .
                        newText = newText + line.substring(0, endOfH1Tag) + textToAddToStartOfHeading +
                                textToChangeHeadingTo +
                                "</h1>" + System.getProperty("line.separator");

                    } else {
                        //Just adding stuff to start of existing heading
                        newText = newText + line.substring(0, endOfH1Tag) + textToAddToStartOfHeading +
                                line.substring(endOfH1Tag, line.toLowerCase().indexOf("</h1>")) +
                                "</h1>" + System.getProperty("line.separator");
                    }

                    foundKey = true;
                } else if (line.contains("<span class=\"breadcrumbs\">")) {
                    //remove breadcrumbs from saved previous runs or from called tests as they don't make sense and won't work:
                    int startOfBreadCrumbs = line.indexOf("<span class=\"breadcrumbs\">");
                    int endOfBreadCrumbs = line.indexOf("</span>") + 7;
                    newText = newText + line.substring(0, startOfBreadCrumbs) + line.substring(endOfBreadCrumbs) + System.getProperty("line.separator");
                } else {
                    if (movingPreviousRun) {
                        //Need to change any links to results of called tests and runlogs to one level further down
                        // Do this by adding a "." to start of link href. E.g change ./currentlink to ../currentlink
                        //check for result
                        if (line.contains("<a href=") && (line.contains("Results</a></td>")) && (line.contains("PASS") || line.contains("FAIL"))) {
                            int upToHref = line.indexOf("<a href=") + 9;
                            newText = newText + line.substring(0, upToHref) + "." + line.substring(upToHref) + System.getProperty("line.separator");
                        }
                        // check for run log
                        else if (line.contains("<a href=") && (line.contains(">Run Log</a>"))) {
                            int upToHref = line.indexOf("<a href=") + 9;
                            newText = newText + line.substring(0, upToHref) + "." + line.substring(upToHref) + System.getProperty("line.separator");
                        } else {
                            newText = newText + line + System.getProperty("line.separator");
                        }

                    } else {
                        newText = newText + line + System.getProperty("line.separator");
                    }

                }
            }
            // Close file.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            reportEvent("INOF", "Unable to update called test header");

        } catch (IOException ex) {
            reportEvent("INFO",
                    "Error reading file '"
                            + fileName + "'"
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }

        // Now update the newText to the file:

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            bufferedWriter.write(newText);

            // Always close files.
            bufferedWriter.close();
        } catch (IOException ex) {
            reportEvent("INFO",
                    "Error writing to file '"
                            + fileName + "'"
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }


    }

    public static void writeToFile(File fileToUpdate, String textToWrite) {

        // The name of the file:
        String fileName = fileToUpdate.getPath();

        String newText = textToWrite;
        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            bufferedWriter.write(newText);

            // Always close files.
            bufferedWriter.close();
        } catch (IOException ex) {
            reportEvent("INFO",
                    "Error writing to file '"
                            + fileName + "'"
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }


    }


    public static void startTestLogFile() {
        //Per main test (i.e. initial calling test), all events reported through reportEvent method will be written to a file.
        //Set up timestamp:
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = sdf.format(date);
        testLogFullPath = testLogName + formattedDate + ".txt";
        File testLogFile = new File(testLogFullPath);
        try {
            testLogFile.getParentFile().mkdirs();
            testLogFile.createNewFile();
        } catch (Exception emf) {
            System.out.println("INFO " + emf.toString());
        }


        // This will reference one line at a time
        String line = null;

        // Now update the newText to the file:

        String lineToWrite = "";

        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(testLogFile);

            // Wrap FileWriter in BufferedWriter.
            testLogWriter =
                    new BufferedWriter(fileWriter);
            lineToWrite = "********************************Test Log for " +
                    Test.getTestLogName() + " run on " + formattedDate + "**************" + System.getProperty("line.separator");
            testLogWriter.write(lineToWrite);

            // Always close files.
            //testLogWriter.close();
        } catch (Exception ex) {
            System.out.println(
                    "Error writing to file '"
                            + testLogFile + "' " + ex.toString()
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }


    }

    // function to create run time data excelsheet to store run time data in the common location. This function checks if there is file and creates if not   -- by radhika 12/11/2013
    public static void CreateRunTimeDataFile() {
        String filepath = AppProperties.get("seleniumRunTimeDataFilePath").replace("dxxx", AppProperties.get("env"));
        filepath = "\\" + filepath;

        try {

            File yourFile = new File(filepath);
            if (!yourFile.exists()) { //create file only if it does not exist
                yourFile.createNewFile();
                XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet("Selenium Runtime Data");
                Row row = sheet.createRow(0);

                Cell cell = row.createCell(0);
                cell.setCellValue("InitialScriptName");
                cell = row.createCell(1);
                cell.setCellValue("VariableName");
                cell = row.createCell(2);
                cell.setCellValue("Value");
                cell = row.createCell(3);
                cell.setCellValue("TimeStamp");

                FileOutputStream fileOut = new FileOutputStream(filepath, false);
                wb.write(fileOut);
                fileOut.close();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /// end of code by radhika   - create run time data file

    //write to selenium run time data file- this is called from the action writetoruntimedata       -- by radhika
    public static void WritetoRunTimeData(String inputData) {
        String filepath = AppProperties.get("seleniumRunTimeDataFilePath").replace("dxxx", AppProperties.get("env"));
        filepath = "\\" + filepath;
        int isPresent = 0;
        try {

            File yourFile = new File(filepath);
            if (!yourFile.exists()) { //create file only if it does not exist
                System.out.println(" runtime data excel file not found");
            } else {

                String[] a = inputData.split(",");

                //code for strign the variable value to string
                for (int i = 0; i < a.length; i++) {
                    if (a[i].startsWith("$")) {
                        Object myObject = null;
                        myObject = jsScriptEngine.eval(a[i]);
                        a[i] = myObject.toString();
                    }
                }

                InputStream inp = new FileInputStream(filepath);
                Workbook wb = WorkbookFactory.create(inp);
                Sheet sheet = wb.getSheetAt(0);
                int rowcount = sheet.getLastRowNum();

                //code when already runtime data exists int he file
                if (rowcount > 0) {
                    System.out.println(rowcount);


                    for (int i = 0; i <= rowcount; i++) {
                        Row row = sheet.getRow(i);
                        System.out.println("values--" + row.getCell(1) + "," + row.getCell(2));
                        //to check if the variable and value are already present
                        if (a[1].equalsIgnoreCase(row.getCell(1).toString()) && a[2].equalsIgnoreCase(row.getCell(2).toString())) {
                            System.out.println("Variable,Value already present");
                            isPresent = 1;
                            //write the code for overriding the value
                            Row existingrow = sheet.createRow(i);
                            for (int j = 0; j < a.length; j++) {
                                Cell cell = existingrow.createCell(j);
                                cell.setCellValue(a[j]);
                            }

                            break;
                        }

                    }

                    if (isPresent == 0) {
                        Row newrow = sheet.createRow(rowcount + 1);
                        for (int j = 0; j < a.length; j++) {
                            Cell cell = newrow.createCell(j);
                            cell.setCellValue(a[j]);
                        }

                    }


                } else {              //this is for an empty file the very first time that is writing an input value


                    Row newrow = sheet.createRow(rowcount + 1);
                    for (int j = 0; j < a.length; j++) {
                        Cell cell = newrow.createCell(j);
                        cell.setCellValue(a[j]);
                    }


                }
                // Write the output to a file
                FileOutputStream fileOut = new FileOutputStream(filepath);
                wb.write(fileOut);
                fileOut.close();


            }


        } catch (Exception ex) {
            System.out.println("Failed to write to runtime data excel file " + inputData + " Error: " + ex.toString());
        }

    }


    //-- end of code : write to selenium run time data file    - by radhika


    /// reading from run time data file        -- by radhika
    public static String ReadFromRunTimeData(String inputData) {
        String filepath = AppProperties.get("seleniumRunTimeDataFilePath").replace("dxxx", AppProperties.get("env"));
        filepath = "\\" + filepath;
        String value = null;
        try {

            File yourFile = new File(filepath);
            if (!yourFile.exists()) { //create file only if it does not exist
                System.out.println(" runtime data excel file not found");
            } else {
                String[] a = inputData.split(",");
                InputStream inp = new FileInputStream(filepath);
                Workbook wb = WorkbookFactory.create(inp);
                Sheet sheet = wb.getSheetAt(0);
                int rowcount = sheet.getLastRowNum();
                //code when already runtime data exists in the file
                if (rowcount > 0) {
                    System.out.println(rowcount);
                    for (int i = 0; i <= rowcount; i++) {
                        Row row = sheet.getRow(i);
                        System.out.println("values--" + row.getCell(1) + "," + row.getCell(2));
                        //to check if the variable and value are already present
                        if (a[0].equalsIgnoreCase(row.getCell(0).toString()) && a[1].equalsIgnoreCase(row.getCell(1).toString())) {
                            System.out.println("Scriptname ,variable present");
                            value = row.getCell(2).toString();
                            break;
                        }
                    }
                }
                /*// Write the output to a file
                FileOutputStream fileOut = new FileOutputStream(filepath);
                wb.write(fileOut);
                fileOut.close();
                */
            }
            return value;

        } catch (Throwable ex) {
            return "Failed to read data from runtime data excel file " + inputData + " Error: " + ex.toString();
        }

    }

    /// En dof code::: reading from run time data file        -- by radhika


    public static String extractExcelData(String inputvalue) {
        excelData.clear();

        try {

            FileInputStream file = new FileInputStream(AppProperties.get("resourcesPath") + inputvalue);

            XSSFWorkbook workbook = new XSSFWorkbook(file);

            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();

            boolean headerRowStored = false;

            int excelDataRowNumber = 0;
            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                //First row should be header containing the names of the parameters
                Row headerRow = sheet.getRow(0);
                if (i == 0) {
                    HashMap<String, String> header = new HashMap<String, String>();
                    int k = 0;
                    while (headerRow.getCell(k) != null && k < 30000) {
                        if (!headerRow.getCell(k).getStringCellValue().equals("")) {
                            String parameterInstance = String.valueOf(k + 1);
                            String parameterName = headerRow.getCell(k).getStringCellValue();
                            header.put(parameterInstance, parameterName);
                        }
                        k = k + 1;
                    }
                    if (header.size() == 0) {
                        break;
                    } else {
                        excelData.put(String.valueOf(0), header);//Store columns names at zero along with their column number;
                    }
                }
                if (i > 0) {
                    HashMap<String, String> currentRow = new HashMap<String, String>(); //must re-initialise object each time - otherwise when add currentRow will be same object added each time.
                    currentRow.clear();
                    int j = 0;

                    while (headerRow.getCell(j) != null && j < 30000) {
                        if (!headerRow.getCell(j).getStringCellValue().equals("")) {
                            String parameterKey = headerRow.getCell(j).getStringCellValue();
                            String parameterValue = "";
                            if (row.getCell(j) != null) {
                                if (row.getCell(j).getCellType() == row.getCell(j).CELL_TYPE_NUMERIC) {
                                    parameterValue = String.valueOf(row.getCell(j).getNumericCellValue());
                                } else {
                                    parameterValue = row.getCell(j).getStringCellValue();
                                }

                            }
                            currentRow.put(parameterKey, parameterValue);
                        } else {
                            break; //reached an empty column heading
                        }
                        j = j + 1;
                    }
                    if (currentRow.size() > 0) {
                        excelDataRowNumber = excelDataRowNumber + 1;
                        String rowKey = String.valueOf(excelDataRowNumber);
                        excelData.put(rowKey, currentRow);
                        //excelData.put(rowKey, (HashMap<String,String>) currentRow.clone());
                    } else {
                        break; // No columns in heading
                    }

                }
            }
            return "";

        } catch (Throwable err) {


            return "Failed to extract cell data for excel file " + inputvalue + " Error: " + err.toString();
        }

    }


}
