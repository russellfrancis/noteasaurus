package com.metrosix.noteasaurus.rpc.proc.impl.exception;

import com.metrosix.noteasaurus.rpc.proc.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class InvalidArgumentException extends ArgumentException {
    private Class type;
    private Object value;

    public InvalidArgumentException(Argument parameter, Class type, Object value) {
        super(parameter);
        setType(type);
        setValue(value);
    }

    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder();
        msg.append("The argument '");
        msg.append(getArgument() != null ? getArgument().name() : "?");
        msg.append("' of type '");
        msg.append(getType() != null ? getType().getCanonicalName() : "?");
        msg.append("' cannot be assigned from value of type '");
        msg.append(getValue() != null ? getValue().getClass().getCanonicalName() : "?");
        msg.append("' whose value is '");
        msg.append(getValue() != null ? getValue().toString() : "?");
        msg.append("'.");
        return msg.toString();
    }

    public Class getType() {
        return type;
    }

    protected void setType(Class type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    protected void setValue(Object value) {
        this.value = value;
    }
}
