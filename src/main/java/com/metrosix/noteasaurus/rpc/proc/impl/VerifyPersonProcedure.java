package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Note;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.domain.VerificationToken;
import com.metrosix.noteasaurus.rpc.proc.Argument;
import com.metrosix.noteasaurus.rpc.proc.AssertSecurity;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import com.metrosix.noteasaurus.util.PicoContainerFactory;
import org.hibernate.Session;
import org.picocontainer.PicoContainer;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
@AssertSecurity(canRead={Person.class},canWrite={})
public class VerifyPersonProcedure extends AbstractProcedure {

    private Long uid;
    private String token;

    public VerifyPersonProcedure(PersistenceManager persistenceManager) {
        super(persistenceManager);
    }

    protected void createInitialEnvironment(Person person) {
        PicoContainer pico = PicoContainerFactory.getPicoContainer();

        // Create Default Corkboard.
        Corkboard corkboard = pico.getComponent(Corkboard.class);
        corkboard.setWeight((short)0);
        corkboard.setFocused(true);
        corkboard.setLabel("Default Corkboard");
        person.addCorkboard(corkboard);

        // Create Default Note.
        Note note = pico.getComponent(Note.class);
        corkboard.addNote(note);
        
        note.setCollapsed(false);
        note.setX((short)50);
        note.setY((short)50);
        note.setWidth((short)200);
        note.setHeight((short)200);
        note.setSkin("yellow");
        note.setZindex(0L);
        note.setContent("Welcome to Noteasaurus!");
    }

    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        Session session = getPersistenceManager().getSession();
        Person person = (Person) session.get(Person.class, getUserId());
        if (person != null) {
            for (VerificationToken vToken : person.getVerificationTokens()) {
                if (getToken().equals(vToken.getToken())) {
                    person.setVerified(true);
                    person.removeVerificationToken(vToken);
                    // Create initial corkboard and notes for the person.
                    createInitialEnvironment(person);
                    break;
                }
            }
        }
        getPersistenceManager().getTransaction().commit();

        if (person != null) {
            // Set person to null if we were not able to verify them.
            if (person.isVerified()) {
                person = person.reattach();
                PicoContainer pico = PicoContainerFactory.getPicoContainer();
                LoginProcedure loginProcedure = pico.getComponent(LoginProcedure.class);
                loginProcedure.login(person);
            } else {
                person = null;
            }
        }

        return person;
    }

    @Argument(name="uid",required=true)
    public Long getUserId() {
        return uid;
    }

    public void setUserId(Long uid) {
        this.uid = uid;
    }

    @Argument(name="token",required=true)
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
