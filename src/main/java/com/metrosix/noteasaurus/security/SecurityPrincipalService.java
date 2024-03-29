package com.metrosix.noteasaurus.security;

import com.metrosix.noteasaurus.database.PersistenceManager;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class SecurityPrincipalService {
    static private final ThreadLocal securityPrincipal = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return new AnonymousSecurityPrincipal();
        }
    };

    private PersistenceManager persistenceManager;

    public SecurityPrincipalService(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    public void bind(SecurityPrincipal principal) {
        securityPrincipal.set(principal);
    }

    public void unbind() {
        securityPrincipal.remove();
    }

    public SecurityPrincipal getSecurityPrincipal() {
        return (SecurityPrincipal)securityPrincipal.get();
    }

    protected PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    protected void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
