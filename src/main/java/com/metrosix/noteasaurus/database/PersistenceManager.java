package com.metrosix.noteasaurus.database;

import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * This interface defines a manager which controls access to our persistence mechanism.
 * 
 * @author Russell Francis (russ@metro-six.com)
 */
public interface PersistenceManager {
    
    /**
     * Configure the persistence mechanism for the database.
     * 
     * @return true if we were able to successfully configure the persistence  manager, false
     * otherwise.
     */
    public boolean configure() throws Exception;

    /**
     * This method will get an instance of the {@link Session} for this application.  This method
     * should never return null but will throw a {@link HibernateException} if there is a failure
     * in establishing a Session for this instance.
     *
     * @return An instance of the Session for this application.
     */
    public Session getSession();

    /**
     * This method will get an instance of the current {@link Transaction}.
     *
     * @return A non-null instance to the current Transaction, this should never return null but
     * will throw a {@link HibernateException} if there is an error getting the current session.
     */
    public Transaction getTransaction();

    public Set<Class> getPersistentClasses();
}
