package com.selenium.automatedTest.engine;

import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;

import java.util.HashMap;


/**
 * Created with IntelliJ IDEA.
 * User: barnest
 * Date: 07/10/16
 * Time: 19:10
 * To change this template use File | Settings | File Templates.
 */

@RunWith(ConcordionRunner.class)
public abstract class Runner {


    public abstract Result run(WebDriver d, HashMap<String, String> params) throws Exception;
    //TODO get rid of Webdriver d as param
}
