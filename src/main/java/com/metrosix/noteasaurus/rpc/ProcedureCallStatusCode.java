package com.metrosix.noteasaurus.rpc;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ProcedureCallStatusCode.java 247 2010-08-07 23:15:10Z adam $
 */
public enum ProcedureCallStatusCode {
    // ===== SUCCESSFUL STATUS CODES  [0 - 999] =====
    SUCCESS(0),

    // ===== BAD REQUEST STATUS CODES [2000 - 2999] =====
    PROCEDURE_NOT_FOUND(2000),
    PROCEDURE_EXECUTION_DENIED(2001),
    PROCEDURE_PARAMETER_INVALID(2002),
    SECURITY_DENIED(2003),
    ENTITY_NOT_FOUND(2004),
    GENERAL_FAILURE(2005),

    // ===== ERROR STATUS CODES       [4000 - 4999] =====
    INTERNAL_SERVER_ERROR(4000),
    INVALID_PROCEDURE_REQUEST(4001);

    private int code;

    private ProcedureCallStatusCode(int code) {
        setCode(code);
    }

    public int getCode() {
        return code;
    }

    private void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.name();
    }
}
