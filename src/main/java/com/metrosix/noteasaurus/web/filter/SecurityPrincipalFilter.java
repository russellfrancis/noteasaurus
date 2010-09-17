package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This provides a ServletFilter which ensures that the SecurityPrincipalService is initialized before processing
 * a request.  It is also responsible for removing any SecurityPrincipal's which have been
 * bound to the SecurityPrincipalService through the life of the request.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: SecurityPrincipalFilter.java 247 2010-08-07 23:15:10Z adam $
 */
public class SecurityPrincipalFilter extends AbstractFilter {

    /**
     * Implement a filter which sets up the SecurityPrincipalService and ensures that no Principals are left
     * bound to the Service at the conclusion of the request.
     *
     * @param request The ServletRequest which invoked this action.
     * @param response The ServletResponse used to send a response to the client.
     * @param chain The FilterChain to execute next.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException
    {
        SecurityPrincipalService securityService = getPicoContainer().getComponent(SecurityPrincipalService.class);
        try {
            chain.doFilter(request, response);
        }
        finally {
            securityService.unbind();
        }
    }
}
