package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class ProcedureNotFoundResponse extends FailedProcedureCallResponse {
    public ProcedureNotFoundResponse(String procedureName) {
        super();        
        setStatusCode(ProcedureCallStatusCode.PROCEDURE_NOT_FOUND);
        setDetailedMessage("Unable to find a procedure with the name '" + procedureName + "'.");        
    }
}
