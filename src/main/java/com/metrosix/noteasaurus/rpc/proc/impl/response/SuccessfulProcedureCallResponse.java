package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.rpc.ProcedureCallStatus;
import com.metrosix.noteasaurus.rpc.proc.Procedure;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: SuccessfulProcedureCallResponse.java 247 2010-08-07 23:15:10Z adam $
 */
public class SuccessfulProcedureCallResponse extends ProcedureCallResponse {
    public SuccessfulProcedureCallResponse(Procedure procedure, Object result) {
        super();
        setStatus(ProcedureCallStatus.SUCCESS);
        setStatusCode(ProcedureCallStatusCode.SUCCESS);
        setDetailedMessage("Execution of procedure '" + procedure.getClass().getSimpleName() +
                "' completed successfully.");
        setResult(result);
    }
}
