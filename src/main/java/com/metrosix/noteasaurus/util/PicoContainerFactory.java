package com.metrosix.noteasaurus.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: PicoContainerFactory.java 247 2010-08-07 23:15:10Z adam $
 */
public class PicoContainerFactory {
    public static MutablePicoContainer getPicoContainer() {
        try {
            InitialContext initialContext = InitialContextFactory.getInitialContext();
            return (MutablePicoContainer)initialContext.lookup(PicoContainer.class.getName());
        }
        catch (NamingException e) {
            throw new RuntimeException(e.toString(), e);
        }
    }
}
