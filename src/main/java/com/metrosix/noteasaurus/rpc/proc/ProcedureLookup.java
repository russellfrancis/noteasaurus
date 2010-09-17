package com.metrosix.noteasaurus.rpc.proc;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ProcedureLookup.java 247 2010-08-07 23:15:10Z adam $
 */
public interface ProcedureLookup {
    public Procedure lookup(String procedureName);
}
