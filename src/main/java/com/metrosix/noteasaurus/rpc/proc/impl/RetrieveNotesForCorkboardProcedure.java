package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Note;
import com.metrosix.noteasaurus.rpc.proc.Argument;
import com.metrosix.noteasaurus.rpc.proc.AssertSecurity;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.SecurityDeniedException;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
@AssertSecurity(canRead={Note.class},canWrite={})
public class RetrieveNotesForCorkboardProcedure extends AbstractProcedure {
    private long id;

    public RetrieveNotesForCorkboardProcedure(PersistenceManager persistenceManager)
    {
        super(persistenceManager);
    }

    @Override
    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        List<Note> notes = null;
        Session session = getPersistenceManager().getSession();
        Query query = session.createQuery(
                "from Note as note where note.corkboard.id = :corkboardId order by note.zIndex asc");
        query.setParameter("corkboardId", getId());
        notes = query.list();

        if (notes != null && !principal.canRead(notes)) {
            throw new SecurityDeniedException(principal, notes);
        }

        return notes == null ? new ArrayList<Note>(0) : notes;
    }

    @Argument(name="id",required=true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
