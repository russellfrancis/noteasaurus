package com.metrosix.noteasaurus.rpc.impl;

import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultRequestExecutorTest {
    @Test
    public void testExecute() {
        SecurityPrincipal principal = createStrictMock(SecurityPrincipal.class);
        ProcedureCallRequest request = createStrictMock(ProcedureCallRequest.class);
        ProcedureCallResponse response = createStrictMock(ProcedureCallResponse.class);
        DefaultRequestExecutor executor = new DefaultRequestExecutor();

        expect(request.executeAs(principal)).andReturn(response);

        replay(response, request, principal);
        assertEquals(response, executor.execute(principal, request));
        verify(response, request, principal);
    }
}