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
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DeleteNoteProcedure.java 247 2010-08-07 23:15:10Z adam $
 */
@AssertSecurity(canRead={},canWrite={Note.class})
public class DeleteNoteProcedure extends AbstractProcedure {
    private long id;

    public DeleteNoteProcedure(PersistenceManager persistenceManager) {
        super(persistenceManager);
    }

    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        Session session = getPersistenceManager().getSession();
        Note note = (Note) session.get(Note.class, getId());

        if (note == null) {
            throw new EntityNotFoundException(Note.class, getId());
        }

        if (!principal.canWrite(note)) {
            throw new SecurityDeniedException(principal, note);
        }

        note.delete();
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
