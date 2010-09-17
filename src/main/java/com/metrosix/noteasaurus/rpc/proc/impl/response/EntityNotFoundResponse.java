package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class EntityNotFoundResponse extends FailedProcedureCallResponse {
    public EntityNotFoundResponse(String message) {
        super();
        setStatusCode(ProcedureCallStatusCode.ENTITY_NOT_FOUND);
        setDetailedMessage(message);
    }
}
