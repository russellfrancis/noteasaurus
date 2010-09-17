package com.metrosix.noteasaurus.rpc.impl.json;

import com.metrosix.noteasaurus.rpc.RequestParser;
import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.util.Pair;
import com.metrosix.noteasaurus.util.PicoContainerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class implements a RequestParser capable of reading JSON formatted requests and creating an associated
 * ProcedureCallRequest object from the input.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: JSONRequestParser.java 247 2010-08-07 23:15:10Z adam $
 */
public class JSONRequestParser implements RequestParser {

    /**
     * This method will read a String from the provided input stream and interpret it as a ProcedureCallRequest.
     *
     * @param requestStatement The input stream from which a JSON object representing the ProcedureCallRequest can be
     * read.
     * @return A ProcedureCallRequest instance created from the data in the provided input stream.
     */
    public ProcedureCallRequest generate(InputStream requestStatement) throws IOException, JSONException {
        if (requestStatement == null) {
            throw new IllegalArgumentException("The parameter requestStatement must be non-null.");
        }

        String requestString = getStringFromInputStream(requestStatement);
        JSONObject jsonProcedureCall = new JSONObject(requestString);

        String procedureName = jsonProcedureCall.getString("procedure");
        JSONObject jsonProperties = jsonProcedureCall.optJSONObject("arguments");

        ProcedureCallRequest procedureCallRequest = newProcedureCallRequest();

        procedureCallRequest.setProcedureName(procedureName);
        if (jsonProperties != null) {
            Iterator it = jsonProperties.keys();
            while (it.hasNext()) {
                String key = (String)it.next();
                Object value = jsonProperties.get(key);
                procedureCallRequest.getParameterMap().put(key, value);
            }
        }

        return procedureCallRequest;
    }

    /**
     * Read all of the bytes from the InputStream and produce a String which represents the bytes read.
     *
     * TODO -- This seems like it could be exploited to cause memory thrashing or OutOfMemory errors, perhaps we should
     * place a limit on the size of the string and if we exceed it just bail?
     *
     * @param ins The input stream to read data from.
     * @return A String representing what was read from the stream.
     */
    protected String getStringFromInputStream(InputStream ins) throws IOException {
        if (ins == null) {
            throw new IllegalArgumentException("The parameter ins must be non-null.");
        }

        Queue<Pair<Integer,byte[]>> chunks = new LinkedList<Pair<Integer,byte[]>>();
        int bytesRead;
        int totalBytes = 0;
        byte[] buf = new byte[4096];
        while ((bytesRead = ins.read(buf)) != -1) {
            chunks.add(new Pair<Integer,byte[]>(bytesRead, buf.clone()));
            totalBytes += bytesRead;
        }

        byte[] dest = new byte[totalBytes];
        int position = 0;
        Pair<Integer,byte[]> chunk;
        while ((chunk = chunks.poll()) != null) {
            int length = chunk.getKey();
            byte[] src = chunk.getValue();
            System.arraycopy(src, 0, dest, position, length);
            position += length;
        }

        return new String(dest, "UTF-8");
    }

    /**
     * Generate a new ProcedureCallRequest instance.
     *
     * @return A new instance of procedure call request.
     */
    protected ProcedureCallRequest newProcedureCallRequest() {
        return PicoContainerFactory.getPicoContainer().getComponent(ProcedureCallRequest.class);
    }
}
