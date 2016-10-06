package com.selenium.automatedTest.engine;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;

/**
 * Created with IntelliJ IDEA.
 * User: barnest
 * Date: 11/09/16
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
@RunWith(ConcordionRunner.class)
public class CalledTest extends StepBase {
    public CalledTest() {
        setTestType("CALLEDTEST");
    }

}
