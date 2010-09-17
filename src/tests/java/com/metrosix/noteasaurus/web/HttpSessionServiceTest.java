package com.metrosix.noteasaurus.web;

import javax.servlet.http.HttpSession;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class HttpSessionServiceTest {

    private final int THREAD_COUNT = 100;
    private final int ITERATION_COUNT = 1000;
    private HttpSessionService httpSessionService = new HttpSessionService();

    @Test
    public void testBindAndUnbind() throws Exception {
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    HttpSession session = createStrictMock(HttpSession.class);
                    replay(session);

                    for (int i = 0; i < ITERATION_COUNT; ++i) {
                        assertNull(httpSessionService.getHttpSession());
                        httpSessionService.bind(session);
                        Thread.yield();
                        assertEquals(session, httpSessionService.getHttpSession());
                        httpSessionService.unbind();
                        Thread.yield();
                    }
                    assertNull(httpSessionService.getHttpSession());

                    verify(session);
                }
            });
        }

        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i].start();
        }

        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i].join();
        }
    }
}