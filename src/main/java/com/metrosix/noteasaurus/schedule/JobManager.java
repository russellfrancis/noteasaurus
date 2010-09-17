package com.metrosix.noteasaurus.schedule;

import com.metrosix.noteasaurus.hibernate.Executable;

/**
 * Define an interface which can be used to manage periodic jobs which should be run for system maintenance or other
 * reasons.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: JobManager.java 247 2010-08-07 23:15:10Z adam $
 */
public interface JobManager {

    /**
     * Start the JobManager.
     */
    public void start();

    /**
     * Stop the JobManager.
     */
    public void stop();

    /**
     * Add a periodic task to be run by the job manager according to the provided cron expression.
     *
     * @param name The name of this periodic task.  This is soley for human readable identification a nice name for this
     * task.
     * @param delegateClass The Class instance of the Executable instance which should be run periodically.
     * @param cronExpression A cron expression indicating when this task should be executed.
     */
    public void addPersistenceWrappedJob(String name, Class<? extends Executable> delegateClass, String cronExpression)
    throws Exception;
    
}
