package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Note;
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
@AssertSecurity(canRead={},canWrite={Corkboard.class,Note.class})
public class DeleteCorkboardProcedure extends AbstractProcedure {
    private long id;

    public DeleteCorkboardProcedure(PersistenceManager persistenceManager) {
        super(persistenceManager);
    }

    @Override
    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        Session session = getPersistenceManager().getSession();
        Corkboard corkboard = (Corkboard) session.get(Corkboard.class, getId());
        if (corkboard == null) {
            throw new EntityNotFoundException(Corkboard.class, getId());
        }

        if (!principal.canWrite(corkboard) || !principal.canWrite(corkboard.getNotes())) {
            throw new SecurityDeniedException(principal, corkboard);
        }

        corkboard.delete();
        session.getTransaction().commit();
        return null;
    }

    @Argument(name="id",required=true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
