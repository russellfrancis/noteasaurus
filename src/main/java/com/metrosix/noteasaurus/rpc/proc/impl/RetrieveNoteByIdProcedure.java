package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
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
@AssertSecurity(canRead={Note.class},canWrite={})
public class RetrieveNoteByIdProcedure extends AbstractProcedure {

    private long id;

    public RetrieveNoteByIdProcedure(PersistenceManager persistenceManager)
    {
        super(persistenceManager);
    }

    public Object executeAs(SecurityPrincipal principal)
    throws ProcedureException
    {
        Session session = getPersistenceManager().getSession();
        Note note = (Note)session.get(Note.class, getId());

        if (note == null) {
            throw new EntityNotFoundException(Note.class, getId());
        }

        if (!principal.canRead(note)) {
            throw new SecurityDeniedException(principal, note);
        }

        return note;
    }

    @Argument(name="id",required=true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
