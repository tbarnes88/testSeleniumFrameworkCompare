package com.selenium.automatedTest;

import com.selenium.automatedTest.engine.AppProperties;
import com.selenium.automatedTest.engine.Test;
import com.selenium.automatedTest.extensions.RunTestFunctions;
import org.concordion.internal.ClassNameBasedSpecificationLocator;
import org.junit.runner.JUnitCore;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RunTest {

    public static void main(String[] args) {
        try {
            String defaultPath = AppProperties.get("defaultTestPath");
            if (args == null || args.length == 0) {

                //code for running the Run test from IDE
                System.setProperty("concordion.output.dir", "C:\\SeleniumResults");  //set the results folder
                String filenameName = "test.html";   //have to give the absolute path
                runTestCaseFile(defaultPath + filenameName);
                //----END OF CODE // code for running the Run test from IDE
            } else {

                String filenameNameOrDirectoryLoc = args[0];
                String OriginalInputFile = args[0];
                String[] testFiles = null;
                boolean isCommaSeperatedFiles = false;

                //CODE for Running the failed tests
                if (args.length == 2 && args[1].equalsIgnoreCase("ONLYFAIL") && !filenameNameOrDirectoryLoc.contains(",")) {

                    runFailedTest(defaultPath, OriginalInputFile);

                } else {
                    //CODE for running the tests when comma seperated
                    if (filenameNameOrDirectoryLoc.contains(",")) {

                        isCommaSeperatedFiles = true;
                        List<String> lstNumbers = new ArrayList<String>();
                        testFiles = filenameNameOrDirectoryLoc.split(",");

                        if (args.length == 2 && args[1].equalsIgnoreCase("ONLYFAIL")) {

                            String strFailedTestList = RunTestFunctions.getListOfFailedTestsForFolder(System.getProperty("concordion.output.dir"), "");
                            String[] FailedtestFiles = strFailedTestList.split(",");

                            for (String eachfile : testFiles) {

                                for (String eachFailedfile : FailedtestFiles) {
                                    if (eachfile.equalsIgnoreCase(eachFailedfile)) {
                                        lstNumbers.add(eachFailedfile);
                                    }
                                }
                            }

                            Set<String> uniqueParentPath = new HashSet<String>(lstNumbers);
                            for (String value : uniqueParentPath) {
                                Test.reportEvent("INFO", "Running the Testcase" + defaultPath + value.trim());
                                runTestCaseFile(defaultPath + value.trim());

                            }

                        } else {
                            for (String eachFile : testFiles) {

                                Test.reportEvent("INFO", "Running the Testcase" + defaultPath + eachFile.trim());
                                runTestCaseFile(defaultPath + eachFile.trim());

                            }
                        }

                        //CODE for Running the failed tests
                        if (args.length == 2 && args[1].equalsIgnoreCase("Y")) {

                            String strFailedTestList = RunTestFunctions.getListOfFailedTestsForFolder(System.getProperty("concordion.output.dir"), "");
                            String[] FailedtestFiles = strFailedTestList.split(",");

                            for (String eachfile : testFiles) {

                                for (String eachFailedfile : FailedtestFiles) {
                                    if (eachfile.equalsIgnoreCase(eachFailedfile)) {
                                        lstNumbers.add(eachFailedfile);
                                    }
                                }
                            }

                            Set<String> uniqueParentPath = new HashSet<String>(lstNumbers);
                            for (String value : uniqueParentPath) {
                                Test.reportEvent("INFO", "Running the Testcase" + defaultPath + value.trim());
                                runTestCaseFile(defaultPath + value.trim());
                            }
                        }
                    } else {

                        if (filenameNameOrDirectoryLoc.endsWith(".html")) {
                            String messageText = "Running a single test case file " + args[0];
                            Test.reportEvent("INFO", messageText);
                            runTestCaseFile(defaultPath + filenameNameOrDirectoryLoc);
                        } else {

                            ClassLoader classLoader = new RunTest().getClass().getClassLoader();
                            final File classpathRoot = new File(classLoader.getResource("").getPath());

                            filenameNameOrDirectoryLoc = defaultPath + filenameNameOrDirectoryLoc;
                            final File input = new File(classpathRoot.getPath() + filenameNameOrDirectoryLoc);

                            if (input.isDirectory()) {

                                File f = new File(input.getPath());
                                displayDirectoryContents(f, filenameNameOrDirectoryLoc);

                            }
                        }
                    }
                    //CODE for Running the failed tests
                    if (args.length == 2 && args[1].equalsIgnoreCase("Y")) {
                        if (isCommaSeperatedFiles == true) {

                        } else {

                            runFailedTest(defaultPath, OriginalInputFile);

                        }
                    }

                }

            }
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

    }

    //Function to run a testcase
    private static void runTestCaseFile(String fileName) {
        ClassNameBasedSpecificationLocator.currentResource = fileName;
        AllTestFixture.currentResource = fileName;
        JUnitCore.runClasses(AllTestFixture.class);

    }

    //Recursive function to get the file name from the directory and sub directory
    public static void displayDirectoryContents(File dir, String filenameNameOrDirectoryLoc) {

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                displayDirectoryContents(file, filenameNameOrDirectoryLoc + "/" + file.getName());
            } else {
                //html file name
                String OnlyFilename = file.getName().substring(0, file.getName().length() - 5);

                if (file.getName().endsWith((".html"))) {

                    if (dir.getName().equalsIgnoreCase(OnlyFilename)) {

                    } else {
                        Test.reportEvent("INFO", "Running the Testcase" + filenameNameOrDirectoryLoc + "/" + file.getName());
                        runTestCaseFile(filenameNameOrDirectoryLoc + "/" + file.getName());
                    }
                }
            }
        }

    }

    //Function to run the Failed Test
    public static void runFailedTest(String strDefaultPath, String strOriginalInputFile) {

        String strFailedTestList;
        if (strOriginalInputFile.endsWith(".html")) {
            strFailedTestList = RunTestFunctions.getListOfFailedTestsForFolder(System.getProperty("concordion.output.dir"), strOriginalInputFile.substring(0, strOriginalInputFile.length() - 5));
        } else {

            strFailedTestList = RunTestFunctions.getListOfFailedTestsForFolder(System.getProperty("concordion.output.dir"), strOriginalInputFile);
        }
        System.out.println("Return of FailedTestcaseList -------" + strFailedTestList);
        if (strFailedTestList == "0") {
            System.out.println("No failed test to Run");
        } else if (strFailedTestList.contains(",")) {
            String[] ReruntestFiles = strFailedTestList.split(",");
            for (String eachFile : ReruntestFiles) {
                Test.reportEvent("INFO", "Running the Testcase" + strDefaultPath + eachFile.trim());
                runTestCaseFile(strDefaultPath + eachFile.trim());
                //System.out.println("Running a file --- " + strDefaultPath +eachFile);
            }
        } else {
            if (strFailedTestList.endsWith(".html")) {
                //System.out.println("Running a single test case file " + strFailedTestList);
                Test.reportEvent("INFO", "Running the Testcase" + strDefaultPath + "/" + strFailedTestList);
                runTestCaseFile(strDefaultPath + "/" + strFailedTestList);
            }

        }
    }


}
