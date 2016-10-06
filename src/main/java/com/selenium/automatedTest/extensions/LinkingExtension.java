package com.selenium.automatedTest.extensions;

import com.selenium.automatedTest.engine.Test;
import org.concordion.api.Element;
import org.concordion.api.Target;
import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;
import org.concordion.api.listener.*;

public class LinkingExtension implements ConcordionExtension {

    Target target;


    public void addTo(ConcordionExtender concordionExtender) {
        //concordionExtender.withResource("/com/selenium/automatedTest/style.css", new Resource("/com/selenium/automatedTest/style.css"));


        concordionExtender.withBuildListener(new ConcordionBuildListener() {
            public void concordionBuilt(ConcordionBuildEvent event) {
                target = event.getTarget();
            }
        });


        concordionExtender.withAssertEqualsListener(new AssertEqualsListener() {
            public void successReported(AssertSuccessEvent event) {
                addLinkToResult(event.getElement());

            }

            public void failureReported(AssertFailureEvent event) {
                addLinkToResult(event.getElement());

            }
        });
        concordionExtender.withAssertFalseListener(new AssertFalseListener() {
            public void successReported(AssertSuccessEvent event) {
                addLinkToResult(event.getElement());
            }

            public void failureReported(AssertFailureEvent event) {
                addLinkToResult(event.getElement());
            }
        });


        concordionExtender.withAssertTrueListener(new AssertTrueListener() {
            public void successReported(AssertSuccessEvent event) {
                addLinkToResult(event.getElement());
            }

            public void failureReported(AssertFailureEvent event) {
                addLinkToResult(event.getElement());
            }
        });

        concordionExtender.withThrowableListener(new ThrowableCaughtListener() {
            public void throwableCaught(ThrowableCaughtEvent event) {
                addLinkToResult(event.getElement());
            }
        });
    }

    public static void addLinkToResult(Element e) {

        if (!Test.getCalledTestLinkPath().equals("")) {
            Element currentElement = e;
            Element a = new Element("a");
            a.appendText(" Results");
            a.addAttribute("href", Test.getCalledTestLinkPath());
            a.addAttribute("target", "_blank");
            Test.setCalledTestLinkPath("");
            currentElement.appendChild(a);
        }
    }

}