package com.metrosix.noteasaurus.rpc.proc.impl.request;

import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureExecutionDeniedResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureNotFoundResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.response.SuccessfulProcedureCallResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.response.InvalidParameterResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ArgumentException;
import com.metrosix.noteasaurus.rpc.proc.Procedure;
import com.metrosix.noteasaurus.rpc.proc.ProcedureLookup;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.EntityNotFoundException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.SecurityDeniedException;
import com.metrosix.noteasaurus.rpc.proc.impl.response.EntityNotFoundResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.response.FailedProcedureCallResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.response.SecurityDeniedResponse;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class ProcedureCallRequest {
    private String procedureName;
    private Map<String,Object> parameterMap = new HashMap<String,Object>();
    private ProcedureLookup procedureLookup;

    public ProcedureCallRequest(ProcedureLookup procedureLookup) {
        setProcedureLookup(procedureLookup);
    }

    public ProcedureCallResponse executeAs(SecurityPrincipal principal) {
        ProcedureCallResponse response;

        Procedure procedure = getProcedureLookup().lookup(getProcedureName());
        if (procedure != null) {
            if (procedure.canExecute(principal)) {
                try {
                    procedure.assignArguments(getParameterMap());
                    Object result = procedure.executeAs(principal);
                    response = new SuccessfulProcedureCallResponse(procedure, result);
                }
                catch (ArgumentException e) {
                    // invalid parameters provided to method.
                    response = new InvalidParameterResponse(procedure, e.getArgument());
                }
                catch (EntityNotFoundException e) {
                    response = new EntityNotFoundResponse(e.getMessage());
                }
                catch (SecurityDeniedException e) {
                    response = new SecurityDeniedResponse(e.getSecurityPrincipal(), 
                            e.getResource() != null ? e.getResource() : e.getResources());
                }
                catch (ProcedureException e) {
                    response = new FailedProcedureCallResponse(e);
                }
            }
            else {
                response = new ProcedureExecutionDeniedResponse(principal, procedure);
            }
        }
        else {
            response = new ProcedureNotFoundResponse(getProcedureName());
        }

        return response;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public ProcedureLookup getProcedureLookup() {
        return procedureLookup;
    }

    public void setProcedureLookup(ProcedureLookup procedureLookup) {
        this.procedureLookup = procedureLookup;
    }
}
