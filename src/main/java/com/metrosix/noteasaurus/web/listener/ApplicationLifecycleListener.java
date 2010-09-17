package com.metrosix.noteasaurus.web.listener;

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
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.picocontainer.Characteristics;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.OptInCaching;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.velocity.app.VelocityEngine;

/**
 * This listener is run when the application is started up and also when it is stopped.  It performs
 * necessary setup of resources which are used by the application.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ApplicationLifecycleListener.java 247 2010-08-07 23:15:10Z adam $
 */
public class ApplicationLifecycleListener implements ServletContextListener {
    
    private static final Logger log = LoggerFactory.getLogger( ApplicationLifecycleListener.class );

    /**
     * Initialize resources used by the application.
     *
     * @param servletContextEvent The event which caused this method to be invoked.
     */
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            DefaultPicoContainer pico = startApplicationServices();
            startDependentServices(pico);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    /**
     * Release all of the resources used by this application.
     *
     * @param servletContextEvent The event which caused this method to be invoked.
     */
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            InitialContext initialContext = newInitialContext();
            DefaultPicoContainer pico = (DefaultPicoContainer)initialContext.lookup(
                    PicoContainer.class.getName());
            initialContext.unbind(PicoContainer.class.getName());
            pico.stop();
            pico.dispose();
        } catch (Exception e) {
            log.error( e.toString(), e );
        }
    }

    /**
     * Start application level services.
     *
     * @return An instance of the PicoContainer for this application.
     */
    protected DefaultPicoContainer startApplicationServices() throws Exception {
        DefaultPicoContainer pico = createPicoContainer();
        bindApplicationServices(pico);
        pico.start();
        return pico;
    }

    /**
     * Start dependent services.
     *
     * @param pico The PicoContainer for this application.
     */
    protected void startDependentServices(DefaultPicoContainer pico) {
        // Now that the persistence manager has been started, we can query
        // it for the persistent classes.  We want to add these to the
        // container as well.
        PersistenceManager pm = pico.getComponent(PersistenceManager.class);
        for (Class persistentClass : pm.getPersistentClasses()) {
            pico.as(Characteristics.NO_CACHE).addComponent(persistentClass);
        }
    }

    /**
     * This method will create a master pico container and bind it to JNDI for later lookup.
     *
     * @return a reference to the PicoContainer.
     */
    protected DefaultPicoContainer createPicoContainer() throws NamingException {
        DefaultPicoContainer pico = newDefaultPicoContainer();

        // Now bind the pico container to JNDI so that we can grab it from
        // other parts of the application.
        InitialContext initialContext = newInitialContext();
        initialContext.bind(PicoContainer.class.getName(), pico);

        return pico;
    }

    protected void bindApplicationServices(MutablePicoContainer pico) throws Exception {
        pico.as(Characteristics.CACHE).addComponent(
                NonVolatileApplicationConfiguration.class, NonVolatileApplicationConfiguration.class);

        pico.as(Characteristics.NO_CACHE).addComponent(
                VolatileApplicationConfiguration.class, VolatileApplicationConfiguration.class);

        pico.as(Characteristics.NO_CACHE).addComponent(
                ApplicationConfiguration.class, DefaultApplicationConfiguration.class);

        pico.as(Characteristics.CACHE).addComponent(
                ConnectionManager.class, DefaultConnectionManager.class);

        pico.as(Characteristics.CACHE).addComponent(
                SchemaManager.class, DefaultSchemaManager.class);

        pico.as(Characteristics.CACHE).addComponent(
                PersistenceManager.class, DefaultPersistenceManager.class);

        pico.as(Characteristics.CACHE).addComponent(
                DatabaseManager.class, DefaultDatabaseManager.class);

        pico.as(Characteristics.CACHE).addComponent(
                RequestExecutor.class, DefaultRequestExecutor.class);

        pico.as(Characteristics.CACHE).addComponent(
                SecurityPrincipalService.class);

        pico.as(Characteristics.CACHE).addComponent(
                HttpSessionService.class);

        pico.as(Characteristics.CACHE).addComponent(
                ProcedureLookup.class, DefaultProcedureLookup.class);

        pico.as(Characteristics.CACHE).addComponent(
                JobManager.class, DefaultJobManager.class);

        pico.as(Characteristics.CACHE).addComponent(
                HibernateSessionWrapper.class);

        pico.as(Characteristics.CACHE).addComponent(
                Scheduler.class, StdSchedulerFactory.getDefaultScheduler());

        pico.as(Characteristics.NO_CACHE).addComponent(
                RemoveExpiredPersonsExecutable.class);

        pico.as(Characteristics.NO_CACHE).addComponent(
                ProcedureCallRequest.class);

        pico.as(Characteristics.NO_CACHE).addComponent(
                CorkboardUtility.class);

        pico.as(Characteristics.NO_CACHE).addComponent(
                PersonUtility.class);

        pico.as(Characteristics.NO_CACHE).addComponent(
                SecureUtility.class);

        // SQL Translators
        pico.as(Characteristics.NO_CACHE).addComponent(
                PostgreSQLTranslator.class);
        
        pico.as(Characteristics.NO_CACHE).addComponent(
                MySQLTranslator.class);
        
        pico.as(Characteristics.NO_CACHE).addComponent(
                DerbyTranslator.class);
        
        pico.as(Characteristics.NO_CACHE).addComponent(
                SQLTranslator.class, DefaultSQLTranslator.class);

        // Velocity Template Engine
        VelocityEngine ve = newVelocityEngine();
        ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
        ve.setProperty("runtime.log.logsystem.log4j.logger", log.getName());
        ve.setProperty("resource.loader", "class");
        ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        ve.init();
        pico.as(Characteristics.CACHE).addComponent(VelocityEngine.class, ve);

        // Email Templates
        pico.as(Characteristics.NO_CACHE).addComponent(
                RegistrationEmail.class);
    }

    /**
     * Get an instance of the InitialContext for this application.
     *
     * @return An InitialContext instance connect to the default JNDI service for this application.
     */
    protected InitialContext newInitialContext() throws NamingException {
        return new InitialContext();
    }

    /**
     * Construct a new DefaultPicoContainer which allows caching.
     *
     * @return A new DefaultPicoContainer which allows caching.
     */
    protected DefaultPicoContainer newDefaultPicoContainer() {
        return new DefaultPicoContainer(new OptInCaching());
    }

    protected VelocityEngine newVelocityEngine() {
        return new VelocityEngine();
    }
}
