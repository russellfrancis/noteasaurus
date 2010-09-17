package com.metrosix.noteasaurus.rpc.proc.impl.response;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import com.metrosix.noteasaurus.rpc.ProcedureCallStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class ProcedureCallResponse implements JSONString {
    private ProcedureCallStatus status;
    private ProcedureCallStatusCode statusCode;
    private String detailedMessage;
    private Object result;

    public ProcedureCallStatus getStatus() {
        return status;
    }

    public void setStatus(ProcedureCallStatus status) {
        this.status = status;
    }

    public ProcedureCallStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(ProcedureCallStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }

    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String toJSONString() {
        JSONObject o = new JSONObject();
        try {
            o.put("status", getStatus() == null ? JSONObject.NULL : getStatus().name());
            o.put("status_code", getStatusCode() == null ? JSONObject.NULL : getStatusCode().getCode());
            o.put("status_message", getStatusCode() == null ? JSONObject.NULL : getStatusCode().getMessage());
            o.put("detailed_message", getDetailedMessage() == null ? JSONObject.NULL : getDetailedMessage());
            o.put("result", getResult() == null ? JSONObject.NULL : getResult());
        }
        catch (JSONException e) {
            throw new RuntimeException("Unable to convert response into a JSON String: " + e.toString(), e);
        }
        return o.toString();
    }
}
