package com.metrosix.noteasaurus.rpc.impl.json;

import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class JSONRequestParserTest {

    @Test
    public void testGetStringFromInputStream_ThrowsException() throws Exception {
        JSONRequestParser parser = new JSONRequestParser();
        try {
            parser.getStringFromInputStream(null);
            fail();
        } catch (IllegalArgumentException e) {
            // success.
        }
    }

    @Test
    public void testGetStringFromInputStream() throws Exception {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 .,!;?";
        Random r = new SecureRandom();
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 14000; ++i) {
            s.append( alphabet.charAt(r.nextInt(alphabet.length())));
        }
        InputStream ins = new ByteArrayInputStream(s.toString().getBytes());
        try {
            JSONRequestParser parser = new JSONRequestParser();
            assertEquals(s.toString(), parser.getStringFromInputStream(ins));
        } finally {
            ins.close();
        }
    }

    @Test
    public void testGenerate_ThrowsException() throws Exception {
        JSONRequestParser parser = new JSONRequestParser();
        try {
            parser.generate(null);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testGenerate0() throws Exception {
        String data = "{'procedure':'login','arguments':{'id':4,'version':42}}";
        ByteArrayInputStream ins = new ByteArrayInputStream(data.getBytes());
        try {
            ProcedureCallRequest procedureCallRequest = new ProcedureCallRequest(null);
            JSONRequestParser parser = createStrictMock(JSONRequestParser.class,
                    JSONRequestParser.class.getDeclaredMethod("newProcedureCallRequest"));
            
            expect(parser.newProcedureCallRequest()).andReturn(procedureCallRequest);

            replay(parser);
            ProcedureCallRequest pcr = parser.generate(ins);
            assertEquals(pcr, procedureCallRequest);
            assertEquals("login", pcr.getProcedureName());
            Map<String,Object> map = pcr.getParameterMap();
            assertEquals(2, map.size());
            assertEquals(4, Integer.parseInt(map.get("id").toString()));
            assertEquals(42, Integer.parseInt(map.get("version").toString()));

            verify(parser);
        } finally {
            ins.close();
        }
    }

    @Test
    public void testGenerate1() throws Exception {
        String data = "{'procedure':'login'}";
        ByteArrayInputStream ins = new ByteArrayInputStream(data.getBytes());
        try {
            ProcedureCallRequest procedureCallRequest = new ProcedureCallRequest(null);
            JSONRequestParser parser = createStrictMock(JSONRequestParser.class,
                    JSONRequestParser.class.getDeclaredMethod("newProcedureCallRequest"));

            expect(parser.newProcedureCallRequest()).andReturn(procedureCallRequest);

            replay(parser);
            ProcedureCallRequest pcr = parser.generate(ins);
            assertEquals(pcr, procedureCallRequest);
            assertEquals("login", pcr.getProcedureName());
            Map<String,Object> map = pcr.getParameterMap();
            assertEquals(0, map.size());

            verify(parser);
        } finally {
            ins.close();
        }
    }
}