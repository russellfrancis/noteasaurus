package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.rpc.impl.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ProcedureNotFoundResponse.java 247 2010-08-07 23:15:10Z adam $
 */
public class ProcedureNotFoundResponse extends FailedProcedureCallResponse {
    public ProcedureNotFoundResponse(String procedureName) {
        super();        
        setStatusCode(ProcedureCallStatusCode.PROCEDURE_NOT_FOUND);
        setDetailedMessage("Unable to find a procedure with the name '" + procedureName + "'.");        
    }
}
