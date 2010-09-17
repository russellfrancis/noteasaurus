package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.domain.util.CorkboardUtility;
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
@AssertSecurity(canRead={},canWrite={Corkboard.class})
public class UpdateCorkboardProcedure extends AbstractProcedure {

    private CorkboardUtility corkboardUtility;

    private long id;
    private String label = null;
    private Short weight = null;
    private Boolean focused = null;

    public UpdateCorkboardProcedure(PersistenceManager persistenceManager, CorkboardUtility corkboardUtility) {
        super(persistenceManager);
        setCorkboardUtility(corkboardUtility);
    }

    @Override
    public boolean canExecute(SecurityPrincipal principal) {
        return principal instanceof Person && super.canExecute(principal);
    }

    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        Session session = getPersistenceManager().getSession();
        Corkboard corkboard = (Corkboard) session.get(Corkboard.class, getId());
        if (corkboard == null) {
            throw new EntityNotFoundException(Corkboard.class, getId());
        }

        if (!principal.canWrite(corkboard)) {
            throw new SecurityDeniedException(principal, corkboard);
        }

        if (isFocused() != null) {
            if (isFocused().booleanValue()) {
                getCorkboardUtility().removeFocusFor((Person)principal);
            }
            corkboard.setFocused(isFocused().booleanValue());
        }

        if (getLabel() != null) {
            setLabel(getCorkboardUtility().getUniqueLabel(getLabel(), (Person)principal, corkboard));
            corkboard.setLabel(getLabel());
        }

        if (getWeight() != null) {
            corkboard.setWeight(getWeight());
        }

        corkboard.assertValid();
        
        session.getTransaction().commit();
        return corkboard;
    }

    @Argument(name="id",required=true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Argument(name="label",required=false)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Argument(name="weight",required=false)
    public Short getWeight() {
        return weight;
    }

    public void setWeight(Short weight) {
        this.weight = weight;
    }

    @Argument(name="is_focused",required=false)
    public Boolean isFocused() {
        return focused;
    }

    public void setFocused(Boolean focused) {
        this.focused = focused;
    }

    public CorkboardUtility getCorkboardUtility() {
        return corkboardUtility;
    }

    public void setCorkboardUtility(CorkboardUtility corkboardUtility) {
        this.corkboardUtility = corkboardUtility;
    }
}
