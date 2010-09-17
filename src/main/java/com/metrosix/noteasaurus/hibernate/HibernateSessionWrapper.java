package com.metrosix.noteasaurus.hibernate;

import com.metrosix.noteasaurus.database.PersistenceManager;
import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: HibernateSessionWrapper.java 247 2010-08-07 23:15:10Z adam $
 */
public class HibernateSessionWrapper {
    static private final Logger log = LoggerFactory.getLogger(HibernateSessionWrapper.class);

    private PersistenceManager persistenceManager;

    public HibernateSessionWrapper(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    public void execute(Executable delegate) throws Exception {
        if (log.isTraceEnabled()) {
            log.trace(getClass().getSimpleName() + " triggered.");
        }

        boolean mustCloseSession = false;
        try {
            delegate.execute();
            getPersistenceManager().getTransaction().commit();
            if (log.isTraceEnabled()) {
                log.trace(getClass().getSimpleName() + " commit() successful.");
            }
        }
        catch (Exception e) {
            mustCloseSession = isExceptionSessionCorrupting(e);
            try {
                getPersistenceManager().getTransaction().rollback();
                if (log.isInfoEnabled()) {
                    log.info(getClass().getSimpleName() + " rollback() successful.");
                }
            }
            catch (Exception ex) {
                mustCloseSession = true;
                if (log.isErrorEnabled()) {
                    log.error("Error rolling back database transaction: " +
                            ex.toString(), ex);
                }
            }

            throw e;
        }
        finally {
            if (mustCloseSession) {
                try {
                    getPersistenceManager().getSession().close();
                    if (log.isInfoEnabled()) {
                        log.info(getClass().getSimpleName() + " session closed due to previous failures.");
                    }
                }
                catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("Unable to close the session: " + e.toString(), e);
                    }
                }
            }
        }
    }

    protected boolean isExceptionSessionCorrupting(Exception e) {
        return e instanceof JDBCException      ||
               e instanceof HibernateException ||
               e instanceof SQLException;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
