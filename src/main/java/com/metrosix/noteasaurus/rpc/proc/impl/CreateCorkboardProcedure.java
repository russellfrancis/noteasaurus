package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.domain.util.CorkboardUtility;
import com.metrosix.noteasaurus.rpc.proc.AssertSecurity;
import com.metrosix.noteasaurus.rpc.proc.Argument;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import com.metrosix.noteasaurus.util.PicoContainerFactory;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: CreateCorkboardProcedure.java 247 2010-08-07 23:15:10Z adam $
 */
@AssertSecurity(canRead={},canWrite={Corkboard.class})
public class CreateCorkboardProcedure extends AbstractProcedure {

    private CorkboardUtility corkboardUtility;

    private String label;
    private Short weight;
    private boolean focused = false;

    public CreateCorkboardProcedure(PersistenceManager persistenceManager, CorkboardUtility corkboardUtility)
    {
        super(persistenceManager);
        setCorkboardUtility(corkboardUtility);
    }

    @Override
    public boolean canExecute(SecurityPrincipal principal) {
        return principal instanceof Person && super.canExecute(principal);
    }

    public Object executeAs(SecurityPrincipal principal) 
    throws ProcedureException
    {
        if (isFocused()) {
            getCorkboardUtility().removeFocusFor((Person)principal);
        }

        Short defaultWeight = getCorkboardUtility().getDefaultWeight(getWeight(), (Person)principal);
        String uniqueLabel = getCorkboardUtility().getUniqueLabel(getLabel(), (Person)principal);

        Corkboard corkboard = newCorkboard();
        corkboard.setLabel(uniqueLabel);
        corkboard.setWeight(defaultWeight);
        corkboard.setFocused(isFocused());
        ((Person)principal).addCorkboard(corkboard);

        corkboard.assertValid();

        corkboard.save();
        getPersistenceManager().getTransaction().commit();
        return corkboard;
    }

    protected Corkboard newCorkboard() {
        return PicoContainerFactory.getPicoContainer().getComponent(Corkboard.class);
    }

    @Argument(name="label",required=true)
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
    public boolean isFocused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public CorkboardUtility getCorkboardUtility() {
        return corkboardUtility;
    }

    public void setCorkboardUtility(CorkboardUtility corkboardUtility) {
        this.corkboardUtility = corkboardUtility;
    }
}
