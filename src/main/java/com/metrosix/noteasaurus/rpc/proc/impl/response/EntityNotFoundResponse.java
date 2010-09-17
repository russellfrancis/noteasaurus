package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: EntityNotFoundResponse.java 247 2010-08-07 23:15:10Z adam $
 */
public class EntityNotFoundResponse extends FailedProcedureCallResponse {
    public EntityNotFoundResponse(String message) {
        super();
        setStatusCode(ProcedureCallStatusCode.ENTITY_NOT_FOUND);
        setDetailedMessage(message);
    }
}
