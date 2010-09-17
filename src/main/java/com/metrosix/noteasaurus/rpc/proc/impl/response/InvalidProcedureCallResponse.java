package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import org.json.JSONException;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class InvalidProcedureCallResponse extends FailedProcedureCallResponse {
    public InvalidProcedureCallResponse(JSONException e) {
        super();
        setStatusCode(ProcedureCallStatusCode.INVALID_PROCEDURE_REQUEST);
        setDetailedMessage("Unable to understand your procedure call request: " + e.toString());
    }
}
