package com.selenium.automatedTest.extensions;

import org.junit.Test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: barnest
 * Date: 18/09/16
 * Time: 11:42
 * To change this template use File | Settings | File Templates.
 */
public class RunTestFunctions {

    @Test
    public void testFailedListStuff() {

        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");

        String inputvalue = "apple";

        try {
            engine.put("$myvar1", "HelloTHT");
            engine.put("$myvar2", 6);
            //String myString = (String) engine.eval("{'Hello ' +  $myvariable.substring(0,3)}");
            //Object myObject =  engine.eval("{$myvariable.equals('hhh')}");
            //Object myObject =  engine.eval("$myvar1.match($myvar2) != null");
            Object myObject = engine.eval("$myvar1.substring($myvar1.length - 3)");
            //Object myObject =  engine.eval("$myvar2 + 1");

            String myString = myObject.toString();


            System.out.println(myString);
            // JavaScript code in a String

        } catch (ScriptException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        //double currentDouble = Double.parseDouble("20,000");
        //System.out.println(currentDouble);

//        Result k1 = new Result();
//        Result k2 = k1;
//        k2.setErrorMessage("Boo");
//        System.out.println(k1.getErrorMessage());
    }

    public static String getListOfFailedTestsForFolder(String resultPath, String folderPath) {
        //  resultPath parameter should be the full path of where the results you want to get are e.g:
        // "C:\Selenium Results\"

        // This will read through the Results Summary File and build a list of failed tests within
        // the passed in folder folderPath. So pass in blank for folderPath to return ALL failed tests.
        // Or if you want to just return failed tests within a particular folder (and its subfolders)
        // pass in relative path of that folder (from automatedTest folder) so to return all
        // failed tests within folder . . .automatedTest\SmokeTests please pass in "SmokeTests"
        // To return all failed tests within folder automatedTest\RegressionTests\Delivery, pass in
        // "RegressionTests\Delivery"
        //
        // NOTE: will return "0" if no failed tests.

        String classPathTopLevel = "\\com\\selenium\\automatedTest";

        String currentTest = "";
        String currentTestName = "";
        String currentTestResult = "";
        String currentTestDate = "";
        String currentTestNameWithRelativePath = "";
        String resultsOutputTopLevel = resultPath + classPathTopLevel;

        // The name of the results file which stores all the results:
        String fileName = resultsOutputTopLevel + "\\TestResultSummary.txt";

        // The name of the folder we are looking in:
        String requiredFolder = resultsOutputTopLevel + "\\" + folderPath;

        // The returned list of failed tests:
        String failedTestList = "";

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

                int firstHashLocation = line.indexOf("###", 5);
                int secondHashLocation = line.indexOf("###", firstHashLocation + 3);
                int thirdHashLocation = line.indexOf("###", secondHashLocation + 3);
                currentTest = line.substring(5, firstHashLocation) + ".html";
                //Need to just get the name of the selenium:
                currentTestName = new File(currentTest).getName();
                currentTestNameWithRelativePath = currentTest.substring(currentTest.indexOf(classPathTopLevel) + classPathTopLevel.length() + 1); // need name from "automatedTest/" onwards
                currentTestResult = line.substring(firstHashLocation + 3, secondHashLocation);
                currentTestDate = line.substring(secondHashLocation + 3, thirdHashLocation);

                // If the current line is for a test within the folder and it is not a passed test,
                // include it in the failure list:
                if (line.startsWith("#####" + requiredFolder) && !currentTestResult.equals("Passed")) {

                    if (!failedTestList.equals("")) {
                        failedTestList = failedTestList + ",";   // If already something in the list put a comma on the end before appending next test to end
                    }
                    failedTestList = failedTestList + currentTestNameWithRelativePath;
                }

            }


            // Close file.
            bufferedReader.close();

        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Results File not found '"
                            + fileName + "'"
            );

        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                            + fileName + "'"
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }

        if (failedTestList.equals("")) {
            failedTestList = "0";
        }
        return failedTestList;


    }

}
