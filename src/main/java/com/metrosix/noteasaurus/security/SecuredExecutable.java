package com.metrosix.noteasaurus.security;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public interface SecuredExecutable {
    public boolean canExecute(SecurityPrincipal principal);
}
