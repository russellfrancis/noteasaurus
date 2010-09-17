package com.metrosix.noteasaurus.security;

import java.util.Collection;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: SystemSecurityPrincipal.java 247 2010-08-07 23:15:10Z adam $
 */
public class SystemSecurityPrincipal implements SecurityPrincipal {
    public String getName() {
        return "System Security Principal";
    }

    public boolean canRead(Class<? extends SecuredResource> resource) {
        return true;
    }

    public boolean canWrite(Class<? extends SecuredResource> resource) {
        return true;
    }

    public boolean canRead(SecuredResource resource) {
        return true;
    }

    public boolean canWrite(SecuredResource resource) {
        return true;
    }

    public boolean canRead(Collection<? extends SecuredResource> resources) {
        return true;
    }

    public boolean canWrite(Collection<? extends SecuredResource> resources) {
        return true;
    }
}
