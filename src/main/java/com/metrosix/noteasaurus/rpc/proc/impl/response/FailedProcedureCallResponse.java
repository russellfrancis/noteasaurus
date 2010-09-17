package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatus;
import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import org.json.JSONString;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: FailedProcedureCallResponse.java 247 2010-08-07 23:15:10Z adam $
 */
public class FailedProcedureCallResponse extends ProcedureCallResponse {
    public FailedProcedureCallResponse() {
        super();
        setStatus(ProcedureCallStatus.FAILURE);
        setResult(null);
    }

    public FailedProcedureCallResponse(String message) {
        super();
        setStatus(ProcedureCallStatus.FAILURE);
        setStatusCode(ProcedureCallStatusCode.GENERAL_FAILURE);
        setDetailedMessage(message);
        setResult(null);
    }

    public FailedProcedureCallResponse(ProcedureException ex) {
        this(ex.toString());
        if (ex instanceof JSONString) {
            setResult(ex);
        }
    }
}
