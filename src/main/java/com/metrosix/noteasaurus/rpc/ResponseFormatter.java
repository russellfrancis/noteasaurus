package com.metrosix.noteasaurus.rpc;

import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import java.io.InputStream;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public interface ResponseFormatter {
    public InputStream format(ProcedureCallResponse response);
    public String getContentType();
}
