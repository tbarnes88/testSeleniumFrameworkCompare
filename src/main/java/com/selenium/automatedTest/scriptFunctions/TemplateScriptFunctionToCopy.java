package com.selenium.automatedTest.scriptFunctions;

import com.selenium.automatedTest.engine.Result;
import com.selenium.automatedTest.engine.Runner;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;

//import utilities.*;
//import static utilities.kirstyutilities.*;

/**
 * Created with IntelliJ IDEA.
 * User: barnest
 * Date: 17/06/16
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */

@RunWith(ConcordionRunner.class)
public class TemplateScriptFunctionToCopy extends Runner {

    public Result run(WebDriver d, HashMap<String, String> params) throws Exception {
        HashMap<String, String> actionParams = new HashMap<String, String>();

        Result res = new Result();
        Result endResult = new Result();
        endResult.setErrorMessage("");
        endResult.setOutcome("PASS");
        boolean bContinue = true;

        // Must return the endResult at the end
        return endResult;

    }
}
