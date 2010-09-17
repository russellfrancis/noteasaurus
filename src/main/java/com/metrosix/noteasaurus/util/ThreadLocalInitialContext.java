package com.metrosix.noteasaurus.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class ThreadLocalInitialContext extends ThreadLocal {
    @Override
    protected Object initialValue() {
        try {
            return new InitialContext();
        }
        catch (NamingException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }
}
