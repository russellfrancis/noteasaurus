package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.util.PicoContainerFactory;
import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import org.picocontainer.PicoContainer;

/**
 * This is an abstract filter implementation which allows derived classes to
 * not implement the init and destroy methods of the filter interface.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: AbstractFilter.java 247 2010-08-07 23:15:10Z adam $
 */
abstract public class AbstractFilter implements Filter {

    private PicoContainer pico;

    /**
     * Invoked once upon filter initialization.
     * @param filterConfig The {@link FilterConfig} for this filter instance.
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        setPicoContainer(PicoContainerFactory.getPicoContainer());
    }

    /**
     * This method is invoked once upon filter shutdown to release any resources
     * held by the filter.
     */
    public void destroy()
    {
    }

    /**
     * Get a reference to this instances PicoContainer.
     *
     * @return A reference to this filters PicoContainer.
     */
    protected PicoContainer getPicoContainer() {
        return pico;
    }

    /**
     * Set this instances pico container.
     *
     * @param pico Set this instances pico container.
     */
    protected void setPicoContainer(PicoContainer pico) {
        this.pico = pico;
    }
}
