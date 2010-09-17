package com.metrosix.noteasaurus.rpc;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatusCode;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ProcedureCallStatusCodeTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class ProcedureCallStatusCodeTest {
    @Test
    public void testBasicEnum() {
        for (ProcedureCallStatusCode code : ProcedureCallStatusCode.values()) {
            assertEquals(code.name(), code.getMessage());
        }

        assertEquals(0, ProcedureCallStatusCode.SUCCESS.getCode());
    }
}