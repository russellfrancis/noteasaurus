package com.metrosix.noteasaurus.config.impl;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.ApplicationConfigurationParameter;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultApplicationConfiguration implements ApplicationConfiguration {

    private NonVolatileApplicationConfiguration nonVolatileApplicationConfiguration;
    private VolatileApplicationConfiguration volatileApplicationConfiguration;

    public DefaultApplicationConfiguration(
            NonVolatileApplicationConfiguration nonVolatileApplicationConfiguration,
            VolatileApplicationConfiguration volatileApplicationConfiguration)
    {
        setVolatileApplicationConfiguration(volatileApplicationConfiguration);
        setNonVolatileApplicationConfiguration(nonVolatileApplicationConfiguration);
    }

    public String getValueOf(ApplicationConfigurationParameter parameter) {
        if (parameter.isVolatile()) {
            return getVolatileApplicationConfiguration().getValueOf(parameter);
        }
        return getNonVolatileApplicationConfiguration().getValueOf(parameter);
    }

    public void start() {
    }

    public void stop() {
    }

    public NonVolatileApplicationConfiguration getNonVolatileApplicationConfiguration() {
        return nonVolatileApplicationConfiguration;
    }

    public void setNonVolatileApplicationConfiguration(NonVolatileApplicationConfiguration nonVolatileApplicationConfiguration) {
        this.nonVolatileApplicationConfiguration = nonVolatileApplicationConfiguration;
    }

    public VolatileApplicationConfiguration getVolatileApplicationConfiguration() {
        return volatileApplicationConfiguration;
    }

    public void setVolatileApplicationConfiguration(VolatileApplicationConfiguration volatileApplicationConfiguration) {
        this.volatileApplicationConfiguration = volatileApplicationConfiguration;
    }
}
