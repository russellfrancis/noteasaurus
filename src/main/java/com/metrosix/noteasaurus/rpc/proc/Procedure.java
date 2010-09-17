package com.metrosix.noteasaurus.rpc.proc;

import com.metrosix.noteasaurus.rpc.proc.impl.exception.ArgumentException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.security.SecuredExecutable;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import java.util.Map;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: Procedure.java 247 2010-08-07 23:15:10Z adam $
 */
public interface Procedure extends SecuredExecutable {
    public void assignArguments(Map<String,Object> arguments) throws ArgumentException;
    public Object executeAs(SecurityPrincipal principal) throws ProcedureException;
}
