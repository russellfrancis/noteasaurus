package com.metrosix.noteasaurus.rpc;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russ@metro-six.com)
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