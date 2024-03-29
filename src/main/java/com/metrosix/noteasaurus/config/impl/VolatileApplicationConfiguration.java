package com.metrosix.noteasaurus.config.impl;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.ApplicationConfigurationParameter;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.ConfigurationParameter;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class VolatileApplicationConfiguration implements ApplicationConfiguration {
    private PersistenceManager persistenceManager;

    public VolatileApplicationConfiguration(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public String getValueOf(ApplicationConfigurationParameter parameter) {
        if (!parameter.isVolatile()) {
            throw new IllegalArgumentException("The parameter '" + parameter.getKey() + "' must be volatile!");
        }

        Session session = getPersistenceManager().getSession();
        Query query = session.createQuery("from ConfigurationParameter where label = :label");
        query.setParameter("label", parameter.getKey());
        ConfigurationParameter result = (ConfigurationParameter) query.uniqueResult();
        return result != null ? result.getValue() : null;
    }

    public PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    public void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }
}
