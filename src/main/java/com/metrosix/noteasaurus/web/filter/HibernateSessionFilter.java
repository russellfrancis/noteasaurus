package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.hibernate.Executable;
import com.metrosix.noteasaurus.hibernate.HibernateSessionWrapper;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This class implements a ServletFilter which takes care of setting up and making a hibernate Session available
 * to the application beyond the filter.  It will commit any pending work when leaving the filter and handle any
 * cases where the transaction needs to be rolled back or cleaned up due to failure.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class HibernateSessionFilter extends AbstractFilter {

    /**
     * Provide a Hibernate Session which is ready for use and handle commiting any pending work as well
     * as cleaning up after any failures.
     *
     * @param request The ServletRequest for this request.
     * @param response The ServletResponse for this request.
     * @param chain The filter chain.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException
    {
        HibernateSessionWrapper wrapper = getPicoContainer().getComponent(HibernateSessionWrapper.class);
        Executable delegate = newServletFilterExecutable(request, response, chain);

        try {
            wrapper.execute(delegate);
        } catch (RuntimeException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } catch (ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Construct a new ServletFilterExecutable.
     *
     * @param request The request instance.
     * @param response The response instance.
     * @param chain The filter chain.
     * @return A new Executable.
     */
    protected Executable newServletFilterExecutable(
            ServletRequest request, ServletResponse response, FilterChain chain)
    {
        return new ServletFilterExecutable(request, response, chain);
    }
}
