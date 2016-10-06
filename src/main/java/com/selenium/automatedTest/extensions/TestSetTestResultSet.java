package com.selenium.automatedTest.extensions;

import java.io.File;
import java.util.Map;


/**
 * Represents all of the test result summaries to be displayed in a single file.
 *
 * @author barnest
 */
public class TestSetTestResultSet {
    private String indexFile;
    private String title;
    private Map<String, TestSetTestResult> map;
    private int level;
    private String overallStatus;
    private String topLevelResultsName = "Overall Test Run Results"; // This should match the title in the html file test/specs/com/selenium/automatedTest/AutomatedTest.html
    private String testRunName = new File(System.getProperty("concordion.output.dir").replaceAll("/", "\\\\")).getName();


    public TestSetTestResultSet(String indexFile, String title, Map<String, TestSetTestResult> map, int level) {
        this.indexFile = indexFile;
        this.title = title;
        this.map = map;
        overallStatus = "Determining status";
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, TestSetTestResult> entry : map.entrySet()) {

                if (!entry.getValue().getTestStatus().equals("Passed")) {
                    overallStatus = "Not all passed";
                }
            }
        }
        if (overallStatus.equals("Determining status")) {
            overallStatus = "All passed";
        }

        this.level = level;
    }

    public String getIndexFile() {
        return indexFile;
    }

    public String getParentFileName() {
        String parentfile = new File(indexFile).getParentFile().getParentFile().getName();
        if (this.level == 0) {
            parentfile = "";
        }
//        if (this.level == 1){
//            parentfile = "summaryindex";
//        }
        return parentfile;
    }


    public String getParentFileTitle() {
        String parentfiletitle = new File(indexFile).getParentFile().getParentFile().getName();
        if (this.level == 0) {
            parentfiletitle = "";
        }
        if (this.level == 1) {
            parentfiletitle = topLevelResultsName;
        }
        return parentfiletitle;
    }


    public String getBreadCrumbs() {
        String breadCrumbs = "";
        String currentParentName = "";
        String currentParentTitle = "";
        String currentParentPath = "";
        String currentFile = "";
        File f = null;
        int i = this.level;
        String pathDivider = "../";
        while (i > 0) {

            if (i == this.level) {
                currentFile = indexFile;

            } else {
                currentFile = currentParentPath;

            }
            f = new File(currentFile);

            currentParentPath = f.getParentFile().getParentFile().getPath() + "\\" + f.getParentFile().getParentFile().getName() + ".html";
            currentParentTitle = f.getParentFile().getParentFile().getName();
            currentParentName = currentParentTitle + ".html";
            if (i == 1) {
                currentParentTitle = topLevelResultsName;
            }


            breadCrumbs = "<a href=" + pathDivider + currentParentName + ">" + currentParentTitle + "</a> &gt " + breadCrumbs;
            pathDivider = pathDivider + "../";
            i = i - 1;
        }

//        if (this.level > 0){
//            // Only include top level breadcrumb in there if not at top level:
//            breadCrumbs = "<a href=" + pathDivider + currentParentName + ">"+ testRunName +"</a> " + breadCrumbs;
//
//        }

        return breadCrumbs;
    }


    public String getTopLevelName() {
        String topLevelName = "";
        //Return blank if level 0 or 1 - as don't want to have link back to top level on this pages
        if (this.level > 1) {
            topLevelName = topLevelResultsName;
        }
        return topLevelName;
    }

    public String getTestRunName() {
        return testRunName;
    }

    public String getOutputTitle() {
        String outputTitle = this.title;
        if (this.level == 0) {
            outputTitle = topLevelResultsName;
        }
        return outputTitle;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, TestSetTestResult> getMap() {
        return map;
    }

    public String getOverallStatus() {
        return overallStatus;
    }

    public int getLevel() {
        return level;
    }

    //public TestEnvironment getTestEnvironment() {
    //	return TestEnvironment.getTestEnvironment();
    //}
}