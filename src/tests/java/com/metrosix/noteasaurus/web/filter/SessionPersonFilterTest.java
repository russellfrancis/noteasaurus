package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.web.filter.SessionPersonFilter;
import com.metrosix.noteasaurus.web.filter.AbstractFilter;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Session;
import org.junit.Test;
import org.picocontainer.PicoContainer;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis
 * @version $Id: SessionPersonFilterTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class SessionPersonFilterTest {

    @Test
    public void testDoFilter_WrongType0_CallChainOnly() throws Exception {
        ServletRequest req = createStrictMock(ServletRequest.class);
        ServletResponse resp = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        SessionPersonFilter filter = new SessionPersonFilter();

        chain.doFilter(req, resp);

        replay(req, resp, chain);
        filter.doFilter(req, resp, chain);
        verify(req, resp, chain);
    }

    @Test
    public void testDoFilter_WrongType1_CallChainOnly() throws Exception {
        HttpServletRequest req = createStrictMock(HttpServletRequest.class);
        ServletResponse resp = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        SessionPersonFilter filter = new SessionPersonFilter();

        chain.doFilter(req, resp);

        replay(req, resp, chain);
        filter.doFilter(req, resp, chain);
        verify(req, resp, chain);
    }

    @Test
    public void testDoFilter_WrongType2_CallChainOnly() throws Exception {
        ServletRequest req = createStrictMock(ServletRequest.class);
        HttpServletResponse resp = createStrictMock(HttpServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        SessionPersonFilter filter = new SessionPersonFilter();

        chain.doFilter(req, resp);

        replay(req, resp, chain);
        filter.doFilter(req, resp, chain);
        verify(req, resp, chain);
    }

    @Test
    public void testDoFilter_NoSecurityPrincipal() throws Exception {
        HttpSession session = createStrictMock(HttpSession.class);
        HttpServletRequest req = createStrictMock(HttpServletRequest.class);
        HttpServletResponse resp = createStrictMock(HttpServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        SessionPersonFilter filter = new SessionPersonFilter();

        expect(req.getSession()).andReturn(session);
        expect(session.getAttribute("sessionPersonId")).andReturn(null);
        chain.doFilter(req, resp);

        replay(req, resp, chain, session);
        filter.doFilter(req, resp, chain);
        verify(req, resp, chain, session);
    }

    @Test
    public void testDoFilter_SecurityPrincipal() throws Exception {
        PicoContainer pico = createStrictMock(PicoContainer.class);
        PersistenceManager persistenceManager = createStrictMock(PersistenceManager.class);
        SecurityPrincipalService sps = createStrictMock(SecurityPrincipalService.class);
        Long sessionPersonId = Long.valueOf(1L);
        Person sessionPerson = createStrictMock(Person.class);
        HttpSession session = createStrictMock(HttpSession.class);
        Session hibernateSession = createStrictMock(Session.class);
        HttpServletRequest req = createStrictMock(HttpServletRequest.class);
        HttpServletResponse resp = createStrictMock(HttpServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        SessionPersonFilter filter = createStrictMock(SessionPersonFilter.class,
                AbstractFilter.class.getDeclaredMethod("getPicoContainer"));
        filter.setPersistenceManager(persistenceManager);

        expect(req.getSession()).andReturn(session);
        expect(session.getAttribute("sessionPersonId")).andReturn(sessionPersonId);
        expect(persistenceManager.getSession()).andReturn(hibernateSession);
        expect(hibernateSession.get(Person.class, sessionPersonId)).andReturn(sessionPerson);
        expect(filter.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(SecurityPrincipalService.class)).andReturn(sps);
        sps.bind(sessionPerson);
        chain.doFilter(req, resp);

        replay(req, resp, chain, session, sps, filter, pico, persistenceManager, hibernateSession, sessionPerson);
        filter.doFilter(req, resp, chain);
        verify(req, resp, chain, session, sps, filter, pico, persistenceManager, hibernateSession, sessionPerson);
    }
}