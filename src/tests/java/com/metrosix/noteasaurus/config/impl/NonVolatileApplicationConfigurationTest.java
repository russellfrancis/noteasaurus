package com.metrosix.noteasaurus.config.impl;

import com.metrosix.noteasaurus.config.ApplicationConfigurationParameter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.easymock.classextension.ConstructorArgs;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class NonVolatileApplicationConfigurationTest {

    @Test
    public void testStartStopAndSimpleLookup() throws SecurityException, NoSuchMethodException {
        final String USERNAME = "RUSS";
        InputStream ins = new ByteArrayInputStream(
                (ApplicationConfigurationParameter.DB_USERNAME.getKey() + "=" + USERNAME).getBytes() );
        
        ConstructorArgs constructorArguments = new ConstructorArgs(
                NonVolatileApplicationConfiguration.class.getConstructor());
        
        NonVolatileApplicationConfiguration instance = createMock(
                NonVolatileApplicationConfiguration.class, constructorArguments,
                NonVolatileApplicationConfiguration.class.getDeclaredMethod("getConfigurationInputStream"));

        expect(instance.getConfigurationInputStream()).andReturn( ins );
        
        replay(instance);
        instance.start();
        assertEquals(USERNAME, instance.getValueOf(ApplicationConfigurationParameter.DB_USERNAME));
        instance.stop();
        assertNull(instance.getValueOf(ApplicationConfigurationParameter.DB_USERNAME));
        verify(instance);
    }
    
    @Test
    public void testStart_WithException() throws SecurityException, NoSuchMethodException, IOException {
        final String USERNAME = "RUSS";
        InputStream ins = new ByteArrayInputStream(
                (ApplicationConfigurationParameter.DB_USERNAME.getKey() + "=" + USERNAME).getBytes() );
        
        ConstructorArgs constructorArguments = new ConstructorArgs(
                NonVolatileApplicationConfiguration.class.getConstructor());
        
        NonVolatileApplicationConfiguration instance = createMock(
                NonVolatileApplicationConfiguration.class, constructorArguments,
                NonVolatileApplicationConfiguration.class.getDeclaredMethod("getConfigurationInputStream"),
                NonVolatileApplicationConfiguration.class.getDeclaredMethod("getConfigProperties"));
        Properties configProperties = createMock(Properties.class);

        expect(instance.getConfigurationInputStream()).andReturn(ins);
        expect(instance.getConfigProperties()).andReturn(configProperties);
        configProperties.load(ins);
        expectLastCall().andThrow(new IOException("Yikes!"));
        
        replay(instance, configProperties);
        
        try {
            instance.start();
            fail();
        } catch (RuntimeException e) {
            // success
        }
        
        verify(instance, configProperties);
    }
    
    @Test
    public void testStart_ExceptionInFinally() throws SecurityException, NoSuchMethodException, IOException {
        InputStream ins = createMock( InputStream.class );
        
        ConstructorArgs constructorArguments = new ConstructorArgs(
                NonVolatileApplicationConfiguration.class.getConstructor());
        
        NonVolatileApplicationConfiguration instance = createMock(
                NonVolatileApplicationConfiguration.class, constructorArguments,
                NonVolatileApplicationConfiguration.class.getDeclaredMethod("getConfigurationInputStream"),
                NonVolatileApplicationConfiguration.class.getDeclaredMethod("getConfigProperties"));
        Properties configProperties = createMock(Properties.class);

        expect(instance.getConfigurationInputStream()).andReturn(ins);
        expect(instance.getConfigProperties()).andReturn(configProperties);
        configProperties.load(ins);
        ins.close();
        expectLastCall().andThrow(new IOException("Yikes!"));
        
        replay(instance, configProperties, ins);
        
        instance.start();
        
        verify(instance, configProperties, ins);
    }
    
    @Test
    public void testGetConfigurationInputStream() {
        NonVolatileApplicationConfiguration config = new NonVolatileApplicationConfiguration();
        assertNotNull(config.getConfigurationInputStream());
    }
}