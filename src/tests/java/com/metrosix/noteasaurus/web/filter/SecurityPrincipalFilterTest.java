package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.web.filter.SecurityPrincipalFilter;
import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Test;
import org.picocontainer.PicoContainer;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: SecurityPrincipalFilterTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class SecurityPrincipalFilterTest {

    @Test
    public void testDoFilter() throws Exception {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        SecurityPrincipalService sps = createStrictMock(SecurityPrincipalService.class);

        SecurityPrincipalFilter filter = new SecurityPrincipalFilter();
        filter.setPicoContainer(pico);

        expect(pico.getComponent(SecurityPrincipalService.class)).andReturn(sps);
        chain.doFilter(request, response);
        sps.unbind();

        replay(request, response, chain, pico, sps);
        filter.doFilter(request, response, chain);
        verify(request, response, chain, pico, sps);
    }

    @Test
    public void testDoFilter_WithException() throws Exception {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        SecurityPrincipalService sps = createStrictMock(SecurityPrincipalService.class);

        SecurityPrincipalFilter filter = new SecurityPrincipalFilter();
        filter.setPicoContainer(pico);

        expect(pico.getComponent(SecurityPrincipalService.class)).andReturn(sps);
        chain.doFilter(request, response);
        expectLastCall().andThrow(new RuntimeException("Yikes!"));
        sps.unbind();

        replay(request, response, chain, pico, sps);
        try {
            filter.doFilter(request, response, chain);
            fail();
        } catch (RuntimeException e) {
            // success.
        }
        verify(request, response, chain, pico, sps);
    }
}