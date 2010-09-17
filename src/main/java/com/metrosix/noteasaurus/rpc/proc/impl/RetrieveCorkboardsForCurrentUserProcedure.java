package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Person;
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
@AssertSecurity(canRead={Corkboard.class},canWrite={})
public class RetrieveCorkboardsForCurrentUserProcedure extends AbstractProcedure {

    public RetrieveCorkboardsForCurrentUserProcedure(
            PersistenceManager persistenceManager)
    {
        super(persistenceManager);
    }

    @Override
    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        List<Corkboard> corkboards = null;
        if (principal instanceof Person) {
            Session session = getPersistenceManager().getSession();
            Query query = session.createQuery("from Corkboard where owner = :owner order by weight asc");
            query.setParameter("owner", (Person)principal);
            corkboards = query.list();

            if (corkboards != null && !principal.canRead(corkboards)) {
                throw new SecurityDeniedException(principal, corkboards);
            }
        }

        return corkboards == null ? new ArrayList<Corkboard>(0) : corkboards;
    }
}
