package com.metrosix.noteasaurus.rpc.proc.impl.exception;

import com.metrosix.noteasaurus.rpc.proc.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class ArgumentException extends ProcedureException {
    private Argument argument;

    public ArgumentException(Argument argument) {
        setArgument(argument);
    }

    public Argument getArgument() {
        return argument;
    }

    protected void setArgument(Argument argument) {
        this.argument = argument;
    }
}
