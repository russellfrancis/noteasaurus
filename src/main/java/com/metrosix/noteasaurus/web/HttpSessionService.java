package com.metrosix.noteasaurus.web;

import javax.servlet.http.HttpSession;

/**
 * This class implements a service which allows for the binding of the HttpSession for a given request to the currently
 * executing Thread and allowing that HttpSession instance to be shared among different parts of the system.  It does
 * require that a users request be satisfied in a single thread.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class HttpSessionService {
    static private final ThreadLocal httpSession = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return null;
        }
    };

    /**
     * Bind the provided HttpSession instance to the current thread.
     *
     * @param session The HttpSession instance we wish to bind to the current thread.
     */
    public void bind(HttpSession session) {
        httpSession.set(session);
    }

    /**
     * Unbind any currently bound HttpSession instance from the current thread.
     */
    public void unbind() {
        httpSession.remove();
    }

    /**
     * Get a reference to the HttpSession instance bound to this thread or null if no HttpSession has been bound to this
     * thread.
     *
     * @return A reference to the HttpSession instance bound to this thread or null if no HttpSession has been bound to
     * this thread.
     */
    public HttpSession getHttpSession() {
        return (HttpSession)httpSession.get();
    }
}
