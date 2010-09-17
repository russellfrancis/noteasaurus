package com.metrosix.noteasaurus.schedule.impl.executable;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.hibernate.Executable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class RemoveExpiredPersonsExecutable implements Executable {

    static private final Logger log = LoggerFactory.getLogger(RemoveExpiredPersonsExecutable.class);
    static private final int MAX_RECORDS_PER_ITERATION = 100;

    private PersistenceManager persistenceManager;

    public RemoveExpiredPersonsExecutable(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    public void execute() {
        removeExpiredPersons();
    }

    protected void removeExpiredPersons() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date expirationDate = cal.getTime();

        int rowsDeleted;
        do {
            // Query the database to find the expired person records.
            Session session = getPersistenceManager().getSession();
            Query query = session.createQuery(
                    "from Person " +
                    "where " +
                        "createdOn <= :expirationDate " +
                        "and " +
                        "verified = :verified "
                    );
            query.setParameter("expirationDate", expirationDate);
            query.setParameter("verified", Boolean.FALSE);
            query.setMaxResults(MAX_RECORDS_PER_ITERATION);
            List<Person> persons = query.list();

            // Iterate over these records, deleting each one.
            rowsDeleted = 0;
            if (persons != null) {
                for (Person person : persons) {
                    person.delete();
                    ++rowsDeleted;
                }
            }
            
            // Commit the transaction if we have deleted anything.
            if (rowsDeleted > 0) {
                getPersistenceManager().getTransaction().commit();
                if (log.isInfoEnabled()) {
                    log.info("Successfully removed " + rowsDeleted + " entries from the person table.");
                }
            }
            
            // Repeat until we have deleted all expired person records.
        } while (rowsDeleted == MAX_RECORDS_PER_ITERATION);
    }

    protected PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
