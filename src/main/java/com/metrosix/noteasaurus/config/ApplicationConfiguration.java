package com.metrosix.noteasaurus.config;

import org.picocontainer.Startable;

/**
 * This class defines an interface for the ApplicationConfiguration instance.
 * 
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ApplicationConfiguration.java 247 2010-08-07 23:15:10Z adam $
 */
public interface ApplicationConfiguration extends Startable {
    
    /**
     * This method can be used get get the value of a particular configuration
     * property.
     * 
     * @param propertyName The name of the property whose value we wish to 
     * receive.
     * @return The value associated with the parameter propertyName, or null if
     * no such property is defined in the configuration file.
     */
    public String getValueOf( ApplicationConfigurationParameter propertyName  );
}
