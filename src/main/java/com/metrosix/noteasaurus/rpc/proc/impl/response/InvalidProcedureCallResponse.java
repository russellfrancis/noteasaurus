package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.rpc.impl.*;
import org.json.JSONException;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: InvalidProcedureCallResponse.java 247 2010-08-07 23:15:10Z adam $
 */
public class InvalidProcedureCallResponse extends FailedProcedureCallResponse {
    public InvalidProcedureCallResponse(JSONException e) {
        super();
        setStatusCode(ProcedureCallStatusCode.INVALID_PROCEDURE_REQUEST);
        setDetailedMessage("Unable to understand your procedure call request: " + e.toString());
    }
}
