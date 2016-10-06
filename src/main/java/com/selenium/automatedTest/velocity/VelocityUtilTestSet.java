package com.selenium.automatedTest.velocity;

import com.selenium.automatedTest.extensions.TestSetTestResultSet;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * This is the Velocity util class that works with TestSetTestResultSet class and produce the test result files using velocity
 * templates.
 *
 * @author U0154946
 */
public class VelocityUtilTestSet {

    private static VelocityEngine VelocityEngine = null;

    static {
        try {
            VelocityEngine = new VelocityEngine();
            VelocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            VelocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            VelocityEngine.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String renderVelocityTemplate(final String templateName, final TestSetTestResultSet TestSetTestResultSet) {
        StringWriter writer = new StringWriter();
        try {
            final String templatePath = "templates/" + templateName + ".vm";
            InputStream input = this.getClass().getClassLoader().getResourceAsStream(templatePath);
            if (input == null) {
                throw new IOException("Template file doesn't exist");
            }

            VelocityContext context = new VelocityContext();
            context.put("testSetTestResultSet", TestSetTestResultSet);

            Template template = VelocityEngine.getTemplate(templatePath, "UTF-8");

            template.merge(context, writer);
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer.toString();
    }
}