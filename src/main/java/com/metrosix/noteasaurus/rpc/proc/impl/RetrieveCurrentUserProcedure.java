package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.rpc.proc.AssertSecurity;
import com.metrosix.noteasaurus.security.SecurityPrincipal;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: RetrieveCurrentUserProcedure.java 247 2010-08-07 23:15:10Z adam $
 */
@AssertSecurity(canRead={Person.class},canWrite={})
public class RetrieveCurrentUserProcedure extends AbstractProcedure {

    public RetrieveCurrentUserProcedure(PersistenceManager persistenceManager)
    {
        super(persistenceManager);
    }

    public Object executeAs(SecurityPrincipal principal)
    {
        return (principal instanceof Person) ? principal : null;
    }
}
