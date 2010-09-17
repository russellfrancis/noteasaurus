package com.metrosix.noteasaurus.security;

import java.util.Collection;

/**
 * @author Russell Francis (russ@metro-six.com)
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
