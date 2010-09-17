package com.metrosix.noteasaurus.rpc.impl;

import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import com.metrosix.noteasaurus.rpc.RequestExecutor;
import com.metrosix.noteasaurus.security.SecurityPrincipal;

/**
 * This is a default implementation of the RequestExecutor, which executes requests and returns responses.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DefaultRequestExecutor.java 247 2010-08-07 23:15:10Z adam $
 */
public class DefaultRequestExecutor implements RequestExecutor {

    /**
     * This method will execute the provided ProcedureCallRequest as the given principal returning a response.
     *
     * @param principal The person who is executing the request.
     * @param request The request that we wish to execute.
     * @return A response from the executed procedure call request.
     */
    public ProcedureCallResponse execute(SecurityPrincipal principal, ProcedureCallRequest request) {
        return request.executeAs(principal);
    }
}
