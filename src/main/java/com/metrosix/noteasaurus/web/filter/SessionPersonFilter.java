package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Session;

/**
 * This ServletFilter is used to associate the Person/SecurityPrincipal who is making
 * the request with the SecurityPrincipalService.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class SessionPersonFilter extends AbstractFilter {

    private PersistenceManager persistenceManager;

    /**
     * Initialize the SessionPersonFilter.
     *
     * @param filterConfig The configuration instance for this filter.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        setPersistenceManager(getPicoContainer().getComponent(PersistenceManager.class));
    }

    /**
     * Associate the logged in person making the request with the SecurityPrincipalService.
     *
     * @param req The ServletRequest instance for this request.
     * @param resp The ServletResponse instance for this request.
     * @param chain The FilterChain which allows us to invoke the next filter in the chain.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
    throws IOException, ServletException
    {
        if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {
            HttpServletRequest request = (HttpServletRequest)req;
            HttpSession session = request.getSession();
            Long sessionPersonId = (Long) session.getAttribute("sessionPersonId");
            if (sessionPersonId != null) {
                Session hibernateSession = getPersistenceManager().getSession();
                Person sessionPerson = (Person) hibernateSession.get(Person.class, sessionPersonId);
                if (sessionPerson != null) {
                    SecurityPrincipalService securityPrincipalService =
                        getPicoContainer().getComponent(SecurityPrincipalService.class);
                    securityPrincipalService.bind(sessionPerson);
                } else {
                    session.removeAttribute("sessionPersonId");
                }
            }
        }
        chain.doFilter(req, resp);
    }

    protected PersistenceManager getPersistenceManager() {
        return this.persistenceManager;
    }

    protected void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
