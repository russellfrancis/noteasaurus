package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.security.SecurityPrincipal;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: SecurityDeniedResponse.java 247 2010-08-07 23:15:10Z adam $
 */
public class SecurityDeniedResponse extends FailedProcedureCallResponse {
    public SecurityDeniedResponse(SecurityPrincipal principal, Object resource) {
        super();
        setStatusCode(ProcedureCallStatusCode.SECURITY_DENIED);
        setDetailedMessage("Security denied for '" + principal.getName() + "' to '" + resource + "'." );
    }
}
