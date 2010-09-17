package com.metrosix.noteasaurus.database.impl;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.ApplicationConfigurationParameter;
import com.metrosix.noteasaurus.config.impl.NonVolatileApplicationConfiguration;
import com.metrosix.noteasaurus.database.ConnectionManager;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.hibernate.HibernatePicofier;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.resolver.DialectFactory;
import org.hibernate.metadata.ClassMetadata;

/**
 * This is a default persistence manager implementation intended to be used with Hibernate.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultPersistenceManager implements PersistenceManager {

    private ConnectionManager connectionManager;
    private ApplicationConfiguration applicationConfiguration;
    
    private SessionFactory sessionFactory = null;

    /**
     * Construct a new instance of the DefaultPersistenceManager.
     *
     * @param connectionManager The ConnectionManager to use for this instance.
     * @param applicationConfiguration The ApplicationConfiguration to use for this instance.
     */
    public DefaultPersistenceManager(ConnectionManager connectionManager, 
            NonVolatileApplicationConfiguration applicationConfiguration)
    {
        setConnectionManager(connectionManager);
        setApplicationConfiguration(applicationConfiguration);
    }

    /**
     * This method will read the annotation configuration information for hibernate based on the
     * persistent classes as defined in the file "/persistent.classes" on the classpath.
     *
     * @return An AnnotationConfiguration instance.
     */
    protected AnnotationConfiguration getAnnotationConfiguration() throws IOException, ClassNotFoundException {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        
        // Add our annotated classes.
        InputStream ins = getClass().getResourceAsStream("/persistent.classes");
        try {
            Reader insReader = new InputStreamReader( ins );
            try {
                LineNumberReader reader = new LineNumberReader( insReader );
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.length() > 0 && !line.startsWith("#")) {
                            configuration.addAnnotatedClass(Class.forName(line));
                        }
                    }
                } finally {
                    reader.close();
                }
            } finally {
                insReader.close();
            }
        } finally {
            ins.close();
        }
        
        return configuration;
    }

    public Set<Class> getPersistentClasses() {
        Set<Class> persistentClasses = new HashSet<Class>();
        Map<String, ClassMetadata> classMetadataMap = getSessionFactory().getAllClassMetadata();
        Set<Entry<String, ClassMetadata>> entrySet = classMetadataMap.entrySet();
        for (Entry<String, ClassMetadata> entry : entrySet) {
            persistentClasses.add(entry.getValue().getMappedClass(EntityMode.POJO));
        }
        return persistentClasses;
    }

    /**
     * This method will return a Properties instance with the properties configured in the file
     * "/hibernate.properties" which exists on the classpath.
     *
     * @return A Properties instance with the properties from the /hibernate.properties file.
     */
    protected Properties getHibernateProperties() throws IOException {
        // Add our configuration properties.
        Properties props = new Properties();
        InputStream ins = getClass().getResourceAsStream("/hibernate.properties");
        try {
            props.load(ins);
        } finally {
            ins.close();
        }
        return props;
    }

    /**
     * This method will configure the PersistenceManager so that it may persist instances of our
     * domain model.
     *
     * @return true if we were able to configure the PersistenceManager.
     */
    public boolean configure() throws ClassNotFoundException, IOException, SQLException {

        if (getSessionFactory() == null) {
            // Load our annotated classes.
            AnnotationConfiguration configuration = getAnnotationConfiguration();

            // Add our configuration properties.
            Properties props = getHibernateProperties();

            Dialect dialect;
            Connection connection = getConnectionManager().getConnection();
            try {
                dialect = DialectFactory.buildDialect(new Properties(), connection);
            } finally {
                connection.close();
            }

            props.put("hibernate.dialect", dialect.getClass().getName() );
            props.put("hibernate.connection.username",
                    getApplicationConfiguration().getValueOf(
                    ApplicationConfigurationParameter.DB_USERNAME));
            
            props.put("hibernate.connection.password",
                    getApplicationConfiguration().getValueOf(
                    ApplicationConfigurationParameter.DB_PASSWORD));
            
            props.put("hibernate.connection.url",
                    getApplicationConfiguration().getValueOf(
                    ApplicationConfigurationParameter.DB_URL));
            
            props.put("hibernate.connection.driver_class",
                    getApplicationConfiguration().getValueOf(
                    ApplicationConfigurationParameter.DB_DRIVER));
            
            configuration.addProperties(props);

            configuration.setInterceptor(new HibernatePicofier());
            configuration = configuration.configure();
            setSessionFactory(configuration.buildSessionFactory());
        }
 
        return true;
    }

    /**
     * Get an instance of the Session.
     *
     * @return An instance of the Session.
     */
    public Session getSession() {
        SessionFactory sf = getSessionFactory();
        if (sf == null) {
            throw new HibernateException("Unable to get an instance of the SessionFactory, has " +
                    "this instance been configured?" );
        }
        Session session = sf.getCurrentSession();
        session.beginTransaction();
        return session;
    }

    /**
     * Get the current Transaction.  If no transaction exists, one is started and returned.
     *
     * @return The current Transaction.
     */
    public Transaction getTransaction() {
        return getSession().getTransaction();
    }

    /**
     * Get an instance of the SessionFactory.
     *
     * @return An instance of the Hibernate SessionFactory.
     */
    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Set an instance of the SessionFactory.
     * @param aSessionFactory
     */
    private void setSessionFactory(SessionFactory aSessionFactory) {
        sessionFactory = aSessionFactory;
    }

    /**
     * Get an instance of the ConnectionManager for this class.
     *
     * @return An instance of the ConnectionManager for this class.
     */
    protected ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Set the ConnectionManager for this class.
     *
     * @param aConnectionManager The ConnectionManager to use for this class.
     */
    protected void setConnectionManager(ConnectionManager aConnectionManager) {
        connectionManager = aConnectionManager;
    }

    /**
     * Get the ApplicationConfiguration to use for this instance.
     *
     * @return The ApplicationConfiguration to use for this instance.
     */
    protected ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    /**
     * Set the ApplicationConfiguration to use for this instance.
     *
     * @param anApplicationConfiguration The ApplicationConfiguration to use for this instance.
     */
    protected void setApplicationConfiguration(ApplicationConfiguration anApplicationConfiguration) {
        applicationConfiguration = anApplicationConfiguration;
    }
}
