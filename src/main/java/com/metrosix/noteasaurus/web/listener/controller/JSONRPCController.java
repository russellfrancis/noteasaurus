package com.metrosix.noteasaurus.web.listener.controller;

import com.metrosix.noteasaurus.rpc.RequestExecutor;
import com.metrosix.noteasaurus.rpc.RequestParser;
import com.metrosix.noteasaurus.rpc.ResponseFormatter;
import com.metrosix.noteasaurus.rpc.proc.impl.response.InvalidProcedureCallResponse;
import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.rpc.proc.impl.response.ProcedureCallResponse;
import com.metrosix.noteasaurus.rpc.impl.json.JSONRequestParser;
import com.metrosix.noteasaurus.rpc.impl.json.JSONResponseFormatter;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import com.metrosix.noteasaurus.util.PicoContainerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.picocontainer.PicoContainer;

/**
 * This acts as a controller for our web service.  It handles incoming requests and delegates the response
 * to the appropriate handler.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class JSONRPCController extends HttpServlet {

    /**
     * Handle an incoming RPC request over HTTP.
     *
     * @param httpRequest The HttpServletRequest for this request.
     * @param httpResponse The HttpServletResponse for this request.
     */
    @Override
    protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
    throws ServletException, IOException {
        try {
            ProcedureCallResponse rpcResponse = executeRequest(httpRequest);
            sendResponse(rpcResponse, httpResponse);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Read the client request from the HttpServletResponse instance, execute it and return a proper
     * ProcedureCallResponse to the caller.
     *
     * @param httpRequest The HttpServletResponse which we should interpret and execute.
     * @return A ProcedureCallResponse indicating what occured.
     */
    protected ProcedureCallResponse executeRequest(HttpServletRequest httpRequest)
    throws Exception {
        InputStream ins = httpRequest.getInputStream();
        try {
            ProcedureCallRequest request = newRequestParser().generate(ins);
            return getRequestExecutor().execute(getSecurityPrincipal(), request);
        }
        catch (JSONException e) {
            return new InvalidProcedureCallResponse(e);
        } finally {
            ins.close();
        }
    }

    /**
     * Send the ProcedureCallResponse to the client via the HttpServletResponse instance.
     *
     * @param response The ProcedureCallResponse we wish to send back to the client.
     * @param httpResponse The HttpServletResponse used to send a response to the client.
     */
    protected void sendResponse(ProcedureCallResponse response, HttpServletResponse httpResponse)
    throws IOException
    {
        ResponseFormatter responseFormatter = newResponseFormatter();
        httpResponse.setContentType(responseFormatter.getContentType());
        InputStream responseStream = responseFormatter.format(response);
        try {
            OutputStream outs = getHttpResponseOutputStream(httpResponse);
            try {
                int bytesRead;
                byte[] buf = new byte[4096];
                while ((bytesRead = responseStream.read(buf)) != -1) {
                    outs.write(buf, 0, bytesRead);
                }
            }
            finally {
                outs.close();
            }
        }
        finally {
            responseStream.close();
        }
    }

    /**
     * Get the OutputStream for the provided HttpServletResponse.
     *
     * @param response The HttpServletResponse whose OutputStream we want.
     * @return The OutputStream for the given HttpServletResponse.
     */
    protected OutputStream getHttpResponseOutputStream(HttpServletResponse response) throws IOException {
        return response.getOutputStream();
    }

    /**
     * Get the RequestExecutor for this application.
     *
     * @return The RequestExecutor for this application.
     */
    protected RequestExecutor getRequestExecutor() {
        return getPicoContainer().getComponent(RequestExecutor.class);
    }

    /**
     * Get the SecurityPrincipal who is making this request.
     *
     * @return The security principal aka Person who is making this request.
     */
    protected SecurityPrincipal getSecurityPrincipal() {
        return getSecurityPrincipalService().getSecurityPrincipal();
    }

    /**
     * Get the SecurityPrincipalService for this application.
     *
     * @return The SecurityPrincipalService for this application.
     */
    protected SecurityPrincipalService getSecurityPrincipalService() {
        return getPicoContainer().getComponent(SecurityPrincipalService.class);
    }

    /**
     * Get a reference to this applications PicoContainer.
     *
     * @return A reference to the applications PicoContainer.
     */
    protected PicoContainer getPicoContainer() {
        return PicoContainerFactory.getPicoContainer();
    }

    /**
     * Construct a new RequestParser instance.
     *
     * @return A new RequestParser instance.
     */
    protected RequestParser newRequestParser() {
        return new JSONRequestParser();
    }

    /**
     * Construct a new ResponseFormatter instance.
     *
     * @return Construct a new ResponseFormatter instance.
     */
    protected ResponseFormatter newResponseFormatter() {
        return new JSONResponseFormatter();
    }
}
