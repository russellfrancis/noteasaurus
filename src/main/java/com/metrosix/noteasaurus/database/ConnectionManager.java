package com.metrosix.noteasaurus.database;

import java.sql.Connection;
import java.sql.SQLException;
import org.picocontainer.Startable;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ConnectionManager.java 247 2010-08-07 23:15:10Z adam $
 */
public interface ConnectionManager extends Startable {
    /**
     * This method can be used to grab a {@link Connection} to a database which has been configured
     * and whose properties are stored in the {@link NonVolatileApplicationConfiguration} instance for this
     * application.  The connection instance returned is an open connection to the database and as
     * such, must be closed to prevent the potential of connection leaks from occuring.
     * 
     * @return A Connection instance for the currently configured database or null if the
     * application has not yet been configured.
     * @throws java.sql.SQLException If the database is not configured or there is a problem
     * connecting to it.
     * @throws java.lang.ClassNotFoundException If the database driver could not be found.
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException;
}
