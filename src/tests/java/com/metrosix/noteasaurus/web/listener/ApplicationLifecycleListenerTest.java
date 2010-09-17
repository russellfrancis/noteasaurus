package com.metrosix.noteasaurus.web.listener;

import com.metrosix.noteasaurus.web.listener.ApplicationLifecycleListener;
import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.impl.DefaultApplicationConfiguration;
import com.metrosix.noteasaurus.config.impl.NonVolatileApplicationConfiguration;
import com.metrosix.noteasaurus.config.impl.VolatileApplicationConfiguration;
import com.metrosix.noteasaurus.database.ConnectionManager;
import com.metrosix.noteasaurus.database.DatabaseManager;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.database.SQLTranslator;
import com.metrosix.noteasaurus.database.SchemaManager;
import com.metrosix.noteasaurus.database.impl.DefaultConnectionManager;
import com.metrosix.noteasaurus.database.impl.DefaultDatabaseManager;
import com.metrosix.noteasaurus.database.impl.DefaultPersistenceManager;
import com.metrosix.noteasaurus.database.impl.DefaultSchemaManager;
import com.metrosix.noteasaurus.database.impl.translation.DefaultSQLTranslator;
import com.metrosix.noteasaurus.database.impl.translation.DerbyTranslator;
import com.metrosix.noteasaurus.database.impl.translation.MySQLTranslator;
import com.metrosix.noteasaurus.database.impl.translation.PostgreSQLTranslator;
import com.metrosix.noteasaurus.domain.Note;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.domain.util.CorkboardUtility;
import com.metrosix.noteasaurus.domain.util.PersonUtility;
import com.metrosix.noteasaurus.hibernate.HibernateSessionWrapper;
import com.metrosix.noteasaurus.rpc.RequestExecutor;
import com.metrosix.noteasaurus.rpc.impl.DefaultRequestExecutor;
import com.metrosix.noteasaurus.rpc.proc.ProcedureLookup;
import com.metrosix.noteasaurus.rpc.proc.impl.DefaultProcedureLookup;
import com.metrosix.noteasaurus.rpc.proc.impl.request.ProcedureCallRequest;
import com.metrosix.noteasaurus.schedule.JobManager;
import com.metrosix.noteasaurus.schedule.impl.DefaultJobManager;
import com.metrosix.noteasaurus.schedule.impl.executable.RemoveExpiredPersonsExecutable;
import com.metrosix.noteasaurus.security.SecurityPrincipalService;
import com.metrosix.noteasaurus.util.SecureUtility;
import com.metrosix.noteasaurus.util.mail.RegistrationEmail;
import com.metrosix.noteasaurus.web.HttpSessionService;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import org.apache.velocity.app.VelocityEngine;
import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ApplicationLifecycleListenerTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class ApplicationLifecycleListenerTest {
    
    private ApplicationLifecycleListener listener;
    
    @Before
    public void setUp() {
        listener = new ApplicationLifecycleListener();
    }
    
    @After
    public void tearDown() {
        listener = null;
    }
    
    @Test
    public void testContextInitialized() throws Exception {
        DefaultPicoContainer pico = createStrictMock(DefaultPicoContainer.class);
        ServletContextEvent sce = createStrictMock(ServletContextEvent.class);
        listener = createStrictMock(ApplicationLifecycleListener.class,
                ApplicationLifecycleListener.class.getDeclaredMethod("startApplicationServices"),
                ApplicationLifecycleListener.class.getDeclaredMethod("startDependentServices",
                DefaultPicoContainer.class));

        expect(listener.startApplicationServices()).andReturn(pico);
        listener.startDependentServices(pico);

        replay(listener, sce, pico);
        listener.contextInitialized(sce);
        verify(listener, sce, pico);
    }

    @Test
    public void testContextInitialized_WithException() throws Exception {
        DefaultPicoContainer pico = createStrictMock(DefaultPicoContainer.class);
        ServletContextEvent sce = createStrictMock(ServletContextEvent.class);
        listener = createStrictMock(ApplicationLifecycleListener.class,
                ApplicationLifecycleListener.class.getDeclaredMethod("startApplicationServices"),
                ApplicationLifecycleListener.class.getDeclaredMethod("startDependentServices",
                DefaultPicoContainer.class));

        expect(listener.startApplicationServices()).andThrow(new NamingException("Yikes"));

        replay(listener, sce, pico);
        listener.contextInitialized(sce);
        verify(listener, sce, pico);
    }
    
    @Test
    public void testContextDestroyed() {
        ServletContextEvent sce = createStrictMock(ServletContextEvent.class);
        replay(sce);
        listener.contextDestroyed(sce);
        verify(sce);    
    }

    @Test
    public void testContextDestroyed_WithException() throws SecurityException, NoSuchMethodException, NamingException {
        DefaultPicoContainer pico = createStrictMock(DefaultPicoContainer.class);
        InitialContext initialContext = createStrictMock(InitialContext.class);
        ServletContextEvent sce = createStrictMock(ServletContextEvent.class);
        listener = createStrictMock(ApplicationLifecycleListener.class,
                ApplicationLifecycleListener.class.getDeclaredMethod("newInitialContext"));

        expect(listener.newInitialContext()).andReturn(initialContext);
        expect(initialContext.lookup(PicoContainer.class.getName())).andReturn(pico);
        initialContext.unbind(PicoContainer.class.getName());
        pico.stop();
        pico.dispose();

        replay(listener, sce, initialContext, pico);

        listener.contextDestroyed(sce);

        verify(listener, sce, initialContext, pico);
    }

    @Test
    public void testNewDefaultPicoContainer() {
        DefaultPicoContainer pico = listener.newDefaultPicoContainer();
        assertNotNull(pico);
    }

    @Test
    public void testCreatePicoContainer() throws Exception {
        DefaultPicoContainer pico = createStrictMock(DefaultPicoContainer.class);
        InitialContext initialContext = createStrictMock(InitialContext.class);
        listener = createStrictMock(ApplicationLifecycleListener.class,
                ApplicationLifecycleListener.class.getDeclaredMethod("newDefaultPicoContainer"),
                ApplicationLifecycleListener.class.getDeclaredMethod("newInitialContext"));

        expect(listener.newDefaultPicoContainer()).andReturn(pico);
        expect(listener.newInitialContext()).andReturn(initialContext);
        initialContext.bind(PicoContainer.class.getName(), pico);

        replay(listener, initialContext, pico);
        Object result = listener.createPicoContainer();
        assertEquals(result, pico);
        verify(listener, initialContext, pico);
    }

    @Test
    public void testStartApplicationServices() throws Exception {
        DefaultPicoContainer pico = createStrictMock(DefaultPicoContainer.class);
        listener = createStrictMock(ApplicationLifecycleListener.class,
                ApplicationLifecycleListener.class.getDeclaredMethod("createPicoContainer"),
                ApplicationLifecycleListener.class.getDeclaredMethod("bindApplicationServices",
                MutablePicoContainer.class));

        expect(listener.createPicoContainer()).andReturn(pico);
        listener.bindApplicationServices(pico);
        pico.start();

        replay(listener, pico);
        Object result = listener.startApplicationServices();
        assertEquals(pico, result);
        verify(listener, pico);
    }

    @Test
    public void testStartDependentServices() {
        Set<Class> persistentClasses = new LinkedHashSet<Class>();
        persistentClasses.add(Person.class);
        persistentClasses.add(Note.class);
        PersistenceManager persistenceManager = createStrictMock(PersistenceManager.class);
        DefaultPicoContainer pico = createStrictMock(DefaultPicoContainer.class);

        expect(pico.getComponent(PersistenceManager.class)).andReturn(persistenceManager);
        expect(persistenceManager.getPersistentClasses()).andReturn(persistentClasses);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(Person.class)).andReturn(pico);
        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(Note.class)).andReturn(pico);

        replay(persistenceManager, pico);
        listener.startDependentServices(pico);
        verify(persistenceManager, pico);
    }

    @Test
    public void testBindApplicationServices() throws Exception {
        listener = createStrictMock(ApplicationLifecycleListener.class,
                ApplicationLifecycleListener.class.getDeclaredMethod("newVelocityEngine"));

        VelocityEngine ve = createStrictMock(VelocityEngine.class);
        MutablePicoContainer pico = createStrictMock(MutablePicoContainer.class);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(NonVolatileApplicationConfiguration.class, NonVolatileApplicationConfiguration.class))
                .andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(VolatileApplicationConfiguration.class, VolatileApplicationConfiguration.class))
                .andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(ApplicationConfiguration.class, DefaultApplicationConfiguration.class))
                .andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(ConnectionManager.class, DefaultConnectionManager.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(SchemaManager.class, DefaultSchemaManager.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(PersistenceManager.class, DefaultPersistenceManager.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(DatabaseManager.class, DefaultDatabaseManager.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(RequestExecutor.class, DefaultRequestExecutor.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(SecurityPrincipalService.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(HttpSessionService.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(ProcedureLookup.class, DefaultProcedureLookup.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(JobManager.class, DefaultJobManager.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(HibernateSessionWrapper.class)).andReturn(pico);

        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(Scheduler.class, StdSchedulerFactory.getDefaultScheduler())).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(RemoveExpiredPersonsExecutable.class)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(ProcedureCallRequest.class)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(CorkboardUtility.class)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(PersonUtility.class)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(SecureUtility.class)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(PostgreSQLTranslator.class)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(MySQLTranslator.class)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(DerbyTranslator.class)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(SQLTranslator.class, DefaultSQLTranslator.class)).andReturn(pico);

        expect(listener.newVelocityEngine()).andReturn(ve);
        ve.setProperty((String) EasyMock.anyObject(),(Object) EasyMock.anyObject());
        ve.setProperty((String) EasyMock.anyObject(), EasyMock.anyObject());
        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();
        expect(pico.as(Characteristics.CACHE)).andReturn(pico);
        expect(pico.addComponent(VelocityEngine.class, ve)).andReturn(pico);

        expect(pico.as(Characteristics.NO_CACHE)).andReturn(pico);
        expect(pico.addComponent(RegistrationEmail.class)).andReturn(pico);

        replay(listener, pico, ve);
        listener.bindApplicationServices(pico);
        verify(listener, pico, ve);
    }
}
