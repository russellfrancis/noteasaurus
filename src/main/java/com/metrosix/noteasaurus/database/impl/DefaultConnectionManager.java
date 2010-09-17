package com.metrosix.noteasaurus.database.impl;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.ApplicationConfigurationParameter;
import com.metrosix.noteasaurus.config.impl.NonVolatileApplicationConfiguration;
import com.metrosix.noteasaurus.database.ConnectionManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.validator.GenericValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DefaultConnectionManager.java 247 2010-08-07 23:15:10Z adam $
 */
public class DefaultConnectionManager implements ConnectionManager {
    static private final Logger log = LoggerFactory.getLogger(DefaultConnectionManager.class);

    private ApplicationConfiguration applicationConfiguration;

    /**
     * Construct a new instance of the DefaultConnectionManager.
     *
     * @param applicationConfiguration An instance of the ApplicationConfiguration instance for this
     * application.
     */
    public DefaultConnectionManager(NonVolatileApplicationConfiguration applicationConfiguration) {
        setApplicationConfiguration(applicationConfiguration);
    }

    /**
     * Start the ConnectionManager, this will be called before this instance is put into service.
     */
    public void start() {
        log.trace("DefaultConnectionManager started.");
    }

    /**
     * Stop the ConnectionManager, this will be called after this instance is removed from service.
     */
    public void stop() {
        log.trace("DefaultConnectionManager stopped.");
    }
    
    /**
     * This method can be used to grab a {@link Connection} to a database which
     * has been configured and whose properties are stored in the {@link ApplicationConfiguration}
     * instance for this application.  The connection which is returned should be closed once
     * it is no longer needed in order to prevent connection leaks against the database.
     * 
     * @return A Connection instance for the currently configured database or null
     * if the application has not yet been configured.
     * @throws java.sql.SQLException If the database is not configured or there
     * is a problem connecting to it.
     * @throws java.lang.ClassNotFoundException If the database driver could
     * not be found.
     */
    public Connection getConnection() throws SQLException, ClassNotFoundException 
    {
        return getConnection(
                getDatabaseDriver(),
                getDatabaseUrl(),
                getDatabaseUsername(),
                getDatabasePassword());
    }

    /**
     * This method can be used to get a connection to the database using the provided driver and
     * JDBC connection string. The connection which is returned should be closed once it is no 
     * longer needed in order to prevent connection leaks against the database.
     *
     * @param dbDriver A non-null non-empty string which references the {@link Class} of the database 
     * driver to user.
     * @param dbUrl A non-null non-empty string which contains the db url to connect to.
     * @return A Connection instance for the currently configured database.
     * @throws java.sql.SQLException If the database is not configured or there
     * is a problem connecting to it.
     * @throws java.lang.ClassNotFoundException If the database driver could
     * not be found.
     */
    protected Connection getConnection(String dbDriver, String dbUrl) 
            throws SQLException, ClassNotFoundException 
    {
        return getConnection(dbDriver, dbUrl, null, null);
    }

    /**
     * This method can be used to grab a {@link Connection} to a database using the provided 
     * configuration.  The connection which is returned should be closed once it is no longer needed
     * in order to prevent connection leaks against the database.
     * 
     * @param dbDriver A non-null non-empty string which references the {@link Class} of the database 
     * driver to user.
     * @param dbUrl A non-null non-empty string which contains the db url to connect to.
     * @param dbUsername An optional parameter which specifies the username if one is required.
     * @param dbPassword An optional parameter which specifies the password if one is required.
     * @return A Connection instance for the currently configured database.
     * @throws java.sql.SQLException If the database is not configured or there
     * is a problem connecting to it.
     * @throws java.lang.ClassNotFoundException If the database driver could
     * not be found.
     */
    protected Connection getConnection(
            String dbDriver, String dbUrl, String dbUsername, String dbPassword) 
            throws SQLException, ClassNotFoundException 
    {    
        if (GenericValidator.isBlankOrNull(dbDriver)) {
            throw new SQLException(
                    "The database appears to be unconfigured, is the property '" +
                    ApplicationConfigurationParameter.DB_DRIVER.getKey() + 
                    "' defined in 'config.properties'?");
        }

        if (GenericValidator.isBlankOrNull(dbUrl)) {
            throw new SQLException(
                    "The database appears to be unconfigured, is the property '" +
                    ApplicationConfigurationParameter.DB_URL.getKey() + 
                    "' defined in 'config.properties'?");
        }

        // try to load the class to ensure that the driver is available.
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            ClassNotFoundException ex = new ClassNotFoundException(
                    "Unable to find the requested database driver '" + dbDriver +
                    "', is the driver available in the 'config.properties'?");
            throw ex;
        }

        dbUsername = GenericValidator.isBlankOrNull(dbUsername) ? null : dbUsername;
        dbPassword = GenericValidator.isBlankOrNull(dbPassword) ? null : dbPassword;

        return getDriverManagerGetConnection(dbUrl, dbUsername, dbPassword);
    }

    /**
     * This method returns a connection directly from the DriverManager, this creates a seam for
     * testing purposes.
     *
     * @param dbUrl The JDBC url we should use to connect to the database.
     * @param dbUsername The username used to connect to the database.
     * @param dbPassword The password used to connect to the database.
     * @return A Connection to the database based on the provided parameters.
     */
    protected Connection getDriverManagerGetConnection(String dbUrl, String dbUsername, String dbPassword)
    throws SQLException
    {
        Connection c = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
        if (c.getMetaData().getDatabaseProductName().toUpperCase().contains("POSTGRES")) {
            c.setAutoCommit(true);
        } else {
            c.setAutoCommit(false);
        }
        return c;
    }

    /**
     * Get an instance of the ApplicationConfiguration for this application.
     *
     * @return The instance of ApplicationConfiguration for this ConnectionManager.
     */
    protected ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    /**
     * Set the ApplicationConfiguration for this instance.
     *
     * @param anApplicationConfiguration The ApplicationConfiguration for this instance.
     */
    protected void setApplicationConfiguration( ApplicationConfiguration anApplicationConfiguration ) {
        applicationConfiguration = anApplicationConfiguration;
    }    

    /**
     * Get the database driver classname to use for this connection.
     *
     * @return The classname of the database driver to use for this connection.
     */
    protected String getDatabaseDriver() {
        return getApplicationConfiguration().getValueOf(
                ApplicationConfigurationParameter.DB_DRIVER);
    }

    /**
     * Get the JDBC connection string to use for this connection.
     *
     * @return The JDBC connection string  to use for this connection.
     */
    protected String getDatabaseUrl() {
        return getApplicationConfiguration().getValueOf(
                ApplicationConfigurationParameter.DB_URL);
    }

    /**
     * Get the database username for this database connection.
     *
     * @return The database username for this connection.
     */
    protected String getDatabaseUsername() {
        return getApplicationConfiguration().getValueOf(
                ApplicationConfigurationParameter.DB_USERNAME);
    }

    /**
     * Get the database password for this connection.
     *
     * @return The database password for this connection.
     */
    protected String getDatabasePassword() {
        return getApplicationConfiguration().getValueOf(
                ApplicationConfigurationParameter.DB_PASSWORD);
    }
}
