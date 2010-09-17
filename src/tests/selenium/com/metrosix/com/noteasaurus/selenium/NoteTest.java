package com.metrosix.com.noteasaurus.selenium;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: NoteTest.java 252 2010-08-08 00:38:46Z adam $
 */
public class NoteTest extends SeleneseTestBase {
    LoginTest loginTest = new LoginTest();

    @Test
    public void testBUG_76() throws Exception {
        // Login
        loginTest.testSuccessfulLogin();

        // Create a note.
        selenium.click("id=cbar-new-note-btn");
        super.waitForElement("//div[contains(concat(' ',normalize-space(@class),' '),' note-content ')]");

        // Type something with the header in it.
        selenium.click("//div[contains(concat(' ',normalize-space(@class),' '),' note-content ')]");
        selenium.type("//div[contains(concat(' ',normalize-space(@class),' '),' note-content ')]/../textarea", "== Header 2 ==");
        selenium.click("//div[contains(concat(' ',normalize-space(@class),' '),' note-control-bar ')]");

        assertTrue(selenium.getText("//div[contains(concat(' ',normalize-space(@class),' '),' note-content ')]/h2").trim().equals("Header 2"));

        // Delete the note.
        selenium.click("//a[contains(concat(' ',normalize-space(@class),' '),' delete-note-btn ')]");
        super.waitForElementToDisappear("//div[contains(concat(' ',normalize-space(@class),' '),' note ')]");
        assertFalse( selenium.isElementPresent("//div[contains(concat(' ',normalize-space(@class),' '),' note ')]"));
    }
}
