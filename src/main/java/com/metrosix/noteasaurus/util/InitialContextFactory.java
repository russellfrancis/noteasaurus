package com.metrosix.noteasaurus.util;

import javax.naming.InitialContext;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: InitialContextFactory.java 247 2010-08-07 23:15:10Z adam $
 */
class InitialContextFactory {
    static private final ThreadLocalInitialContext initialContext = new ThreadLocalInitialContext();

    static InitialContext getInitialContext() {
        return (InitialContext) initialContext.get();
    }
}
