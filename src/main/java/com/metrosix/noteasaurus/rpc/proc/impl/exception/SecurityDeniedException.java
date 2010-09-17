package com.metrosix.noteasaurus.rpc.proc.impl.exception;

import com.metrosix.noteasaurus.security.SecuredResource;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import java.util.Collection;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class SecurityDeniedException extends ProcedureException {

    private SecurityPrincipal securityPrincipal;
    private SecuredResource resource;
    private Collection<? extends SecuredResource> resources;

    public SecurityDeniedException(SecurityPrincipal principal, SecuredResource resource) {
        super();
        setSecurityPrincipal(principal);
        setResource(resource);
    }

    public SecurityDeniedException(SecurityPrincipal principal, Collection<? extends SecuredResource> resources) {
        super();
        setSecurityPrincipal(principal);
        setResources(resources);
    }

    @Override
    public String getMessage() {
        StringBuilder s = new StringBuilder();
        s.append("Denying access for '");
        s.append(getSecurityPrincipal().getName());
        if (getResource() != null) {
            s.append("' to resource '");
            s.append(getResource());
        }
        else if (getResources() != null) {
            s.append("' to resources '");
            s.append(getResources());
        }
        s.append("'.");
        return s.toString();
    }

    public SecurityPrincipal getSecurityPrincipal() {
        return securityPrincipal;
    }

    public void setSecurityPrincipal(SecurityPrincipal securityPrincipal) {
        this.securityPrincipal = securityPrincipal;
    }

    public SecuredResource getResource() {
        return resource;
    }

    public void setResource(SecuredResource resource) {
        this.resource = resource;
    }

    public Collection<? extends SecuredResource> getResources() {
        return resources;
    }

    public void setResources(Collection<? extends SecuredResource> resources) {
        this.resources = resources;
    }
}
