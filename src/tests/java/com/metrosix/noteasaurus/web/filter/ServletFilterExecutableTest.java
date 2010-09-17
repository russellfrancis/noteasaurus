package com.metrosix.noteasaurus.web.filter;

import com.metrosix.noteasaurus.web.filter.ServletFilterExecutable;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ServletFilterExecutableTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class ServletFilterExecutableTest {

    @Test
    public void testExecute() throws Exception {
        ServletRequest request = createStrictMock(ServletRequest.class);
        ServletResponse response = createStrictMock(ServletResponse.class);
        FilterChain chain = createStrictMock(FilterChain.class);
        ServletFilterExecutable executable = new ServletFilterExecutable(request, response, chain);

        chain.doFilter(request, response);

        replay(request, response, chain);
        executable.execute();
        verify(request, response, chain);
    }

}