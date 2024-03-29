package com.metrosix.noteasaurus.rpc;

import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import java.io.InputStream;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public interface RequestParser {
    public ProcedureCallRequest generate(InputStream requestStatement) throws Exception;
}
