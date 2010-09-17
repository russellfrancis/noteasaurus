package com.metrosix.noteasaurus.security;

/**
 * @author Russell Francis (russell.francis@gmail.com
 * @version $Id: SecuredExecutable.java 247 2010-08-07 23:15:10Z adam $
 */
public interface SecuredExecutable {
    public boolean canExecute(SecurityPrincipal principal);
}
