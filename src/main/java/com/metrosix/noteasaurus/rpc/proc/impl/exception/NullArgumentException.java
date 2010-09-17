package com.metrosix.noteasaurus.rpc.proc.impl.exception;

import com.metrosix.noteasaurus.rpc.proc.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: NullArgumentException.java 247 2010-08-07 23:15:10Z adam $
 */
public class NullArgumentException extends ArgumentException {
    public NullArgumentException(Argument parameter) {
        super(parameter);
    }

    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder();

        msg.append("The argument '");
        msg.append(getArgument() != null ? getArgument().name() : "?");
        msg.append("' was not provided but is declared as being required!");

        return msg.toString();
    }
}
