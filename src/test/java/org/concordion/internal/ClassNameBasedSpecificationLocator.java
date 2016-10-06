package org.concordion.internal;

import com.selenium.automatedTest.engine.Test;
import org.concordion.api.Resource;
import org.concordion.api.SpecificationLocator;


public class ClassNameBasedSpecificationLocator implements SpecificationLocator {

    public static String currentResource = null;       // declare the current resource     -- by radhika

    private String specificationSuffix;

    public ClassNameBasedSpecificationLocator() {
        this("html");
    }

    public ClassNameBasedSpecificationLocator(String specificationSuffix) {
        if (this.specificationSuffix != null) {
            this.specificationSuffix = specificationSuffix;
        }
    }

    public Resource locateSpecification(Object fixture) {
         /*  Check.notNull(fixture, "Fixture is null");

        String dottedClassName = fixture.getClass().getName();
        String slashedClassName = dottedClassName.replaceAll("\\.", "/");
        String specificationName = slashedClassName.replaceAll("(Fixture|Test)$", "");
        String resourcePath = "/" + specificationName + "." + specificationSuffix;

        return new Resource(resourcePath);*/
        //---------------Added by Kirsty----to enable calling other scripts
        if (!Test.getCalledTest().equals("")) {
            String calledTest = Test.getCalledTest();
            Test.setCalledTest("");
            return new Resource(calledTest);
        }
        //---------------End of code added by Kirsty-----to enable calling other scripts
        return new Resource(currentResource);           // call that current resource

    }
}
