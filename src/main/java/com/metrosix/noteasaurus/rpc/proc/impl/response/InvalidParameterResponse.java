package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.rpc.proc.Argument;
import com.metrosix.noteasaurus.rpc.proc.Procedure;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class InvalidParameterResponse extends FailedProcedureCallResponse {
    public InvalidParameterResponse(Procedure procedure, Argument parameter) {
        super();
        setStatusCode(ProcedureCallStatusCode.PROCEDURE_PARAMETER_INVALID);
        setDetailedMessage("The procedure '" + procedure.getClass().getSimpleName() + "' was invoked with an invalid " +
                "parameter, '" + parameter.name() + "' " +
                (parameter.required() ? "is required and was not provided or " : "") +
                "was of an invalid type.");
    }
}
