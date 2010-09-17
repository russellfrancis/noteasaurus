package com.metrosix.noteasaurus.rpc.proc.impl.exception;

import java.io.Serializable;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class EntityNotFoundException extends ProcedureException {
    public EntityNotFoundException(Class entityType, Serializable id) {
        super("Unable to locate required entity '" + entityType.getSimpleName() + "' with id = '" + id + "'.");
    }
}
