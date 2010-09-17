package com.metrosix.com.noteasaurus.selenium;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: LoginTest.java 252 2010-08-08 00:38:46Z adam $
 */
public class LoginTest extends SeleneseTestBase {

    static private final String VERIFIED_USER_NAME = "verified_user@metro-six.com";
    static private final String UNVERIFIED_USER_NAME = "unverified_user@metro-six.com";
    static private final String DEFAULT_PASSWORD = "password";

    static private final String LOGIN_FORM_LOCATOR = "id=login";
    static private final String LOGIN_FORM_EMAIL_FIELD_LOCATOR = "id=email";
    static private final String LOGIN_FORM_PASSWORD_FIELD_LOCATOR = "id=password";
    static private final String LOGIN_FORM_SUBMIT_LOCATOR = "id=login-submit";
    static private final String LOGIN_FORM_ERROR_LOCATOR = "css=p.error";

    @Test
    public void testFailedLogin() throws Exception {
        logout();
        selenium.open("/");
        waitForElement(LOGIN_FORM_LOCATOR);

        assertTrue( !selenium.isElementPresent(LOGIN_FORM_ERROR_LOCATOR) ||
                    !selenium.isVisible(LOGIN_FORM_ERROR_LOCATOR));

        selenium.type(LOGIN_FORM_EMAIL_FIELD_LOCATOR, "unregister@gmail.com");
        selenium.type(LOGIN_FORM_PASSWORD_FIELD_LOCATOR, "password");
        selenium.click(LOGIN_FORM_SUBMIT_LOCATOR);

        waitForElement(LOGIN_FORM_ERROR_LOCATOR);
        assertTrue(selenium.isElementPresent(LOGIN_FORM_ERROR_LOCATOR));
    }

    @Test
    public void testUnverifiedLogin() throws Exception {
        logout();
        selenium.open("/");
        waitForElement(LOGIN_FORM_LOCATOR);

        assertTrue( !selenium.isElementPresent(LOGIN_FORM_ERROR_LOCATOR) ||
                    !selenium.isVisible(LOGIN_FORM_ERROR_LOCATOR));

        selenium.type(LOGIN_FORM_EMAIL_FIELD_LOCATOR, UNVERIFIED_USER_NAME);
        selenium.type(LOGIN_FORM_PASSWORD_FIELD_LOCATOR, DEFAULT_PASSWORD);
        selenium.click(LOGIN_FORM_SUBMIT_LOCATOR);

        waitForElement(LOGIN_FORM_ERROR_LOCATOR);
        assertTrue(selenium.isElementPresent(LOGIN_FORM_ERROR_LOCATOR));

        assertFalse(selenium.isElementPresent("id=controlbar"));
        assertFalse(selenium.isElementPresent("id=corkboard-panel"));
        assertFalse(selenium.isElementPresent("id=corkboard-area"));
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        logout();
        selenium.open("/");
        waitForElement(LOGIN_FORM_LOCATOR);

        selenium.type(LOGIN_FORM_EMAIL_FIELD_LOCATOR, VERIFIED_USER_NAME);
        selenium.type(LOGIN_FORM_PASSWORD_FIELD_LOCATOR, DEFAULT_PASSWORD);
        selenium.click(LOGIN_FORM_SUBMIT_LOCATOR);

        waitForElement("id=controlbar");
        assertTrue(selenium.isElementPresent("id=controlbar"));

        waitForElement("id=corkboard-panel");
        assertTrue(selenium.isElementPresent("id=corkboard-panel"));

        waitForElement("id=corkboard-area");
        assertTrue(selenium.isElementPresent("id=corkboard-area"));
    }
}
