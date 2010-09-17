package com.metrosix.noteasaurus.rpc.impl;

import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import com.metrosix.noteasaurus.rpc.RequestExecutor;
import com.metrosix.noteasaurus.security.SecurityPrincipal;

/**
 * This is a default implementation of the RequestExecutor, which executes requests and returns responses.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultRequestExecutor implements RequestExecutor {

    /**
     * This method will execute the provided ProcedureCallRequest as the given principal returning a response.
     *
     * @param principal The person who is executing the request.
     * @param request The request that we wish to execute.
     * @return A response from the executed procedure call request.
     */
    @Override
    public ProcedureCallResponse execute(SecurityPrincipal principal, ProcedureCallRequest request) {
        return request.executeAs(principal);
    }
}
