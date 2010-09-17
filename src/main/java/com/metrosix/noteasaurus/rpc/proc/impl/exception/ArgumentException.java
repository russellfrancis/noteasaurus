package com.metrosix.noteasaurus.rpc.proc.impl.exception;

import com.metrosix.noteasaurus.rpc.proc.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ArgumentException.java 247 2010-08-07 23:15:10Z adam $
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
