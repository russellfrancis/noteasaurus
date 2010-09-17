package com.metrosix.noteasaurus.database.impl;

import com.metrosix.noteasaurus.database.impl.DefaultPersistenceManager;
import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.impl.NonVolatileApplicationConfiguration;
import com.metrosix.noteasaurus.database.ConnectionManager;
import org.easymock.classextension.ConstructorArgs;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DefaultPersistenceManagerTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class DefaultPersistenceManagerTest {

    @Test
    public void testGetTransaction() throws SecurityException, NoSuchMethodException {
        ConstructorArgs args = new ConstructorArgs(
                DefaultPersistenceManager.class.getConstructor(
                    ConnectionManager.class, NonVolatileApplicationConfiguration.class), null, null);
        DefaultPersistenceManager pm = createMock(DefaultPersistenceManager.class, args,
                DefaultPersistenceManager.class.getDeclaredMethod("setApplicationConfiguration",ApplicationConfiguration.class),
                DefaultPersistenceManager.class.getDeclaredMethod("setConnectionManager", ConnectionManager.class),
                DefaultPersistenceManager.class.getDeclaredMethod("getSession"));
        Session session = createMock(Session.class);
        Transaction transaction = createMock(Transaction.class);

        expect(pm.getSession()).andReturn(session);
        expect(session.getTransaction()).andReturn(transaction);

        replay(transaction, session, pm);

        pm.setConnectionManager(null);
        pm.setApplicationConfiguration(null);
        assertEquals(transaction, pm.getTransaction());

        verify(pm, session, transaction);
    }

    @Test
    public void testConfigureWhenSessionFactoryExists() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultPersistenceManager.class.getConstructor(
                    ConnectionManager.class, NonVolatileApplicationConfiguration.class), null, null);
        DefaultPersistenceManager pm = createMock(DefaultPersistenceManager.class, args,
                DefaultPersistenceManager.class.getDeclaredMethod("setApplicationConfiguration",ApplicationConfiguration.class),
                DefaultPersistenceManager.class.getDeclaredMethod("setConnectionManager", ConnectionManager.class),
                DefaultPersistenceManager.class.getDeclaredMethod("getSessionFactory"));
        SessionFactory sessionFactory = createMock(SessionFactory.class);

        expect(pm.getSessionFactory()).andReturn(sessionFactory);

        replay(pm, sessionFactory);

        pm.setConnectionManager(null);
        pm.setApplicationConfiguration(null);
        assertTrue(pm.configure());

        verify(pm, sessionFactory);
    }

    @Test
    public void testGetSession_NullSessionFactory_ThrowsException() {
        DefaultPersistenceManager pm = new DefaultPersistenceManager(null, null);
        try {
            pm.getSession();
            fail();
        } catch (HibernateException e) {
            // success, we haven't been configured yet!
        }
    }

    @Test
    public void testGetSession() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultPersistenceManager.class.getConstructor(
                    ConnectionManager.class, NonVolatileApplicationConfiguration.class), null, null);
        DefaultPersistenceManager pm = createMock(DefaultPersistenceManager.class, args,
                DefaultPersistenceManager.class.getDeclaredMethod("setApplicationConfiguration",ApplicationConfiguration.class),
                DefaultPersistenceManager.class.getDeclaredMethod("setConnectionManager", ConnectionManager.class),
                DefaultPersistenceManager.class.getDeclaredMethod("getSessionFactory"));
        SessionFactory sessionFactory = createMock(SessionFactory.class);
        org.hibernate.classic.Session session = createMock(org.hibernate.classic.Session.class);

        expect(pm.getSessionFactory()).andReturn(sessionFactory);
        expect(sessionFactory.getCurrentSession()).andReturn(session);
        expect(session.beginTransaction()).andReturn(null);

        replay(sessionFactory, session, pm);

        pm.setConnectionManager(null);
        pm.setApplicationConfiguration(null);
        assertEquals(session, pm.getSession());

        verify(sessionFactory, session, pm);
    }
}