package com.metrosix.noteasaurus.schedule.impl.job;

import com.metrosix.noteasaurus.hibernate.HibernateSessionWrapper;
import com.metrosix.noteasaurus.schedule.PersistenceWrappedExecutableJob;
import com.metrosix.noteasaurus.schedule.impl.executable.RemoveExpiredPersonsExecutable;
import org.junit.Test;
import org.picocontainer.PicoContainer;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class HibernateWrappedExecutableJobTest {
    @Test
    public void testExecute() throws Exception {
        HibernateWrappedExecutableJob job = createStrictMock(HibernateWrappedExecutableJob.class,
                HibernateWrappedExecutableJob.class.getDeclaredMethod("getPicoContainer"));
        JobExecutionContext context = createStrictMock(JobExecutionContext.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        JobDetail jobDetail = new JobDetail();
        jobDetail.getJobDataMap().put(PersistenceWrappedExecutableJob.DELEGATE_CLASS,
                RemoveExpiredPersonsExecutable.class);
        RemoveExpiredPersonsExecutable delegate = new RemoveExpiredPersonsExecutable(null);

        expect(job.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(context.getJobDetail()).andReturn(jobDetail);
        expect(job.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(RemoveExpiredPersonsExecutable.class)).andReturn(delegate);
        wrapper.execute(delegate);

        replay(job,context,pico,wrapper);
        job.execute(context);
        verify(job,context,pico,wrapper);
    }

    @Test
    public void testExecuteWithRuntimeException() throws Exception {
        HibernateWrappedExecutableJob job = createStrictMock(HibernateWrappedExecutableJob.class,
                HibernateWrappedExecutableJob.class.getDeclaredMethod("getPicoContainer"));
        JobExecutionContext context = createStrictMock(JobExecutionContext.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        JobDetail jobDetail = new JobDetail();
        jobDetail.getJobDataMap().put(PersistenceWrappedExecutableJob.DELEGATE_CLASS,
                RemoveExpiredPersonsExecutable.class);
        RemoveExpiredPersonsExecutable delegate = new RemoveExpiredPersonsExecutable(null);

        expect(job.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(context.getJobDetail()).andReturn(jobDetail);
        expect(job.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(RemoveExpiredPersonsExecutable.class)).andReturn(delegate);
        wrapper.execute(delegate);
        RuntimeException ex = new RuntimeException();
        expectLastCall().andThrow(ex);

        replay(job,context,pico,wrapper);

        try {
            job.execute(context);
            fail("This should throw an exception.");
        } catch (RuntimeException e) {
            // success
            assertEquals(ex, e);
        }

        verify(job,context,pico,wrapper);
    }

    @Test
    public void testExecuteWithJobExecutionException() throws Exception {
        HibernateWrappedExecutableJob job = createStrictMock(HibernateWrappedExecutableJob.class,
                HibernateWrappedExecutableJob.class.getDeclaredMethod("getPicoContainer"));
        JobExecutionContext context = createStrictMock(JobExecutionContext.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        JobDetail jobDetail = new JobDetail();
        jobDetail.getJobDataMap().put(PersistenceWrappedExecutableJob.DELEGATE_CLASS,
                RemoveExpiredPersonsExecutable.class);
        RemoveExpiredPersonsExecutable delegate = new RemoveExpiredPersonsExecutable(null);

        expect(job.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(context.getJobDetail()).andReturn(jobDetail);
        expect(job.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(RemoveExpiredPersonsExecutable.class)).andReturn(delegate);
        wrapper.execute(delegate);
        JobExecutionException ex = new JobExecutionException();
        expectLastCall().andThrow(ex);

        replay(job,context,pico,wrapper);

        try {
            job.execute(context);
            fail("This should throw an exception.");
        } catch (JobExecutionException e) {
            // success
            assertEquals(ex, e);
        }

        verify(job,context,pico,wrapper);
    }

    @Test
    public void testExecuteWithOtherException() throws Exception {
        HibernateWrappedExecutableJob job = createStrictMock(HibernateWrappedExecutableJob.class,
                HibernateWrappedExecutableJob.class.getDeclaredMethod("getPicoContainer"));
        JobExecutionContext context = createStrictMock(JobExecutionContext.class);
        PicoContainer pico = createStrictMock(PicoContainer.class);
        HibernateSessionWrapper wrapper = createStrictMock(HibernateSessionWrapper.class);
        JobDetail jobDetail = new JobDetail();
        jobDetail.getJobDataMap().put(PersistenceWrappedExecutableJob.DELEGATE_CLASS,
                RemoveExpiredPersonsExecutable.class);
        RemoveExpiredPersonsExecutable delegate = new RemoveExpiredPersonsExecutable(null);

        expect(job.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(HibernateSessionWrapper.class)).andReturn(wrapper);
        expect(context.getJobDetail()).andReturn(jobDetail);
        expect(job.getPicoContainer()).andReturn(pico);
        expect(pico.getComponent(RemoveExpiredPersonsExecutable.class)).andReturn(delegate);
        wrapper.execute(delegate);
        Exception ex = new Exception();
        expectLastCall().andThrow(ex);

        replay(job,context,pico,wrapper);

        try {
            job.execute(context);
            fail("This should throw an exception.");
        } catch (JobExecutionException e) {
            // success
            assertEquals(ex, e.getCause());
        }

        verify(job,context,pico,wrapper);
    }
}
