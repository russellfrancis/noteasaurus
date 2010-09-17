package com.metrosix.noteasaurus.schedule.impl.job;

import com.metrosix.noteasaurus.hibernate.Executable;
import com.metrosix.noteasaurus.hibernate.HibernateSessionWrapper;
import com.metrosix.noteasaurus.schedule.PersistenceWrappedExecutableJob;
import com.metrosix.noteasaurus.util.PicoContainerFactory;
import org.picocontainer.PicoContainer;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The following class implements a PersistenceWrappedExecutableJob which will wrap the configured job in a persistence
 * session.  We can schedule this Job to run and provide a Class instance in the JobDataMap associated with the
 * PersistenceWrappedExecutableJob.DELEGATE_CLASS key.  An instance of the delegate whic must be of type Executable will
 * be retrieved from the PicoContainer and a hibernate session will be created and closed around the execution of that
 * object.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: HibernateWrappedExecutableJob.java 247 2010-08-07 23:15:10Z adam $
 */
public class HibernateWrappedExecutableJob implements PersistenceWrappedExecutableJob {

    static private final Logger log = LoggerFactory.getLogger(HibernateWrappedExecutableJob.class);

    /**
     * The JobExecutionContext must contain a Class<Executable> instance which will be used to grab an instance from
     * PicoContainer.  That instance will be wrapped in a hibernate session and then executed.  This will allow the
     * configured executable to interact with our persistent store.
     *
     * @param context The JobExecutionContext with which we should run.
     * @throws org.quartz.JobExecutionException
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {
        HibernateSessionWrapper wrapper = getPicoContainer().getComponent(HibernateSessionWrapper.class);

        JobDetail jobDetail = context.getJobDetail();
        JobDataMap dataMap = jobDetail.getJobDataMap();
        Class<Executable> delegateClass = (Class<Executable>)dataMap.get(DELEGATE_CLASS);

        try {
            Executable delegate = getPicoContainer().getComponent(delegateClass);
            wrapper.execute(delegate);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Unable to run periodic task: " + e.toString(), e);
            }

            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            if (e instanceof JobExecutionException) {
                throw (JobExecutionException)e;
            }

            throw new JobExecutionException(e);
        }
    }

    /**
     * Grab an instance of our PicoContainer.
     *
     * @return An instance of the PicoContainer.
     */
    protected PicoContainer getPicoContainer() {
        return PicoContainerFactory.getPicoContainer();
    }
}
