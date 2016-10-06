package com.selenium.automatedTest.extensions;

import com.selenium.automatedTest.velocity.VelocityUtilTestSet;
import org.concordion.api.Target;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ProduceTestSetResults {
    public final static String PROJECT_ROOT = "automatedTest";
    private TestSetTestResult current = new TestSetTestResult(null, null, null, null);
    private Map<String, Map<String, TestSetTestResult>> folders = new HashMap<String, Map<String, TestSetTestResult>>();
    Target target;

    private static final Logger LOG = Logger.getLogger(ProduceTestSetResults.class.getName());
    private static VelocityUtilTestSet velocityUtilTestSet = new VelocityUtilTestSet();


    public int getSummaryLevel(String fullTestResultPath) {
        String path = fullTestResultPath;
        String sub = path.substring(path.indexOf("selenium\\" + PROJECT_ROOT) + 9);

        // Count the number of slashes to determine the level
        int slashes = sub.length() - sub.replace("\\", "").length();

        return slashes - 1;
    }


    public String getSummaryIndexFileTitle(String fullTestResultPath) {
        File testResultFile = new File(fullTestResultPath);
        String folderName = testResultFile.getParentFile().getName();
        String title = folderName.replaceAll("[A-Z]", " $0");
        title = folderName.substring(0, 1).toUpperCase() + folderName.substring(1);
        return title;
    }

    public String getSummaryIndexFile(String fullTestResultPath, int level) {
        File testResultFile = new File(fullTestResultPath);
        String folderName = testResultFile.getParentFile().getName();
        String indexName = null;
        if (level > 0) {
            indexName = folderName.substring(0, 1).toUpperCase() + folderName.substring(1) + ".html";
        } else {
            // It's the top level index file - pointed to via the Jenkins Concordion reports
            //indexName = "summaryindex.html";
            indexName = folderName.substring(0, 1).toUpperCase() + folderName.substring(1) + ".html";
        }
        String path = testResultFile.getParent() + "\\" + indexName;
        return path;
    }

    public void updateSummaryIndexFile(String fullTestResultPath, int level, TestSetTestResult result) {
        if (level >= 0) {
            String indexFile = getSummaryIndexFile(fullTestResultPath, level);
            File indexFileFile = new File(indexFile);
            String title = getSummaryIndexFileTitle(fullTestResultPath);
            String path = indexFile;

            Map<String, TestSetTestResult> map = folders.get(path);

            if (map == null) {
                map = new HashMap<String, TestSetTestResult>();
                folders.put(path, map);
            }

            TestSetTestResult oldResult = map.get(result.getLink());

            if (oldResult != null) {
                result = oldResult.combine(result);
            }

            map.put(result.getLink(), result);

            writeFile(new TestSetTestResultSet(indexFile, title, map, level));
            File testResultFile = new File(fullTestResultPath);

            updateSummaryIndexFile(testResultFile.getParent(), level - 1, new TestSetTestResult(result, testResultFile.getParentFile().getName(), indexFileFile.getParentFile().getName() + "/" + indexFileFile.getName()));
        }
    }

    private void writeFile(TestSetTestResultSet set) {
        String testSetTestResult = velocityUtilTestSet.renderVelocityTemplate("testresultsummary", set);
        String fileName = set.getIndexFile();
        try {
            // Assume default encoding.
            FileWriter fileWriter =
                    new FileWriter(fileName);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                    new BufferedWriter(fileWriter);

            // Note that write() does not automatically
            // append a newline character.
            bufferedWriter.write(testSetTestResult);

            // Always close files.
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println(
                    "Error writing to file '"
                            + fileName + "'"
            );
            // Or we could just do this:
            // ex.printStackTrace();
        }

    }
}