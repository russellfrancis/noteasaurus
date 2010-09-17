package com.metrosix.noteasaurus.rpc;

import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import com.metrosix.noteasaurus.security.SecurityPrincipal;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public interface RequestExecutor {
    public ProcedureCallResponse execute(SecurityPrincipal requester, ProcedureCallRequest request);
}
