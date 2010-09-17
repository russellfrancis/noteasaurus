package com.metrosix.noteasaurus.rpc;

import com.metrosix.noteasaurus.rpc.ProcedureCallStatus;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ProcedureCallStatusTest.java 247 2010-08-07 23:15:10Z adam $
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