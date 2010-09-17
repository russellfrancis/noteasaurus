package com.metrosix.noteasaurus.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * An interface for a Job which will invoke a provided Executable instance and ensuring that that executable has access
 * to a persistence session and taking care to commit any pending work after the Executable is run.  The Executable
 * which should be run must be provided in the JobDataMap within the JobExecutionContext under the key defined by the
 * DELEGATE_CLASS variable. PicoContainer will be consulted for an instance of this class and that instance will be
 * executed with a persistence session wrapped around it.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public interface PersistenceWrappedExecutableJob extends Job {
    static public final String DELEGATE_CLASS = "delegateClass";

    /**
     * Execute this executable.
     *
     * @param context The JobExecutionContext which must contain a JobDataMap which has an entry for DELEGATE_CLASS.
     * This should reference a Class instance of the Executable which should be wrapped and then executed.
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException;
}
