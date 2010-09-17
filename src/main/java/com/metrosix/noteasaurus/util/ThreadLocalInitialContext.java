package com.metrosix.noteasaurus.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ThreadLocalInitialContext.java 247 2010-08-07 23:15:10Z adam $
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
