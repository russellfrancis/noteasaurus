package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.web.HttpSessionService;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.Test;
import org.picocontainer.PicoContainer;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class HttpSessionFilterTest {
    @Test
    public void testDoFilter() throws Exception {
        // create our mock classes.
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HttpServletRequest req = createStrictMock(HttpServletRequest.class);
        HttpServletResponse res = createStrictMock(HttpServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        HttpSession session = createStrictMock(HttpSession.class);
        HttpSessionService httpSessionService = createStrictMock(HttpSessionService.class);

        // create the class we are testing.
        HttpSessionFilter filter = new HttpSessionFilter();
        // assign the mock pico to it.
        filter.setPicoContainer(pico);

        // declare our expectations, what we expect our test to do.
        expect(req.getSession(true)).andReturn(session);
        expect(pico.getComponent(HttpSessionService.class)).andReturn(httpSessionService);
        httpSessionService.bind(session);
        chain.doFilter(req, res);
        httpSessionService.unbind();

        // replay our mock objects, mark that we are beginning the test.
        replay(req, res, chain, session, pico, httpSessionService);
        // run the test.
        filter.doFilter(req, res, chain);
        // verify that everything we expected to happen happened and nothing else.
        verify(req, res, chain, session, pico, httpSessionService);
    }

    @Test
    public void testDoFilter_WithException() throws Exception {
        // create our mock classes.
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HttpServletRequest req = createStrictMock(HttpServletRequest.class);
        HttpServletResponse res = createStrictMock(HttpServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        HttpSession session = createStrictMock(HttpSession.class);
        HttpSessionService httpSessionService = createStrictMock(HttpSessionService.class);

        // create the class we are testing.
        HttpSessionFilter filter = new HttpSessionFilter();
        // assign the mock pico to it.
        filter.setPicoContainer(pico);

        // declare our expectations, what we expect our test to do.
        expect(req.getSession(true)).andReturn(session);
        expect(pico.getComponent(HttpSessionService.class)).andReturn(httpSessionService);
        httpSessionService.bind(session);
        chain.doFilter(req, res);
        expectLastCall().andThrow(new RuntimeException("Yikes!"));
        httpSessionService.unbind();

        // replay our mock objects, mark that we are beginning the test.
        replay(req, res, chain, session, pico, httpSessionService);
        // run the test.
        try {
            filter.doFilter(req, res, chain);
            fail();
        } catch (RuntimeException e) {
            // success.
        }
        // verify that everything we expected to happen happened and nothing else.
        verify(req, res, chain, session, pico, httpSessionService);
    }

    @Test
    public void testDestroy() {
        // create the class we are testing.
        HttpSessionFilter filter = new HttpSessionFilter();
        filter.destroy();
    }
}