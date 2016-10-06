package com.selenium.automatedTest.engine;

import com.selenium.automatedTest.extensions.LinkingExtension;
import com.selenium.automatedTest.extensions.ProduceTestSetResults;
import com.selenium.automatedTest.extensions.TestSetTestResult;
import org.apache.commons.io.FileUtils;
import org.concordion.api.extension.Extensions;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.selenium.automatedTest.engine.Test.quitAllDrivers;
import static com.selenium.automatedTest.engine.Test.reportEvent;

@RunWith(ConcordionRunner.class)
@Extensions({LinkingExtension.class})
public class StepBase {

    public static String currentResource = null;
    public String testType = "INITIALCALLINGTEST";

    private HashMap<String, String> methodParameters = new HashMap<String, String>();
    private int failureCount;
    private String testStatus = "";
    private static String currentTestStatus = "";
    private boolean testStarted = false;

    private boolean stepSuccessful = true;
    private boolean anyStepsFellOver = false;
    String TEST_CONFIG_FILE = "TestConfig.properties";
    //Location of results output and testscript names:
    private String resultsOutput = System.getProperty("concordion.output.dir").replaceAll("/", "\\\\");  //get location of results
    private String resultsOutputTopLevel = resultsOutput + "\\com\\selenium\\automatedTest";
    //*****CODE To get test script name and location if running in a separate java class with same name*****
    //private java.net.URL currentTestClassLocation = this.getClass().getResource("");  //get full path of class
    //private String currentResultLocation = resultsOutput + currentTestClassLocation.toString().substring(currentTestClassLocation.toString().indexOf("/com/selenium/automatedTest/")).replaceAll("/", "\\\\");
    //private String currentScriptName = this.getClass().getSimpleName().substring(0,this.getClass().getSimpleName().length());
    //************************************************************************************************
    //Get test script name if running with the single java class Sample via RunTest:
    File currentResourceFile = new File(currentResource);
    String callingTestLocation = currentResourceFile.getParentFile().getPath() + "\\";
    String currentResultLocation = resultsOutput + currentResourceFile.getParentFile().getPath() + "\\";
    String currentScriptName = currentResourceFile.getName().substring(0, currentResourceFile.getName().length() - 5);
    String callingTestName = currentScriptName;

    @After
    public void tearDown() {
        //TODO work out a better way of working out if the selenium passed or failed - there MUST be a way of just getting the result. . !!!
        if (failureCount > 0) {
            //NOTE: Don't need this fail statement to fail the selenium as the assertEquals concordion statement for each row in the selenium will cause the failure.
            //However handy as a catch all - to ensure the selenium really does fail
            //TODO count number of successes too. Report to the script at the end too if possible. .
            //fail("Failures occurred: " + failureCount);
            testStatus = "Failed";

        }
        //If for any reason a step failed to complete this would be picked up by stepSuccessful being false:

        if (stepSuccessful == false) {
            testStatus = "Failed";

        }
        if (anyStepsFellOver == true) {

            testStatus = "Failed";

        }
        if (!testStarted) {
            testStatus = "Failed";
        }
        if (!testStatus.equals("Failed")) {
            testStatus = "Passed";

        }
        Test.clearCallParameters();
        currentTestStatus = testStatus;

        if (testType.equals("INITIALCALLINGTEST")) {
            reportEvent("INFO", "Test " + currentScriptName + " completed. Test Status: " + testStatus);
            reportEvent("DIVIDER", "");
            reportEvent("DIVIDER", "");
            writeToSummaryResultFile();
            buildTestResults();
            String testResultLocation = currentResultLocation + currentScriptName + ".html";
            testResultLocation = testResultLocation.replaceAll("/", "\\\\");
            String testLogLink = "./" + (new File(Test.getTestLogFullPath())).getName();
            try{
                Test.updateInitialTestResultFile(new File(testResultLocation), currentScriptName, " <h2><a href='" + testLogLink + "' target='_blank'>Run Log</a></h2>");
            } catch(Exception e){
                            // Always must return something
            }
            Test.closeDownTestLogWriter();
            //Test.getDriver().quit();
            quitAllDrivers(); //Close all driver sessions (more than one may exist if user has opened up another browser using openbrowser action)
        }

        if (testType.equals("CALLEDTEST")) {
            reportEvent("INFO", "--------Called script " + currentScriptName + " completed. Called Test Script Status: " + testStatus);
            reportEvent("INFO", "Test continuing in calling test. . .");
            reportEvent("DIVIDER", "");
        }
    }

    @Before
    public void setUpTest() throws Exception {
        currentTestStatus = "";
        reportEvent("INFO", "Start of test '" + currentScriptName + "'");
        //Initialise Test objects:
        if (testType.equals("INITIALCALLINGTEST")) {
            Test.setRootElement(null);
            Test.setCurrentRoot(null);
            Test.clearExcelData();
            Test.setTestLogName("");
            Test.clearScriptVariables();
            Test.clearDriverInstances();
            Test.initialiseJSScriptEngine();
            Test.setEndTest(false);
            Test.clearCallParameters();
            //Call stack instances which store to Script Variables:
            Test.clearCallStackInstances();  //First Clear
            Test.setCallStackInstance(0);   //Now set to zero
            methodParameters.clear();
        }
        Test.setCallingTestLocation(callingTestLocation);
        Test.setCallingTestName(callingTestName);
        failureCount = 0;
        testStarted = false;

        if (testType.equals("INITIALCALLINGTEST")) {
            //Move previous selenium result to archive folder
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String currentTestResultLocation = currentResultLocation + currentScriptName + ".html";
            String currentTestRunLogName = currentResultLocation + currentScriptName;
            Test.setTestLogName(currentTestRunLogName.replaceAll("/", "\\\\"));
            Test.startTestLogFile();
            currentTestResultLocation = currentTestResultLocation.replaceAll("/", "\\\\");
            File f1 = new File(currentTestResultLocation);
            String formattedDate = sdf.format(f1.lastModified());
            String newCurrentTestResultLocation = currentResultLocation + currentScriptName + "_PreviousRuns/" + currentScriptName + formattedDate + ".html";
            newCurrentTestResultLocation = newCurrentTestResultLocation.replaceAll("/", "\\\\");
            File f2 = new File(newCurrentTestResultLocation);
            try {
                FileUtils.moveFile(f1, f2);
                Test.updateHtmlFile(f2, "RESULT FROM A PREVIOUS RUN:", currentScriptName, true);
            } catch (FileNotFoundException ex) {
            } catch (Exception g) {
                System.out.println("[ERROR] No previous run result moved to archive: Reason: " + g.toString());
            }
            Test.openBrowser();
        }

    }

    public static String getCurrentTestStatus() {
        return currentTestStatus;
    }

    public void setTestType(String s) {
        testType = s;
    }

    public Result performAction(String rowswitch, String comment, String bytype, String bytext, String byinnertext, String action, String inputvalue, String wait, String resultname) throws Exception {
        //Determine which type of action. Will either be setting up a parameter, calling a function -
        //- or performing an action on an element.
        //TODO use comment in log messaging. .
        testStarted = true;
        if (stepSuccessful == false) {
            anyStepsFellOver = true;
        }
        Result performActionResult = new Result();
        //Evaluate the rowswitch if a formula has been entered:
        if (rowswitch.contains("$")) {
            rowswitch = Test.evaluateScript(rowswitch, "");
        }
        if (Test.getEndTest() == true) {
            //Either an "endtest" action was encountered (only used when people developing tests)
            // or the test was stopped by closing browser window.
            performActionResult.setOutcome("PASS");
            performActionResult.setErrorMessage("Test ended");
            performActionResult.setOutput("");
        } else if (rowswitch.startsWith("//")) {
            //Row is commented out / is just a comment row
            performActionResult.setOutcome("PASS");
            performActionResult.setErrorMessage("");
            performActionResult.setOutput("NOTE: **This step was not performed. The row was commented out.**");
            reportEvent("INFO", "Step: '" + comment + "' was not performed. Row was commented out.");
            reportEvent("DIVIDER", "");
        } else if (rowswitch.toLowerCase().equals("false")) {
            //Conditional step - not going to be performed
            performActionResult.setOutcome("PASS");
            performActionResult.setErrorMessage("");
            performActionResult.setOutput("NOTE: **This step was not performed. Condition in rowswitch was not met.**");
            reportEvent("INFO", "Step: '" + comment + "' was not performed. Condition in rowswitch not met.");
            reportEvent("DIVIDER", "");
        } else {
            stepSuccessful = false;
            performActionResult.setOutcome("FAIL"); //initialise to failure - will only get set to PASS if successful below
            if (!(action.toLowerCase().equals("callmethod") || action.toLowerCase().equals("callhtml") || action.toLowerCase().equals("setparameter") || action.toLowerCase().equals("getparameter") || action.toLowerCase().equals("getoptionalparameter"))) {
                Test.clearCallParameters();
            }
            methodParameters.clear();
            methodParameters.put("comment", comment);
            methodParameters.put("bytype", bytype);
            methodParameters.put("bytext", bytext);
            methodParameters.put("byinnertext", byinnertext);
            methodParameters.put("action", action);
            methodParameters.put("inputvalue", inputvalue);
            methodParameters.put("wait", wait);
            methodParameters.put("resultname", resultname);
            performActionResult = Test.actOnElement(methodParameters);
            methodParameters.clear(); //TODO think about this we are clearing before and after (after because navigate action sets up baseurl parm)
            if (action.toLowerCase().equals("callmethod") || action.toLowerCase().equals("callhtml")) {
                Test.clearCallParameters();
            }
        }
        if (rowswitch.toLowerCase().contains("allowfail") && !performActionResult.getOutcome().equals("PASS")) {
            reportEvent("INFO", "Step failed but failure allowed for this step. Setting step result to PASS");
            performActionResult.setOutcome("PASS");
        }
        if (!performActionResult.getOutcome().equals("PASS")) {
            failureCount = failureCount + 1;
        } else {
            stepSuccessful = true;
        }
        return performActionResult;
    }

    public String getMethodParameter(String k) {
        //Get the parameter:
        return methodParameters.get(k);
    }

    public void setMethodParameter(String k, String v) {
        try {
            methodParameters.put(k, v);
        } catch (Error e) {

        }
    }

    public Result evaluateSetMethodParameter(String key, String val) {
        //If script variable passed in, use that and not the literal value
        boolean bcontinue = true;
        String k = key;
        String v = "";
        Result setParamResult = new Result();
        if (val.contains("$")) {
            v = Test.evaluateScript(val, "");
        } else {
            //Just a literal value
            v = val;
        }
        if (bcontinue) {
            try {
                methodParameters.put(k, v);
                setParamResult.setOutcome("PASS");
                setParamResult.setErrorMessage("");
            } catch (Error e) {
                setParamResult.setOutcome("FAIL");
                setParamResult.setErrorMessage(e.toString());
            }
        }
        setParamResult.setOutput(v);
        return setParamResult;
    }

    public Result callActionMethod(String methodname, HashMap<String, String> hm, String resultname) throws Exception {
        //TODO need to maybe make this abstract and implement in each project. .  want the runnerlist to be specific to each app anyway. .
        Result callActionResult = new Result();
        reportEvent("INFO", "Starting step which calls method '" + methodname + "'");
        Runner currentRunner = RunnerList.runners.get(methodname.toLowerCase());
        if (currentRunner == null) {
            // method does not exist
            callActionResult.setOutcome("FAIL");
            callActionResult.setErrorMessage("Call Method: '" + methodname + "' does not exist.");
        } else {
            callActionResult = currentRunner.run(Test.getDriver(), hm);
        }

        if (!resultname.equals("")) {
            Result setScriptValueResult = Test.setScriptValue(resultname, callActionResult.getOutput());
            if (!setScriptValueResult.getOutcome().equals("PASS")) {
                callActionResult.setOutcome("FAIL");
                callActionResult.setErrorMessage(callActionResult.getErrorMessage() + " " + setScriptValueResult.getErrorMessage());
            }
        }
        if (!callActionResult.getErrorMessage().equals("")) {
            reportEvent("INFO", callActionResult.getErrorMessage());
        }
        reportEvent(callActionResult.getOutcome(), "Overall outcome of step calling method '" + methodname + "'");
        reportEvent("DIVIDER", "");
        return callActionResult;
    }

    public HashMap<String, String> getMethodParameters() {
        return methodParameters;
    }

    public void navTo(String s) {
        Test.getDriver().get(s);
    }

    private void writeToSummaryResultFile() {
        //This will read through the Results Summary File and check to see if the test
        //exists already in there. If it does, will update it with latest result.
        //If not will create a new entry for it.

        //Set up timestamp:
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(date);
        // The name of the file:
        String fileName = resultsOutputTopLevel + "\\TestResultSummary.txt";
        String newText = "";
        String keyText = "#####" + currentResultLocation + currentScriptName + "###";
        boolean foundKey = false;
        String currentTestResultLine = "#####" + currentResultLocation + currentScriptName + "###" + testStatus + "###" + formattedDate + "###";

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

                if (line.startsWith(keyText)) {
                    newText = newText + currentTestResultLine + System.getProperty("line.separator");
                    foundKey = true;
                } else {
                    newText = newText + line + System.getProperty("line.separator");
                }
            }
            if (foundKey == false) {
                newText = newText + currentTestResultLine + System.getProperty("line.separator");
            }

            // Close file.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            newText = newText + currentTestResultLine + System.getProperty("line.separator");

        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'"
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }

        // Now update the newText to the file:

        String lineToWrite = "";

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
            System.out.println(
                    "Error writing to file '"
                            + fileName + "'"
            );
        }
    }

    private void buildTestResults() {
        ProduceTestSetResults produceResults = new ProduceTestSetResults();

        // The name of the file:
        String fileName = resultsOutputTopLevel + "\\TestResultSummary.txt";

        // This will reference one line at a time
        String line = null;
        String currentTest;
        String currentTestName;
        String currentTestResult;
        String currentTestDate;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader =
                    new FileReader(fileName);

            // Wrap FileReader in BufferedReader.
            BufferedReader bufferedReader =
                    new BufferedReader(fileReader);

            //Build the text of the file line by line - replace line of this selenium with latest result if exists. Otherwise append to end:
            while ((line = bufferedReader.readLine()) != null) {
                //Extract out each line
                int firstHashLocation = line.indexOf("###", 5);
                int secondHashLocation = line.indexOf("###", firstHashLocation + 3);
                int thirdHashLocation = line.indexOf("###", secondHashLocation + 3);
                currentTest = line.substring(5, firstHashLocation) + ".html";
                //Need to just get the name of the selenium:
                currentTestName = new File(currentTest).getName();
                currentTestResult = line.substring(firstHashLocation + 3, secondHashLocation);
                currentTestDate = line.substring(secondHashLocation + 3, thirdHashLocation);
                //TestSetTestResult tstr = new TestSetTestResult(currentTestDate, currentTest,currentTest,currentTestResult);
                int level = produceResults.getSummaryLevel(currentTest);

                produceResults.updateSummaryIndexFile(currentTest, level, new TestSetTestResult(currentTestDate, currentTestName, currentTestName, currentTestResult));
            }
            // Close file.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" +
                            fileName + "'"
            );
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'"
            );
        }
        produceResults = null;
    }
}