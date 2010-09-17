package com.metrosix.noteasaurus.rpc.proc.impl.exception;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class ProcedureException extends Exception {
    public ProcedureException() {
        super();
    }

    public ProcedureException(Throwable e) {
        super(e);
    }

    public ProcedureException(String message) {
            super(message);
    }

    public ProcedureException(String message, Throwable e) {
        super(message, e);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
