package com.metrosix.noteasaurus.domain.util;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Person;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: PersonUtility.java 247 2010-08-07 23:15:10Z adam $
 */
public class PersonUtility {

    private PersistenceManager persistenceManager;

    public PersonUtility(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    public Person getPersonByEmail(String email) {
        Session session = getPersistenceManager().getSession();
        Query query = session.createQuery("from Person where email = :email");
        query.setParameter("email", email);
        return (Person) query.uniqueResult();
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
