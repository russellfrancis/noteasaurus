package com.metrosix.noteasaurus.schedule.impl;

import com.metrosix.noteasaurus.schedule.impl.executable.RemoveExpiredPersonsExecutable;
import com.metrosix.noteasaurus.schedule.impl.job.HibernateWrappedExecutableJob;
import java.util.Date;
import org.junit.Test;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DefaultJobManagerTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class DefaultJobManagerTest {

    @Test
    public void testStart() throws Exception {
        Scheduler scheduler = createStrictMock(Scheduler.class);
        DefaultJobManager jobManager = createStrictMock(DefaultJobManager.class,
                DefaultJobManager.class.getDeclaredMethod(
                "addPersistenceWrappedJob", String.class, Class.class, String.class));
        jobManager.setScheduler(scheduler);

        // Set expectations.
        scheduler.start();
        jobManager.addPersistenceWrappedJob(
                "Remove Expired Persons",
                RemoveExpiredPersonsExecutable.class,
                "0 0 * * * ?");

        replay(scheduler, jobManager);

        // Run the test
        jobManager.start();

        // Verify expectations
        verify(scheduler, jobManager);
    }

    @Test
    public void testStart_WithException() throws Exception {
        Scheduler scheduler = createStrictMock(Scheduler.class);
        DefaultJobManager jobManager = createStrictMock(DefaultJobManager.class,
                DefaultJobManager.class.getDeclaredMethod(
                "addPersistenceWrappedJob", String.class, Class.class, String.class));
        jobManager.setScheduler(scheduler);

        // Set expectations.
        scheduler.start();
        expectLastCall().andThrow(new RuntimeException("Yikes!"));
        replay(scheduler, jobManager);

        // Run the test
        jobManager.start();

        // Verify expectations
        verify(scheduler, jobManager);
    }

    @Test
    public void testStop() throws Exception {
        Scheduler scheduler = createStrictMock(Scheduler.class);
        DefaultJobManager jobManager = new DefaultJobManager(scheduler);

        scheduler.shutdown();

        replay(scheduler);
        jobManager.stop();
        verify(scheduler);
    }

    @Test
    public void testStop_WithException() throws Exception {
        Scheduler scheduler = createStrictMock(Scheduler.class);
        DefaultJobManager jobManager = new DefaultJobManager(scheduler);

        scheduler.shutdown();
        expectLastCall().andThrow(new SchedulerException("Yikes!"));

        replay(scheduler);
        jobManager.stop();
        verify(scheduler);
    }

    @Test
    public void testAddPersistenceWrappedJob() throws Exception {
        String name = "NAME";
        Class delegateClass = RemoveExpiredPersonsExecutable.class;
        String cronExpression = "0 0 * * * ?";

        JobDetail jobDetail = createStrictMock(JobDetail.class);
        JobDataMap jobDataMap = createStrictMock(JobDataMap.class);
        CronTrigger trigger = createStrictMock(CronTrigger.class);
        Scheduler scheduler = createStrictMock(Scheduler.class);
        DefaultJobManager jobManager = createStrictMock(DefaultJobManager.class,
                DefaultJobManager.class.getDeclaredMethod("newJobDetail", String.class, String.class),
                DefaultJobManager.class.getDeclaredMethod("newCronTrigger", String.class, String.class));
        jobManager.setScheduler(scheduler);

        expect(jobManager.newJobDetail(name, DefaultJobManager.JOB_THREAD_GROUP_NAME)).andReturn(jobDetail);
        expect(jobManager.newCronTrigger(name, DefaultJobManager.JOB_THREAD_GROUP_NAME)).andReturn(trigger);
        expect(jobDetail.getJobDataMap()).andReturn(jobDataMap);
        expect(jobDataMap.put(HibernateWrappedExecutableJob.DELEGATE_CLASS, delegateClass)).andReturn(null);
        trigger.setCronExpression(cronExpression);
        expect(scheduler.scheduleJob(jobDetail, trigger)).andReturn(new Date());

        replay(jobManager, scheduler, trigger, jobDataMap, jobDetail);
        jobManager.addPersistenceWrappedJob(name, delegateClass, cronExpression);
        verify(jobManager, scheduler, trigger, jobDataMap, jobDetail);
    }

    @Test
    public void testNewCronTrigger() {
        DefaultJobManager jobManager = new DefaultJobManager(null);
        assertNotNull(jobManager.newCronTrigger("name", "thread group"));
    }

    @Test
    public void testNewJobDetail() {
        DefaultJobManager jobManager = new DefaultJobManager(null);
        assertNotNull(jobManager.newJobDetail("name", "thread group"));
    }
}