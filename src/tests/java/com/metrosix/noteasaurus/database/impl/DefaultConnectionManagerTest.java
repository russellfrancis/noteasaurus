package com.metrosix.noteasaurus.database.impl;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.impl.NonVolatileApplicationConfiguration;
import com.metrosix.noteasaurus.database.ConnectionManager;
import java.sql.Connection;
import java.sql.SQLException;
import org.easymock.classextension.ConstructorArgs;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultConnectionManagerTest {

    @Test
    public void testStartAndStop() {
        ConnectionManager cm = new DefaultConnectionManager(null);
        cm.start();
        cm.stop();
    }

    @Test
    public void testGetConnectionWith2Parameters() throws NoSuchMethodException, SQLException, ClassNotFoundException {
        ConstructorArgs args = new ConstructorArgs(
                DefaultConnectionManager.class.getConstructor(
                    NonVolatileApplicationConfiguration.class),
                    (NonVolatileApplicationConfiguration)null );

        Connection conn = createMock(Connection.class);
        DefaultConnectionManager connectionManager = createMock( DefaultConnectionManager.class, args,
                DefaultConnectionManager.class.getDeclaredMethod("setApplicationConfiguration", ApplicationConfiguration.class),
                DefaultConnectionManager.class.getDeclaredMethod("getConnection", String.class, String.class, String.class, String.class) );

        expect(connectionManager.getConnection("DRIVER", "URL", null, null)).andReturn(conn);

        replay(conn, connectionManager);

        connectionManager.setApplicationConfiguration(null);
        assertEquals(conn, connectionManager.getConnection("DRIVER", "URL"));

        verify(conn, connectionManager);
    }

    @Test
    public void testGetConnectionWith4Parameters_InvalidParameter1_ThrowException() throws ClassNotFoundException {
        DefaultConnectionManager cm = new DefaultConnectionManager(null);
        try {
            // The empty first parameter should throw a SQLException.
            cm.getConnection("", "URL", null, null);
            fail();
        } catch (SQLException e) {
            // success
        }
    }

    @Test
    public void testGetConnectionWith4Parameters_InvalidParameter2_ThrowException() throws ClassNotFoundException {
        DefaultConnectionManager cm = new DefaultConnectionManager(null);
        try {
            // The empty first parameter should throw a SQLException.
            cm.getConnection("db.driver", null, null, null);
            fail();
        } catch (SQLException e) {
            // success
        }
    }

    @Test
    public void testGetConnectionWith4Parameters_InvalidDriver_ThrowException() throws SQLException {
        DefaultConnectionManager cm = new DefaultConnectionManager(null);
        try {
            // The empty first parameter should throw a SQLException.
            cm.getConnection("db.driver", "db.url", null, null);
            fail();
        } catch (ClassNotFoundException e) {
            // success
        }
    }

    @Test
    public void testGetConnectionWith4Parameters_ValidDriver() throws SQLException, NoSuchMethodException, ClassNotFoundException {
        ConstructorArgs args = new ConstructorArgs(
                DefaultConnectionManager.class.getConstructor(
                    NonVolatileApplicationConfiguration.class),
                    (NonVolatileApplicationConfiguration)null );

        Connection conn = createMock(Connection.class);
        DefaultConnectionManager cm = createMock( DefaultConnectionManager.class, args,
                DefaultConnectionManager.class.getDeclaredMethod("setApplicationConfiguration", ApplicationConfiguration.class),
                DefaultConnectionManager.class.getDeclaredMethod("getDriverManagerGetConnection", String.class, String.class, String.class) );

        expect(cm.getDriverManagerGetConnection("db.url", null, null)).andReturn(conn);

        replay(cm, conn);

        cm.setApplicationConfiguration(null);
        assertEquals(conn, cm.getConnection("org.apache.derby.jdbc.EmbeddedDriver", "db.url", null, null));

        verify(cm, conn);
    }
}