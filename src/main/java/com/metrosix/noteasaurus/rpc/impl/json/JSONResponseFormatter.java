package com.metrosix.noteasaurus.rpc.impl.json;

import com.metrosix.noteasaurus.rpc.ResponseFormatter;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * This implements a ResponseFormatter class which will format a response into a JSON string.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class JSONResponseFormatter implements ResponseFormatter {

    /**
     * Format the provided ProcedureCallResponse into a JSON formatted string which can be read from the returned
     * InputStream.
     *
     * @param response The ProcedureCallResponse instance to format.
     * @return An InputStream which can be used to read the JSON formatted string which represents the provided
     * response instance.
     */
    @Override
    public InputStream format(ProcedureCallResponse response) {
        if (response == null) {
            throw new IllegalArgumentException("The parameter response must be non-null.");
        }
        return new ByteArrayInputStream(response.toJSONString().getBytes(Charset.forName("UTF-8")));
    }

    /**
     * Return the Content-Type which should be used to interpret this response.
     * 
     * @return A String representing the mimetype which should be used to interpret this response.
     */
    @Override
    public String getContentType() {
        return "application/json";
    }
}
