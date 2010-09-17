package com.metrosix.noteasaurus.util;

import javax.naming.InitialContext;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
class InitialContextFactory {
    static private final ThreadLocalInitialContext initialContext = new ThreadLocalInitialContext();

    static InitialContext getInitialContext() {
        return (InitialContext) initialContext.get();
    }
}
