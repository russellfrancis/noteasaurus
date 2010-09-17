package com.metrosix.noteasaurus.rpc;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class ProcedureCallStatusTest {

    @Test
    public void testBasic() {
        int i = 0;
        for (ProcedureCallStatus status : ProcedureCallStatus.values()) {
            assertEquals(i++, status.ordinal());
        }
    }
}