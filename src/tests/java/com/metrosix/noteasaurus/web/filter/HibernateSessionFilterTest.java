package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.web.filter.HibernateSessionFilter;
import com.metrosix.noteasaurus.web.filter.AbstractFilter;
import com.metrosix.noteasaurus.hibernate.Executable;
import com.metrosix.noteasaurus.hibernate.HibernateSessionWrapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Test;
import org.picocontainer.PicoContainer;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: HibernateSessionFilterTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class HibernateSessionFilterTest {

    @Test
    public void testNewServletFilterExecutable() {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        HibernateSessionFilter filter = new HibernateSessionFilter();

        replay(request, response, chain);
        assertNotNull(filter.newServletFilterExecutable(request, response, chain));
        verify(request, response, chain);
    }

    @Test
    public void testDoFilter() throws Exception {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        Executable delegate = createStrictMock(Executable.class);
        HibernateSessionFilter filter = createStrictMock(HibernateSessionFilter.class,
                AbstractFilter.class.getDeclaredMethod("getPicoContainer"),
                HibernateSessionFilter.class.getDeclaredMethod("newServletFilterExecutable",
                ServletRequest.class, ServletResponse.class, FilterChain.class));

        expect(filter.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(filter.newServletFilterExecutable(request, response, chain)).andReturn(delegate);
        wrapper.execute(delegate);

        replay(request, response, chain, pico, wrapper, delegate, filter);
        filter.doFilter(request, response, chain);
        verify(request, response, chain, pico, wrapper, delegate, filter);
    }

    @Test
    public void testDoFilter_WithRuntimeException() throws Exception {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        Executable delegate = createStrictMock(Executable.class);
        HibernateSessionFilter filter = createStrictMock(HibernateSessionFilter.class,
                AbstractFilter.class.getDeclaredMethod("getPicoContainer"),
                HibernateSessionFilter.class.getDeclaredMethod("newServletFilterExecutable",
                ServletRequest.class, ServletResponse.class, FilterChain.class));

        expect(filter.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(filter.newServletFilterExecutable(request, response, chain)).andReturn(delegate);
        wrapper.execute(delegate);
        expectLastCall().andThrow(new RuntimeException("Yikes!"));

        replay(request, response, chain, pico, wrapper, delegate, filter);
        try {
            filter.doFilter(request, response, chain);
            fail();
        } catch (RuntimeException e) {
            // success.
        }
        verify(request, response, chain, pico, wrapper, delegate, filter);
    }

    @Test
    public void testDoFilter_WithIOException() throws Exception {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        Executable delegate = createStrictMock(Executable.class);
        HibernateSessionFilter filter = createStrictMock(HibernateSessionFilter.class,
                AbstractFilter.class.getDeclaredMethod("getPicoContainer"),
                HibernateSessionFilter.class.getDeclaredMethod("newServletFilterExecutable",
                ServletRequest.class, ServletResponse.class, FilterChain.class));

        expect(filter.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(filter.newServletFilterExecutable(request, response, chain)).andReturn(delegate);
        wrapper.execute(delegate);
        expectLastCall().andThrow(new IOException("Yikes!"));

        replay(request, response, chain, pico, wrapper, delegate, filter);
        try {
            filter.doFilter(request, response, chain);
            fail();
        } catch (IOException e) {
            // success.
        }
        verify(request, response, chain, pico, wrapper, delegate, filter);
    }

    @Test
    public void testDoFilter_WithServletException() throws Exception {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        Executable delegate = createStrictMock(Executable.class);
        HibernateSessionFilter filter = createStrictMock(HibernateSessionFilter.class,
                AbstractFilter.class.getDeclaredMethod("getPicoContainer"),
                HibernateSessionFilter.class.getDeclaredMethod("newServletFilterExecutable",
                ServletRequest.class, ServletResponse.class, FilterChain.class));

        expect(filter.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(filter.newServletFilterExecutable(request, response, chain)).andReturn(delegate);
        wrapper.execute(delegate);
        expectLastCall().andThrow(new ServletException("Yikes!"));

        replay(request, response, chain, pico, wrapper, delegate, filter);
        try {
            filter.doFilter(request, response, chain);
            fail();
        } catch (ServletException e) {
            // success.
        }
        verify(request, response, chain, pico, wrapper, delegate, filter);
    }

    @Test
    public void testDoFilter_WithOtherException() throws Exception {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        Executable delegate = createStrictMock(Executable.class);
        HibernateSessionFilter filter = createStrictMock(HibernateSessionFilter.class,
                AbstractFilter.class.getDeclaredMethod("getPicoContainer"),
                HibernateSessionFilter.class.getDeclaredMethod("newServletFilterExecutable",
                ServletRequest.class, ServletResponse.class, FilterChain.class));

        expect(filter.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(filter.newServletFilterExecutable(request, response, chain)).andReturn(delegate);
        wrapper.execute(delegate);
        expectLastCall().andThrow(new Exception("Yikes!"));

        replay(request, response, chain, pico, wrapper, delegate, filter);
        try {
            filter.doFilter(request, response, chain);
            fail();
        } catch (ServletException e) {
            // success.
        }
        verify(request, response, chain, pico, wrapper, delegate, filter);
    }
}