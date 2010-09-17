package com.metrosix.noteasaurus.database.impl;

import com.metrosix.noteasaurus.database.ConnectionManager;
import com.metrosix.noteasaurus.database.DatabaseManager;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.database.SchemaManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a basic database manager which can startup and shutdown a database, this
 * is primarily used for embedded database systems such as Derby and H2.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultDatabaseManager implements DatabaseManager {
    
    static private final Logger log = LoggerFactory.getLogger(DefaultDatabaseManager.class);
    static protected final String DERBY_PRODUCT_NAME = "Apache Derby";
    
    private PersistenceManager persistenceManager;
    private SchemaManager schemaManager;
    private ConnectionManager connectionManager;

    /**
     * Construct a new DefaultDatabaseManager.
     *
     * @param aPersistenceManager The persistence manager to use.
     * @param aSchemaManager The schema manager to use.
     * @param aConnectionManager The connection manager to use.
     */
    public DefaultDatabaseManager(
            PersistenceManager aPersistenceManager, 
            SchemaManager aSchemaManager,
            ConnectionManager aConnectionManager)
    {
        setPersistenceManager(aPersistenceManager);
        setSchemaManager(aSchemaManager);
        setConnectionManager(aConnectionManager);
    }

    /**
     * This method will start the database, which includes installing the schema and creating a
     * hibernate session factory via the persistence manager.
     */
    public void start() {
        try {
            if(getSchemaManager().install()){
                if(getPersistenceManager().configure()) {
                    log.info("Successfully started the database.");
                }
            }
        }
        catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            RuntimeException ex = new RuntimeException(e);
            throw ex;
        }
     }

    /**
     * This method will stop the database.
     */
    public void stop() {
        try {
            if (isDatabaseDerby()) {
                try {
                    Connection conn = getShutdownConnection();
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    log.info("Successfully shutdown embedded derby database.");
                }
            }
        } catch(RuntimeException e) {
            throw e;
        } catch (Exception e) {
            RuntimeException ex = new RuntimeException(e);
            throw ex;
        }
    }

    protected Connection getShutdownConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:derby:;shutdown=true");
    }

    /**
     * Determine whether the database we are connected to is Derby or not.
     *
     * @return true if we are connected to a Derby database, false otherwise.
     */
    protected boolean isDatabaseDerby() throws SQLException, ClassNotFoundException 
    {            
        boolean isDerby = false;        
        Connection conn = getConnectionManager().getConnection();
        if (conn != null) {
            try {
                DatabaseMetaData dbmd = conn.getMetaData();
                String productName = dbmd.getDatabaseProductName();
                isDerby = DefaultDatabaseManager.DERBY_PRODUCT_NAME.equals(productName);
            } finally {
                conn.close();
            }
        }
        return isDerby;
    }

    /**
     * Get the PersistenceManager for this instance.
     *
     * @return The PersistenceManager for this instance.
     */
    protected PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    /**
     * Set the PersistenceManager for this instance.
     *
     * @param aPersistenceManager The PersistenceManager for this instance.
     */
    protected void setPersistenceManager( PersistenceManager aPersistenceManager ) {
        persistenceManager = aPersistenceManager;
    }

    /**
     * Get the SchemaManager for this instance.
     *
     * @return The SchemaManager for this instance.
     */
    protected SchemaManager getSchemaManager() {
        return schemaManager;
    }

    /**
     * Set the SchemaManager for this instance.
     *
     * @param aSchemaManager The SchemaManager for this instance.
     */
    protected void setSchemaManager( SchemaManager aSchemaManager ) {
        schemaManager = aSchemaManager;
    }

    /**
     * Get the ConnectionManager for this instance.
     *
     * @return The ConnectionManager for this instance.
     */
    protected ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Set the ConnectionManager for this instance.
     *
     * @param connectionManager The ConnectionManager for this instance.
     */
    protected void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
}
