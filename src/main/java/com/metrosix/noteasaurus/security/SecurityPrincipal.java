package com.metrosix.noteasaurus.security;

import java.util.Collection;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: SecurityPrincipal.java 247 2010-08-07 23:15:10Z adam $
 */
public interface SecurityPrincipal {
    public String getName();
    public boolean canRead(Class<? extends SecuredResource> resourceType);
    public boolean canWrite(Class<? extends SecuredResource> resourceType);
    public boolean canRead(SecuredResource resource);
    public boolean canRead(Collection<? extends SecuredResource> resources);
    public boolean canWrite(SecuredResource resource);
    public boolean canWrite(Collection<? extends SecuredResource> resources);
}
