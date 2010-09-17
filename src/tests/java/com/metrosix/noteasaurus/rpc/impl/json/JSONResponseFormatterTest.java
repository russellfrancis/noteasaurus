package com.metrosix.noteasaurus.rpc.impl.json;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatus;
import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import java.io.InputStream;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class JSONResponseFormatterTest {

    @Test
    public void testFormat() throws Exception {
        String detailedMessage = "Detailed Message";
        JSONResponseFormatter formatter = new JSONResponseFormatter();
        ProcedureCallResponse response = new ProcedureCallResponse();
        response.setDetailedMessage(detailedMessage);
        response.setResult(Integer.valueOf(42));
        response.setStatus(ProcedureCallStatus.SUCCESS);
        response.setStatusCode(ProcedureCallStatusCode.SUCCESS);

        InputStream ins = formatter.format(response);

        // Make a buffer which is clearly large enough for the response.
        byte[] b = new byte[8192 * 4];
        int bytesRead = ins.read(b);

        JSONObject o = new JSONObject(new String(b, 0, bytesRead, "UTF-8"));

        assertEquals(ProcedureCallStatus.SUCCESS.name(), o.getString("status"));
        assertEquals(ProcedureCallStatusCode.SUCCESS.getCode(), o.getInt("status_code"));
        assertEquals(ProcedureCallStatusCode.SUCCESS.getMessage(), o.getString("status_message"));
        assertEquals(detailedMessage, o.getString("detailed_message"));
        assertEquals(42, o.getInt("result"));
    }

    @Test
    public void testFormat_ThrowsException() throws Exception {
        JSONResponseFormatter formatter = new JSONResponseFormatter();
        try {
            formatter.format(null);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }
}