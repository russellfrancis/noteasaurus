package com.metrosix.noteasaurus.web.listener.controller;

import com.metrosix.noteasaurus.web.listener.controller.JSONRPCController;
import com.metrosix.noteasaurus.rpc.RequestExecutor;
import com.metrosix.noteasaurus.rpc.RequestParser;
import com.metrosix.noteasaurus.rpc.ResponseFormatter;
import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.rpc.proc.impl.response.InvalidProcedureCallResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.easymock.classextension.ConstructorArgs;
import org.json.JSONException;
import org.junit.Test;
import org.picocontainer.PicoContainer;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: JSONRPCControllerTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class JSONRPCControllerTest {

    @Test
    public void testNewResponseFormatter() {
        JSONRPCController rpc = new JSONRPCController();
        assertNotNull(rpc.newResponseFormatter());
    }

    @Test
    public void testNewRequestParser() {
        JSONRPCController rpc = new JSONRPCController();
        assertNotNull(rpc.newRequestParser());
    }

    @Test
    public void testGetSecurityPrincipalService() throws Exception {
        SecurityPrincipalService sps = createStrictMock(SecurityPrincipalService.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        JSONRPCController rpc = createStrictMock( JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("getPicoContainer"));

        expect(rpc.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(SecurityPrincipalService.class)).andReturn(sps);

        replay(rpc, pico, sps);
        assertEquals(sps, rpc.getSecurityPrincipalService());
        verify(rpc, pico, sps);
    }

    @Test
    public void testGetSecurityPrincipal() throws Exception {
        SecurityPrincipal sp = createStrictMock(SecurityPrincipal.class);
        SecurityPrincipalService sps = createStrictMock(SecurityPrincipalService.class);
        JSONRPCController rpc = createStrictMock( JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("getSecurityPrincipalService"));

        expect(rpc.getSecurityPrincipalService()).andReturn(sps);
        expect(sps.getSecurityPrincipal()).andReturn(sp);

        replay(rpc, sps, sp);
        assertEquals(sp, rpc.getSecurityPrincipal());
        verify(rpc, sps, sp);
    }

    @Test
    public void testGetRequestExecutor() throws Exception {
        RequestExecutor re = createStrictMock(RequestExecutor.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        JSONRPCController rpc = createStrictMock( JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("getPicoContainer"));

        expect(rpc.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(RequestExecutor.class)).andReturn(re);

        replay(re, pico, rpc);
        assertEquals(re, rpc.getRequestExecutor());
        verify(re, pico, rpc);
    }

    @Test
    public void testSendResponse() throws Exception {
        String msg = "Hello, World!";
        ConstructorArgs args = new ConstructorArgs(
                ByteArrayInputStream.class.getConstructor(byte[].class),
                msg.getBytes());
        ByteArrayInputStream ins = createStrictMock(ByteArrayInputStream.class,
                args,
                ByteArrayInputStream.class.getMethod("close"));
        ByteArrayOutputStream outs = createStrictMock(ByteArrayOutputStream.class,
                new ConstructorArgs(ByteArrayOutputStream.class.getConstructor()),
                ByteArrayOutputStream.class.getMethod("close"));

        ProcedureCallResponse response = createStrictMock(ProcedureCallResponse.class);
        HttpServletResponse httpResponse = createStrictMock(HttpServletResponse.class);
        ResponseFormatter formatter = createStrictMock(ResponseFormatter.class);
        JSONRPCController rpc = createStrictMock(JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("newResponseFormatter"),
                JSONRPCController.class.getDeclaredMethod("getHttpResponseOutputStream", HttpServletResponse.class));

        expect(rpc.newResponseFormatter()).andReturn(formatter);
        expect(formatter.getContentType()).andReturn("application/json");
        httpResponse.setContentType("application/json");
        expect(formatter.format(response)).andReturn(ins);
        expect(rpc.getHttpResponseOutputStream(httpResponse)).andReturn(outs);
        outs.close();
        ins.close();

        replay(response, httpResponse, formatter, rpc, outs, ins);
        rpc.sendResponse(response, httpResponse);
        assertEquals(msg, new String(outs.toByteArray()));
        verify(response, httpResponse, formatter, rpc, outs, ins);
    }

    @Test
    public void testExecuteRequest() throws Exception {
        ServletInputStream ins = createStrictMock(ServletInputStream.class);
        HttpServletRequest httpRequest = createStrictMock(HttpServletRequest.class);
        RequestParser parser = createStrictMock(RequestParser.class);
        RequestExecutor executor = createStrictMock(RequestExecutor.class);
        ProcedureCallRequest request = createStrictMock(ProcedureCallRequest.class);
        ProcedureCallResponse response = createStrictMock(ProcedureCallResponse.class);
        SecurityPrincipal principal = createStrictMock(SecurityPrincipal.class);
        JSONRPCController rpc = createStrictMock(JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("newRequestParser"),
                JSONRPCController.class.getDeclaredMethod("getRequestExecutor"),
                JSONRPCController.class.getDeclaredMethod("getSecurityPrincipal"));

        expect(httpRequest.getInputStream()).andReturn(ins);
        expect(rpc.newRequestParser()).andReturn(parser);
        expect(parser.generate(ins)).andReturn(request);
        expect(rpc.getRequestExecutor()).andReturn(executor);
        expect(rpc.getSecurityPrincipal()).andReturn(principal);
        expect(executor.execute(principal, request)).andReturn(response);
        ins.close();

        replay(ins, httpRequest, parser, executor, request, response, principal, rpc);
        assertEquals(response, rpc.executeRequest(httpRequest));
        verify(ins, httpRequest, parser, executor, request, response, principal, rpc);
    }

    @Test
    public void testExecuteRequest_WithException() throws Exception {
        ServletInputStream ins = createStrictMock(ServletInputStream.class);
        HttpServletRequest httpRequest = createStrictMock(HttpServletRequest.class);
        RequestParser parser = createStrictMock(RequestParser.class);
        RequestExecutor executor = createStrictMock(RequestExecutor.class);
        ProcedureCallRequest request = createStrictMock(ProcedureCallRequest.class);
        SecurityPrincipal principal = createStrictMock(SecurityPrincipal.class);
        JSONRPCController rpc = createStrictMock(JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("newRequestParser"),
                JSONRPCController.class.getDeclaredMethod("getRequestExecutor"),
                JSONRPCController.class.getDeclaredMethod("getSecurityPrincipal"));

        expect(httpRequest.getInputStream()).andReturn(ins);
        expect(rpc.newRequestParser()).andReturn(parser);
        expect(parser.generate(ins)).andThrow(new JSONException("Yikes!"));
        ins.close();

        replay(ins, httpRequest, parser, executor, request, principal, rpc);
        ProcedureCallResponse response = rpc.executeRequest(httpRequest);
        assertTrue(response instanceof InvalidProcedureCallResponse);
        verify(ins, httpRequest, parser, executor, request, principal, rpc);
    }

    @Test
    public void testGetHttpResponseOutputStream() throws Exception {
        HttpServletResponse response = createStrictMock(HttpServletResponse.class);
        ServletOutputStream outs = createStrictMock(ServletOutputStream.class);
        JSONRPCController rpc = new JSONRPCController();

        expect(response.getOutputStream()).andReturn(outs);

        replay(response, outs);
        assertEquals(outs, rpc.getHttpResponseOutputStream(response));
        verify(response, outs);
    }

    @Test
    public void testDoPost() throws Exception {
        HttpServletRequest httpRequest = createStrictMock(HttpServletRequest.class);
        HttpServletResponse httpResponse = createStrictMock(HttpServletResponse.class);
        ProcedureCallResponse response = createStrictMock(ProcedureCallResponse.class);
        JSONRPCController rpc = createStrictMock(JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("sendResponse", ProcedureCallResponse.class, HttpServletResponse.class),
                JSONRPCController.class.getDeclaredMethod("executeRequest", HttpServletRequest.class));

        expect(rpc.executeRequest(httpRequest)).andReturn(response);
        rpc.sendResponse(response, httpResponse);

        replay(httpRequest, httpResponse, response, rpc);
        rpc.doPost(httpRequest, httpResponse);
        verify(httpRequest, httpResponse, response, rpc);
    }

    @Test
    public void testDoPost_WithRuntimeException() throws Exception {
        HttpServletRequest httpRequest = createStrictMock(HttpServletRequest.class);
        HttpServletResponse httpResponse = createStrictMock(HttpServletResponse.class);
        ProcedureCallResponse response = createStrictMock(ProcedureCallResponse.class);
        JSONRPCController rpc = createStrictMock(JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("sendResponse", ProcedureCallResponse.class, HttpServletResponse.class),
                JSONRPCController.class.getDeclaredMethod("executeRequest", HttpServletRequest.class));

        expect(rpc.executeRequest(httpRequest)).andThrow(new RuntimeException("Yikes!"));

        replay(httpRequest, httpResponse, response, rpc);
        try {
            rpc.doPost(httpRequest, httpResponse);
            fail();
        } catch (RuntimeException e) {
            // success.
        }
        verify(httpRequest, httpResponse, response, rpc);
    }

    @Test
    public void testDoPost_WithIOException() throws Exception {
        HttpServletRequest httpRequest = createStrictMock(HttpServletRequest.class);
        HttpServletResponse httpResponse = createStrictMock(HttpServletResponse.class);
        ProcedureCallResponse response = createStrictMock(ProcedureCallResponse.class);
        JSONRPCController rpc = createStrictMock(JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("sendResponse", ProcedureCallResponse.class, HttpServletResponse.class),
                JSONRPCController.class.getDeclaredMethod("executeRequest", HttpServletRequest.class));

        expect(rpc.executeRequest(httpRequest)).andThrow(new IOException("Yikes!"));

        replay(httpRequest, httpResponse, response, rpc);
        try {
            rpc.doPost(httpRequest, httpResponse);
            fail();
        } catch (IOException e) {
            // success.
        }
        verify(httpRequest, httpResponse, response, rpc);
    }

    @Test
    public void testDoPost_WithServletException() throws Exception {
        HttpServletRequest httpRequest = createStrictMock(HttpServletRequest.class);
        HttpServletResponse httpResponse = createStrictMock(HttpServletResponse.class);
        ProcedureCallResponse response = createStrictMock(ProcedureCallResponse.class);
        JSONRPCController rpc = createStrictMock(JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("sendResponse", ProcedureCallResponse.class, HttpServletResponse.class),
                JSONRPCController.class.getDeclaredMethod("executeRequest", HttpServletRequest.class));

        expect(rpc.executeRequest(httpRequest)).andThrow(new ServletException("Yikes!"));

        replay(httpRequest, httpResponse, response, rpc);
        try {
            rpc.doPost(httpRequest, httpResponse);
            fail();
        } catch (ServletException e) {
            // success.
        }
        verify(httpRequest, httpResponse, response, rpc);
    }

    @Test
    public void testDoPost_WithOtherException() throws Exception {
        HttpServletRequest httpRequest = createStrictMock(HttpServletRequest.class);
        HttpServletResponse httpResponse = createStrictMock(HttpServletResponse.class);
        ProcedureCallResponse response = createStrictMock(ProcedureCallResponse.class);
        JSONRPCController rpc = createStrictMock(JSONRPCController.class,
                JSONRPCController.class.getDeclaredMethod("sendResponse", ProcedureCallResponse.class, HttpServletResponse.class),
                JSONRPCController.class.getDeclaredMethod("executeRequest", HttpServletRequest.class));

        expect(rpc.executeRequest(httpRequest)).andThrow(new Exception("Yikes!"));

        replay(httpRequest, httpResponse, response, rpc);
        try {
            rpc.doPost(httpRequest, httpResponse);
            fail();
        } catch (ServletException e) {
            // success.
        }
        verify(httpRequest, httpResponse, response, rpc);
    }
}