package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.rpc.proc.AssertSecurity;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import com.metrosix.noteasaurus.web.HttpSessionService;
import javax.servlet.http.HttpSession;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
@AssertSecurity(canRead={},canWrite={})
public class LogoutProcedure extends AbstractProcedure {

    private HttpSessionService httpSessionService;
    private SecurityPrincipalService securityPrincipalService;

    public LogoutProcedure(
            PersistenceManager persistenceManager,
            HttpSessionService httpSessionService,
            SecurityPrincipalService securityPrincipalService)
    {
        super(persistenceManager);
        setHttpSessionService(httpSessionService);
        setSecurityPrincipalService(securityPrincipalService);
    }

    @Override
    public Object executeAs(SecurityPrincipal principal) {
        Boolean result = false;
        
        HttpSession httpSession = getHttpSessionService().getHttpSession();
        if (httpSession.getAttribute("sessionPersonId") != null) {
            // Unbind from the http session.
            httpSession.removeAttribute("sessionPersonId");
            // unbind any existing credentials from the SecurityPrincipalService
            getSecurityPrincipalService().unbind();
            result = true;
        }

        return Boolean.valueOf(result);
    }

    public HttpSessionService getHttpSessionService() {
        return httpSessionService;
    }

    public void setHttpSessionService(HttpSessionService httpSessionService) {
        this.httpSessionService = httpSessionService;
    }

    public SecurityPrincipalService getSecurityPrincipalService() {
        return securityPrincipalService;
    }

    public void setSecurityPrincipalService(SecurityPrincipalService securityPrincipalService) {
        this.securityPrincipalService = securityPrincipalService;
    }
}
