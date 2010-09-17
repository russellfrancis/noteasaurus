package com.metrosix.noteasaurus.database.impl;

import com.metrosix.noteasaurus.database.ConnectionManager;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.database.SchemaManager;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.easymock.classextension.ConstructorArgs;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultDatabaseManagerTest {

    @Test
    public void testStop_ThrowsRuntime_ThrowsRuntime() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("isDatabaseDerby"));

        expect(dm.isDatabaseDerby()).andThrow( new RuntimeException() );

        replay(dm);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        try {
            dm.stop();
            fail();
        } catch (RuntimeException e) {
            // success
        }

        verify(dm);
    }

    @Test
    public void testStop_ThrowsException_ThrowsRuntime() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("isDatabaseDerby"));

        expect(dm.isDatabaseDerby()).andThrow( new SQLException() );

        replay(dm);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        try {
            dm.stop();
            fail();
        } catch (RuntimeException e) {
            // success
        }

        verify(dm);
    }

    @Test
    public void testStop_NotDerbyDatabase() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("isDatabaseDerby"));

        expect(dm.isDatabaseDerby()).andReturn(Boolean.FALSE);

        replay(dm);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        dm.stop();

        verify(dm);
    }

    @Test
    public void testStop_ShutdownConnectionNull() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("isDatabaseDerby"),
                DefaultDatabaseManager.class.getDeclaredMethod("getShutdownConnection"));

        expect(dm.isDatabaseDerby()).andReturn(Boolean.TRUE);
        expect(dm.getShutdownConnection()).andReturn(null);

        replay(dm);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        dm.stop();

        verify(dm);
    }

    @Test
    public void testStop_ShutdownConnectionNotNull() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("isDatabaseDerby"),
                DefaultDatabaseManager.class.getDeclaredMethod("getShutdownConnection"));
        Connection c = createMock(Connection.class);

        expect(dm.isDatabaseDerby()).andReturn(Boolean.TRUE);
        expect(dm.getShutdownConnection()).andReturn(c);
        c.close();

        replay(c, dm);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        dm.stop();

        verify(c, dm);
    }

    @Test
    public void testStart_InstallThrowsException_ThrowsRuntime() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("getSchemaManager"),
                DefaultDatabaseManager.class.getDeclaredMethod("getPersistenceManager"),
                DefaultDatabaseManager.class.getDeclaredMethod("getConnectionManager"));
        SchemaManager schemaManager = createMock(SchemaManager.class);
        PersistenceManager persistenceManager = createMock(PersistenceManager.class);

        expect(dm.getSchemaManager()).andReturn(schemaManager);
        expect(schemaManager.install()).andReturn(Boolean.TRUE);
        expect(dm.getPersistenceManager()).andReturn(persistenceManager);
        expect(persistenceManager.configure()).andThrow(new Exception());

        replay(dm, schemaManager, persistenceManager);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        try {
            dm.start();
            fail();
        } catch (RuntimeException e) {
            // Expected
        }

        verify(dm, schemaManager, persistenceManager);
    }

    @Test
    public void testStart_InstallThrowsRuntime_ThrowsRuntime() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("getSchemaManager"),
                DefaultDatabaseManager.class.getDeclaredMethod("getPersistenceManager"),
                DefaultDatabaseManager.class.getDeclaredMethod("getConnectionManager"));
        SchemaManager schemaManager = createMock(SchemaManager.class);
        PersistenceManager persistenceManager = createMock(PersistenceManager.class);

        expect(dm.getSchemaManager()).andReturn(schemaManager);
        expect(schemaManager.install()).andReturn(Boolean.TRUE);
        expect(dm.getPersistenceManager()).andReturn(persistenceManager);
        expect(persistenceManager.configure()).andThrow(new RuntimeException());

        replay(dm, schemaManager, persistenceManager);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        try {
            dm.start();
            fail();
        } catch (RuntimeException e) {
            // Expected
        }

        verify(dm, schemaManager, persistenceManager);
    }

    @Test
    public void testStart_InstallFailed() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("getSchemaManager"),
                DefaultDatabaseManager.class.getDeclaredMethod("getPersistenceManager"),
                DefaultDatabaseManager.class.getDeclaredMethod("getConnectionManager"));
        SchemaManager schemaManager = createMock(SchemaManager.class);

        expect(dm.getSchemaManager()).andReturn(schemaManager);
        expect(schemaManager.install()).andReturn(Boolean.FALSE);

        replay(dm, schemaManager);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        dm.start();

        verify(dm, schemaManager);
    }

    @Test
    public void testStart_ConfigureFailed() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("getSchemaManager"),
                DefaultDatabaseManager.class.getDeclaredMethod("getPersistenceManager"),
                DefaultDatabaseManager.class.getDeclaredMethod("getConnectionManager"));
        SchemaManager schemaManager = createMock(SchemaManager.class);
        PersistenceManager persistenceManager = createMock(PersistenceManager.class);

        expect(dm.getSchemaManager()).andReturn(schemaManager);
        expect(schemaManager.install()).andReturn(Boolean.TRUE);
        expect(dm.getPersistenceManager()).andReturn(persistenceManager);
        expect(persistenceManager.configure()).andReturn(Boolean.FALSE);

        replay(dm, schemaManager, persistenceManager);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        dm.start();

        verify(dm, schemaManager, persistenceManager);
    }

    @Test
    public void testIsDatabaseDerby_NoConnection_ReturnFalse() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultDatabaseManager.class.getConstructor( PersistenceManager.class, SchemaManager.class, ConnectionManager.class),
                null, null, null);
        DefaultDatabaseManager dm = createMock(DefaultDatabaseManager.class,args,
                DefaultDatabaseManager.class.getDeclaredMethod("setConnectionManager",ConnectionManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setSchemaManager",SchemaManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("setPersistenceManager",PersistenceManager.class),
                DefaultDatabaseManager.class.getDeclaredMethod("getConnectionManager"));
        ConnectionManager cm = createMock(ConnectionManager.class);

        expect(dm.getConnectionManager()).andReturn(cm);
        expect(cm.getConnection()).andReturn(null);

        replay(cm, dm);

        dm.setPersistenceManager(null);
        dm.setSchemaManager(null);
        dm.setConnectionManager(null);
        assertFalse(dm.isDatabaseDerby());

        verify(cm, dm);
    }

    @Test
    public void testIsDatabaseDerby_IsDerby() throws Exception {
        ConnectionManager cm = createStrictMock(ConnectionManager.class);
        Connection conn = createStrictMock(Connection.class);
        DatabaseMetaData dbmd = createStrictMock(DatabaseMetaData.class);
        DefaultDatabaseManager dm = new DefaultDatabaseManager(null, null, cm);

        expect(cm.getConnection()).andReturn(conn);
        expect(conn.getMetaData()).andReturn(dbmd);
        expect(dbmd.getDatabaseProductName()).andReturn(DefaultDatabaseManager.DERBY_PRODUCT_NAME);
        conn.close();

        replay(cm, conn, dbmd);

        assertTrue(dm.isDatabaseDerby());

        verify(cm, conn, dbmd);
    }

    @Test
    public void testIsDatabaseDerby_IsNotDerby() throws Exception {
        ConnectionManager cm = createStrictMock(ConnectionManager.class);
        Connection conn = createStrictMock(Connection.class);
        DatabaseMetaData dbmd = createStrictMock(DatabaseMetaData.class);
        DefaultDatabaseManager dm = new DefaultDatabaseManager(null, null, cm);

        expect(cm.getConnection()).andReturn(conn);
        expect(conn.getMetaData()).andReturn(dbmd);
        expect(dbmd.getDatabaseProductName()).andReturn("PostgreSQL");
        conn.close();

        replay(cm, conn, dbmd);

        assertFalse(dm.isDatabaseDerby());

        verify(cm, conn, dbmd);
    }

    @Test
    public void testIsDatabaseDerby_Failure() throws Exception {
        ConnectionManager cm = createStrictMock(ConnectionManager.class);
        Connection conn = createStrictMock(Connection.class);
        DefaultDatabaseManager dm = new DefaultDatabaseManager(null, null, cm);

        expect(cm.getConnection()).andReturn(conn);
        expect(conn.getMetaData()).andThrow(new SQLException());
        conn.close();

        replay(cm, conn);

        try {
            dm.isDatabaseDerby();
            fail();
        } catch(SQLException e) {
            // success
        }

        verify(cm, conn);
    }

    @Test
    public void testConnectionManagerAccessorAndMutator() {
        ConnectionManager cm = createMock(ConnectionManager.class);
        DefaultDatabaseManager dm = new DefaultDatabaseManager(null, null, null);

        replay(cm);

        assertNull(dm.getConnectionManager());
        dm.setConnectionManager(cm);
        assertEquals(cm, dm.getConnectionManager());

        verify(cm);
    }
}