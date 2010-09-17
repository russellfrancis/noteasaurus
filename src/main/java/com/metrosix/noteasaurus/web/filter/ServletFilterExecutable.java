package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.hibernate.Executable;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * This provides a class which implements the Executable interface and allows for calling a chained ServletFilter via
 * the Executable mechanism.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class ServletFilterExecutable implements Executable {

    private ServletRequest request;
    private ServletResponse response;
    private FilterChain chain;

    /**
     * Construct a new Executable which will invoke the
     * {@link FilterChain#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse)} when executed.
     *
     * @param request The ServletRequest instance to invoke the doFilter method with.
     * @param response The ServleResponse instance to invoke the doFilter method with.
     * @param chain The FilterChain to invoke doFilter on.
     */
    public ServletFilterExecutable(ServletRequest request, ServletResponse response, FilterChain chain) {
        setRequest(request);
        setResponse(response);
        setChain(chain);
    }

    /**
     * Execute the configured FilterChain.
     */
    @Override
    public void execute() throws Exception {
        getChain().doFilter(getRequest(), getResponse());
    }

    /**
     * Get the ServletRequest for this ServletFilterExecutable.
     *
     * @return The ServletRequest for this ServletFilterExecutable.
     */
    protected ServletRequest getRequest() {
        return request;
    }

    /**
     * Set the ServletRequest for this ServletFilterExecutable.
     *
     * @param request The ServletRequest for this ServletFilterExecutable.
     */
    protected void setRequest(ServletRequest request) {
        this.request = request;
    }

    /**
     * Get the ServletResponse for this ServletFilterExecutable.
     *
     * @return The ServletResponse for this ServletFilterExecutable.
     */
    protected ServletResponse getResponse() {
        return response;
    }

    /**
     * Set the ServletResponse for this ServletFilterExecutable.
     *
     * @param response The ServletResponse for this Executable.
     */
    protected void setResponse(ServletResponse response) {
        this.response = response;
    }

    /**
     * Get the FilterChain for this executable.
     * @return
     */
    protected FilterChain getChain() {
        return chain;
    }

    /**
     * Set the FilterChain for this Executable.
     *
     * @param chain The FilterChain for this Executable.
     */
    protected void setChain(FilterChain chain) {
        this.chain = chain;
    }
}
