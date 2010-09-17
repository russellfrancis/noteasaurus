package com.metrosix.noteasaurus.rpc;

import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import com.metrosix.noteasaurus.security.SecurityPrincipal;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: RequestExecutor.java 247 2010-08-07 23:15:10Z adam $
 */
public interface RequestExecutor {
    public ProcedureCallResponse execute(SecurityPrincipal requester, ProcedureCallRequest request);
}
