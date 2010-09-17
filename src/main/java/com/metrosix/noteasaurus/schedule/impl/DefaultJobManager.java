package com.metrosix.noteasaurus.schedule.impl;

import com.metrosix.noteasaurus.hibernate.Executable;
import com.metrosix.noteasaurus.schedule.JobManager;
import com.metrosix.noteasaurus.schedule.impl.executable.RemoveExpiredPersonsExecutable;
import com.metrosix.noteasaurus.schedule.impl.job.HibernateWrappedExecutableJob;
import java.text.ParseException;
import org.picocontainer.Startable;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implement a JobManager which can be used to run periodic tasks within our application.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DefaultJobManager.java -1   $
 */
public class DefaultJobManager implements JobManager, Startable {

    static private final Logger log = LoggerFactory.getLogger(DefaultJobManager.class);
    static protected final String JOB_THREAD_GROUP_NAME = "Job Thread Group";

    private Scheduler scheduler;

    public DefaultJobManager(Scheduler scheduler) {
        setScheduler(scheduler);
    }

    /**
     * Start the DefaultJobManager.  This puts the JobManager into service.
     */
    public void start() {
        try {
            getScheduler().start();

            // Add our jobs.
            addPersistenceWrappedJob("Remove Expired Persons", RemoveExpiredPersonsExecutable.class, "0 0 * * * ?");
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Unable to start scheduler: " + e.toString(), e);
            }
        }
    }

    /**
     * Stop the DefaultJobManager.  This must be called before exiting the application.
     */
    public void stop() {
        try {
            getScheduler().shutdown();
        } catch (SchedulerException e) {
            if (log.isErrorEnabled()) {
                log.error("Unable to shutdown scheduler: " + e.toString(), e);
            }
        }
    }

    /**
     * Add a new periodic task to the JobManager.
     *
     * @param name A human readable name for the task.
     * @param delegateClass The Executable Class instance which should be run periodically.
     * @param cronExpression A cron expression describing when this task should be run.
     */
    public void addPersistenceWrappedJob(String name, Class<? extends Executable> delegateClass, String cronExpression)
    throws ParseException, SchedulerException {
        JobDetail jobDetail = newJobDetail(name, JOB_THREAD_GROUP_NAME);
        CronTrigger trigger = newCronTrigger(name, JOB_THREAD_GROUP_NAME);

        jobDetail.getJobDataMap().put(HibernateWrappedExecutableJob.DELEGATE_CLASS, delegateClass);
        trigger.setCronExpression(cronExpression);
        getScheduler().scheduleJob(jobDetail, trigger);
    }

    /**
     * A new job detail instance.
     *
     * @param name The name of the JobDetail instance.
     * @param threadGroup The thread group of the job Detail instance.
     * @return a new JobDetail instance.
     */
    protected JobDetail newJobDetail(String name, String threadGroup) {
        return new JobDetail(name, threadGroup, HibernateWrappedExecutableJob.class);
    }

    /**
     * Get a new CronTrigger instance.
     *
     * @param name The name of the CronTrigger.
     * @param threadGroup The ThreadGroup of the cron trigger.
     * @return A new CronTrigger instance.
     */
    protected CronTrigger newCronTrigger(String name, String threadGroup) {
        return new CronTrigger(name, threadGroup);
    }

    /**
     * Get the system job scheduler.
     *
     * @return The system job scheduler.
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * Set the system job scheduler.
     *
     * @param scheduler The system job scheduler.
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}
