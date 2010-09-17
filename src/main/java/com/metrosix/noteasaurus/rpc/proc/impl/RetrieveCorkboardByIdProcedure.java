package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.rpc.proc.Argument;
import com.metrosix.noteasaurus.rpc.proc.AssertSecurity;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.EntityNotFoundException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.SecurityDeniedException;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import org.hibernate.Session;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
@AssertSecurity(canRead={Corkboard.class},canWrite={})
public class RetrieveCorkboardByIdProcedure extends AbstractProcedure {
    private long id;

    public RetrieveCorkboardByIdProcedure(PersistenceManager persistenceManager)
    {
        super(persistenceManager);
    }

    @Override
    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        Session session = getPersistenceManager().getSession();
        Corkboard corkboard = (Corkboard)session.get(Corkboard.class, getId());

        if (corkboard == null) {
            throw new EntityNotFoundException(Corkboard.class, getId());
        }

        if (!principal.canRead(corkboard)) {
            throw new SecurityDeniedException(principal, corkboard);
        }

        return corkboard;
    }

    @Argument(name="id",required=true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
