package com.metrosix.noteasaurus.rpc.proc;

import com.metrosix.noteasaurus.rpc.proc.impl.exception.ArgumentException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.security.SecuredExecutable;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import java.util.Map;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public interface Procedure extends SecuredExecutable {
    public void assignArguments(Map<String,Object> arguments) throws ArgumentException;
    public Object executeAs(SecurityPrincipal principal) throws ProcedureException;
}
