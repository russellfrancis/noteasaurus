package com.metrosix.noteasaurus.security;

import com.metrosix.noteasaurus.domain.Person;
import java.util.Collection;

/**
 * This provides an AnonymousSecurityPrincipal which implements the most restrictive set of permissions for the system.
 * It is the default SecurityPrincipal under which requests are run if no other SecurityPrincipal is provided manually
 * or by logging into the system.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class AnonymousSecurityPrincipal implements SecurityPrincipal {

    /**
     * Get a descriptive name of this SecurityPrincipal.
     *
     * @return A descriptive name of this SecurityPrincipal.
     */
    public String getName() {
        return "Anonymous Security Principal";
    }

    /**
     * Determine whether this SecurityPrincipal is allowed to read any objects of the provided class.  If this returns
     * true, it doesn't mean that this principal can access all objects of this type, it just means that it is possible
     * for them to access objects of this type, further checks against the specific resource which they are accessing
     * are also required.
     *
     * @param resource The Class type which we wish to know whether the security principal is allowed access to.
     * @return true if this SecurityPrincipal is allowed access to objects of this type, false otherwise.
     */
    public boolean canRead(Class<? extends SecuredResource> resource) {
        if (resource != null) {
            if (Person.class.isAssignableFrom(resource)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determine whether this principal can access resources of this type.  This will always return <code>false</code>
     * for the AnonymousSecurityPrincipal.
     *
     * @param resource The resource to determine whether write access is allowed to objects of this type.
     * @return true if write is allowed to this principal for objects of this type.
     */
    public boolean canWrite(Class<? extends SecuredResource> resource) {
        return false;
    }

    /**
     * Determine whether this principal is allowed to read the specific resource.
     *
     * @param resource The resource to determine if this principal has read access to.
     * @return true if we can read the provided resource, false otherwise.  The AnonymousSecurityPrincipal is never
     * allowed read access to specific resources.
     */
    public boolean canRead(SecuredResource resource) {
        return false;
    }

    /**
     * Determine whether this principal is allowed to write to the specific resource.
     *
     * @param resource The resource to determine if this principal has write access to.
     * @return true if we can write to the provided resource, false otherwise.  The AnonymousSecurityPrincipal is never
     * allowd write acces to specific resources.
     */
    public boolean canWrite(SecuredResource resource) {
        return false;
    }

    /**
     * Determine whether the principal is allowed read access to all of the resources in the provided collection.
     *
     * @param resources A Collection of resources to determine if we have read access to all of them.
     * @return true if we have read access to all of the resources in the collection, false otherwise.  The
     * AnonymousSecurityPrincipal will always return false.
     */
    public boolean canRead(Collection<? extends SecuredResource> resources) {
        return false;
    }

    /**
     * Determine whether the principal is allowed write access to all of the resources in the provided collection.
     *
     * @param resources A Collection of resources to determine if we have write access to all of them.
     * @return true if we can write to all of the resources in the Collection, false otherwise.  This method always
     * returns false for the AnonymousSecurityPrincipal.
     */
    public boolean canWrite(Collection<? extends SecuredResource> resources) {
        return false;
    }
}
