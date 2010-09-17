package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.web.HttpSessionService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * This class provides a request filter which ensures that the user has an
 * http session established before we forward the request on to the servlet
 * that is expected to handle it.  Some portions of the application may expect
 * that the session has been initialized.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class HttpSessionFilter extends AbstractFilter {
    /**
     *
     * @param req The {@link ServletRequest} for this web request.
     * @param res The {@link ServletResponse} used to respond to this web request.
     * @param chain The filter chain that we are executing within.
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpSession httpSession = request.getSession(true);
        HttpSessionService httpSessionService = getPicoContainer().getComponent(HttpSessionService.class);
        httpSessionService.bind(httpSession);
        try {
            chain.doFilter(req,res);
        }
        finally {
            httpSessionService.unbind();
        }
    }
}
