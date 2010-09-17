package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.rpc.proc.*;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import com.metrosix.noteasaurus.web.HttpSessionService;
import javax.servlet.http.HttpSession;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
@AssertSecurity(canRead={Person.class},canWrite={})
public class LoginProcedure extends AbstractProcedure {    
    private String email;
    private String password;

    private HttpSessionService httpSessionService;
    private SecurityPrincipalService securityPrincipalService;

    public LoginProcedure(
            PersistenceManager persistenceManager,
            HttpSessionService httpSessionService,
            SecurityPrincipalService securityPrincipalService)
    {
        super(persistenceManager);
        setHttpSessionService(httpSessionService);
        setSecurityPrincipalService(securityPrincipalService);
    }

    @Override
    public Object executeAs(SecurityPrincipal principal) 
    throws ProcedureException
    {
        Session session = getPersistenceManager().getSession();
        Query q = session.createQuery(
                "from Person " +
                "where " +
                    "email = :email " +
                    "and " +
                    "verified = :verified");
        q.setParameter("email", getEmail());
        q.setParameter("verified", Boolean.TRUE);
        Person person = (Person) q.uniqueResult();
        
        if (person != null) {
            if (getPassword() != null &&  getPassword().equalsIgnoreCase(person.getPassword())) {
                login(person);
            } else {
                person = null;
            }
        }

        return person;
    }

    public void login(Person person) {
        if (person == null) {
            throw new IllegalArgumentException("The parameter person must be non-null.");
        }

        if (person.getId() == 0) {
            throw new IllegalArgumentException("The parameter person must have a non-default id.");
        }
        
        // Associate the person with the current http session.
        HttpSession httpSession = getHttpSessionService().getHttpSession();
        httpSession.setAttribute("sessionPersonId", person.getId());

        // Bind to the SecurityPrincipalService
        getSecurityPrincipalService().bind(person);
    }

    @Argument(name="email",required=true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Argument(name="password", required=true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.toUpperCase();
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
