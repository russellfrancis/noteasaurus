package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.rpc.proc.Procedure;
import com.metrosix.noteasaurus.security.SecurityPrincipal;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ProcedureExecutionDeniedResponse.java 247 2010-08-07 23:15:10Z adam $
 */
public class ProcedureExecutionDeniedResponse extends FailedProcedureCallResponse {
    public ProcedureExecutionDeniedResponse(SecurityPrincipal principal, Procedure procedure) {
        super();
        setStatusCode(ProcedureCallStatusCode.PROCEDURE_EXECUTION_DENIED);
        setDetailedMessage("Execution of procedure '" + procedure.getClass().getSimpleName() + 
                "' was denied for '" + principal.getName() + "'.");
    }
}
