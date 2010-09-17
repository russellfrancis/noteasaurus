package com.metrosix.noteasaurus.config.impl;

import com.metrosix.noteasaurus.config.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a default implementation of the {@link ApplicationConfiguration} interface which reads
 * its configuration properties from the /config.properties file found somewhere in the classpath.  
 * The file is searched according to the rules of the {@see Class#getResourceAsStream(String)} 
 * method.
 * 
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: NonVolatileApplicationConfiguration.java 247 2010-08-07 23:15:10Z adam $
 */
public class NonVolatileApplicationConfiguration implements ApplicationConfiguration {
    static private final Logger log = LoggerFactory.getLogger(NonVolatileApplicationConfiguration.class);
    private Properties configProperties = new Properties();

    /**
     * This method is called to bring the ApplicationConfiguration into service.
     * This method should be managed by pico-container.
     */
    public void start() {
        InputStream inputStream = getConfigurationInputStream();
        try {
            getConfigProperties().load(inputStream);
        } 
        catch (IOException e) {
            RuntimeException ex = new RuntimeException(
                    "Unable to load configuration properties: " + e.toString());
            ex.initCause(e);
            throw ex;
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                log.warn("Unable to close input stream: " + e.toString());
            }
        }
    }
    
    /**
     * This method is called to bring the ApplicationConfiguration instance out
     * of service.  This should be managed by pico-container.
     */
    public void stop() {
        getConfigProperties().clear();
    }

    /**
     * Get the value associated with the propertyName parameter.
     * 
     * @param property The name of the property we wish to look up.
     * @return null if a property by the propertyName could not be found otherwise
     * the value associated with the propertyName.
     */
    public String getValueOf(ApplicationConfigurationParameter parameter) {
        if (parameter.isVolatile()) {
            throw new IllegalArgumentException("The parameter '" + parameter.getKey() + "' must be non-volatile.");
        }
        return getConfigProperties().getProperty(parameter.getKey());
    }
    
    /**
     * This method will return an InputStream to the configuration file for
     * this instance.
     * 
     * @return An InputStream for the configuration file for this instance.
     */
    protected InputStream getConfigurationInputStream() {
        return getClass().getResourceAsStream("/config.properties");
    }

    /**
     * Get the config properties for this instance.
     * 
     * @return The configuration properties for this instance.
     */
    protected Properties getConfigProperties() {
        return configProperties;
    }
}
