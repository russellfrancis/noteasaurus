package com.metrosix.noteasaurus.rpc.proc.impl.exception;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class InvalidValuesException extends ProcedureException implements JSONString {
    
    private Map<String, Collection<String>> errors = new HashMap<String, Collection<String>>();
    
    public InvalidValuesException(String[] fields, String messages[]) {
        this( Arrays.asList(fields), Arrays.asList(messages) );
    }

    public InvalidValuesException(List<String> fields, List<String> messages) {
        super();

        if (fields.size() == 0 || fields.size() != messages.size()) {
            throw new IllegalStateException(
                "Unable to construct a new instance as there must be an equal and non-zero number of fields and " +
                "messages!");
        }

        for (int i = 0; i < fields.size(); ++i) {
            String field = fields.get(i);
            String message = messages.get(i);
            Collection<String> messageCollection = getErrors().get(field);
            if (messageCollection == null) {
                messageCollection = new LinkedHashSet<String>();
                getErrors().put(field, messageCollection);
            }

            messageCollection.add(message);
        }
    }

    @Override
    public String getMessage() {
        StringBuilder msg = new StringBuilder("Invalid values detected for fields: \n");
        for (Entry<String,Collection<String>> entry : getErrors().entrySet()) {
            String field = entry.getKey();
            Collection<String> messages = entry.getValue();
            msg.append("['").append(field).append("']:\n");
            for (String message : messages) {
                msg.append("\t-> ").append(message).append(";\n");
            }
        }
        return msg.toString();
    }

    protected Map<String, Collection<String>> getErrors() {
        return errors;
    }

    protected void setErrors(Map<String, Collection<String>> errors) {
        this.errors = errors;
    }

    public String toJSONString() {
        try {
            JSONObject o = new JSONObject();
            for (Entry<String,Collection<String>> entry : getErrors().entrySet()) {
                o.put(entry.getKey(), entry.getValue());
            }
            return o.toString();
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
