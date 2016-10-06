package com.selenium.automatedTest.engine;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.Properties;

public class AppProperties {

    private static Properties properties = null;

    public static String testclasspath = "target/test-classes/";

    public static Properties getProperties() {
        if (properties == null) {
            try {
                properties = new Properties();
                String propertyFile = System.getProperty("properties", testclasspath + "TestConfig.properties");
                properties.load(new FileInputStream(propertyFile));

                for (String key : properties.stringPropertyNames()) {
                    String value = properties.getProperty(key);
                    //System.out.println(key + " => " + value);
                }

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return properties;
    }


    public static String get(String propertyKey) {
        if (propertyKey.equalsIgnoreCase("browser") ||
                propertyKey.equalsIgnoreCase("brand") ||
                propertyKey.equalsIgnoreCase("env")) {
            //Allow override in mvn command of browser, brand or env (so can do -Dbrowser=firefox to override what is in AppProperties if required etc. . )
            if (System.getProperty(propertyKey) != null) {
                return System.getProperty(propertyKey);
            }
        }

        return getProperties().getProperty(propertyKey);
    }


    public static String getPassword(String InputValue) {
        String Password = null;
        try {

            FileInputStream file = new FileInputStream(get("resourcesPath") + "Users.xlsx");

            XSSFWorkbook workbook = new XSSFWorkbook(file);

            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();


            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row.getCell(3).getStringCellValue().equalsIgnoreCase(InputValue)) {

                    if (get("env").toLowerCase().startsWith("d")) {
                        Password = row.getCell(5).getStringCellValue();
                        String messageText = "The dev password for " + InputValue + " is " + Password;
                        Test.reportEvent("INFO", messageText);

                    } else if (get("env").equalsIgnoreCase("prod")) {
                        //System.out.println("password for prod " + row.getCell(4));
                        Password = row.getCell(4).getStringCellValue();
                        String messageText = "The Prod password for " + InputValue + " is " + Password;
                        Test.reportEvent("INFO", messageText);
                    }
                }
            }
        } catch (Throwable err) {
            err.printStackTrace();
        }
        return Password;
    }

    public static String ReadExcel(String ExcelName, String InputValue, int ColumnNum) {
        String CellValue = null;
        try {
            FileInputStream file = new FileInputStream(get("resourcesPath") + ExcelName);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();
            for (int i = 0; i <= rowsCount; i++) {
                Row row = sheet.getRow(i);
                if (row.getCell(0).getStringCellValue().equalsIgnoreCase(InputValue)) {
                    CellValue = row.getCell(ColumnNum - 1).toString();
                    System.out.println(" Column " + ColumnNum + " Value" + row.getCell(ColumnNum - 1));
                }
            }
        } catch (Throwable err) {
            err.printStackTrace();
        }
        return CellValue;
    }


    // To convert the input Excel sheet to html and execute the testcase
    public static void ConvertToHtml(String ExcelName) {
        try {
            FileInputStream file = new FileInputStream(ExcelName);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getLastRowNum();
        } catch (Throwable err) {
            err.printStackTrace();
        }
    }
}
