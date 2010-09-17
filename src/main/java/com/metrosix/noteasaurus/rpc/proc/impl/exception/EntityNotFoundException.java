package com.metrosix.noteasaurus.rpc.proc.impl.exception;

import java.io.Serializable;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: EntityNotFoundException.java 247 2010-08-07 23:15:10Z adam $
 */
public class EntityNotFoundException extends ProcedureException {
    public EntityNotFoundException(Class entityType, Serializable id) {
        super("Unable to locate required entity '" + entityType.getSimpleName() + "' with id = '" + id + "'.");
    }
}
