package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.security.SecurityPrincipal;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class SecurityDeniedResponse extends FailedProcedureCallResponse {
    public SecurityDeniedResponse(SecurityPrincipal principal, Object resource) {
        super();
        setStatusCode(ProcedureCallStatusCode.SECURITY_DENIED);
        setDetailedMessage("Security denied for '" + principal.getName() + "' to '" + resource + "'." );
    }
}
