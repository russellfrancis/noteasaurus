package com.metrosix.com.noteasaurus.selenium;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: SeleneseTestBase.java 252 2010-08-08 00:38:46Z adam $
 */
public class SeleneseTestBase {
    static private final Logger log = LoggerFactory.getLogger(SeleneseTestBase.class);
    static protected Selenium selenium;

    public SeleneseTestBase() {
    }

    @BeforeClass
    static public void setupSelenium() {
        SeleneseTestBase.selenium = new DefaultSelenium(
                getSeleniumServerHost(), getSeleniumServerPort(), getBrowserCommand(), getBrowserURL());
        SeleneseTestBase.selenium.start();
    }

    @AfterClass
    static public void shutdownSelenium() {
        if (SeleneseTestBase.selenium != null) {
            SeleneseTestBase.selenium.stop();
            SeleneseTestBase.selenium = null;
        }
    }

    static protected Selenium getSelenium() {
        return SeleneseTestBase.selenium;
    }

    static protected void setSelenium(Selenium selenium) {
        SeleneseTestBase.selenium = selenium;
    }

    static protected String getSeleniumServerHost() {
        return System.getProperty("selenium.server.host", "127.0.0.1");
    }

    static protected int getSeleniumServerPort() {
        int port = 4444;
        String portString = System.getProperty("selenium.server.port");
        if (portString != null) {
            try {
                port = Integer.parseInt(portString);
            }
            catch (NumberFormatException e) {
                if (log.isErrorEnabled()) {
                    log.error("Invalid selenium server port specified '" + portString + "' is not a number.");
                }
            }
        }
        return port;
    }

    static protected String getBrowserCommand() {
        return System.getProperty("selenium.browser.command", "*firefox");
    }

    static protected String getBrowserURL() {
        return System.getProperty("selenium.browser.url", "http://127.0.0.1:8080/");
    }

    protected void logout() {
        selenium.deleteCookie("JSESSIONID", "/");
    }

    protected void waitForElement(String locator) throws Exception {
        waitForElement(locator, 30000);
    }

    protected void waitForElement(String locator, int timeout) throws Exception {
        int timeSpent = 0;
        int interval = 250;

        while (timeSpent < timeout && !selenium.isElementPresent(locator)) {
            Thread.sleep(interval);
            timeSpent += interval;
        }
    }

    protected void waitForElementToDisappear(String locator) throws Exception {
        waitForElementToDisappear(locator, 30000);
    }

    protected void waitForElementToDisappear(String locator, int timeout) throws Exception {
        int timeSpent = 0;
        int interval = 250;

        while (timeSpent < timeout && selenium.isElementPresent(locator)) {
            Thread.sleep(interval);
            timeSpent += interval;
        }
    }
}
