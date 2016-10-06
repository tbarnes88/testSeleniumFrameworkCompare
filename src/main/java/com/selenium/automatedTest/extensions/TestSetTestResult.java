package com.selenium.automatedTest.extensions;

/**
 * Represents a single line in an index file that summarises the success or failure for a
 * single test or all the tests in a sub-folder.
 *
 * @author barnest
 */
public class TestSetTestResult {
    private String endTime = "";
    private String name = "";
    private String link = "";
    private String testStatus = "";

    public TestSetTestResult(String endTime, String name, String link, String testStatus) {
        this.endTime = endTime;
        this.name = name;
        this.link = link;
        this.testStatus = testStatus;
    }

    public TestSetTestResult(TestSetTestResult result, String name, String link) {
        this.endTime = result.endTime;
        this.testStatus = result.testStatus;
        this.name = name;
        this.link = link;
    }

    public String toString() {
        return "[" + ", " + endTime + ", " + name + ", " + link + ", " + testStatus + "]";
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public TestSetTestResult combine(TestSetTestResult result) {
        String newTestStatus = this.testStatus;
        //If the test we are combining into the summary results failed then the status for this summary is failed:
        if (result.testStatus.equals("Failed")) {
            newTestStatus = "Failed";
        }
        return new TestSetTestResult(
                endTime.compareTo(result.endTime) > 0 ? endTime : result.endTime, result.name, result.link, newTestStatus);
    }

    public String getPreviousRunsFolder() {
        String previousRunsFolder = "";
        int lengthOfName = this.name.length();
        if (lengthOfName > 5) {
            if (this.name.substring(lengthOfName - 5).equals(".html")) {
                previousRunsFolder = this.name.substring(0, this.name.length() - 5) + "_PreviousRuns";
            }
        }
        return previousRunsFolder;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}

