package com.metrosix.noteasaurus.config;

/**
 * This enumeration defines all of the configuration parameters which we may
 * expect to find in our configuration file.
 */
public enum ApplicationConfigurationParameter {

    DB_DRIVER("db.driver", false),
    DB_URL("db.url", false),
    DB_USERNAME("db.username", false),
    DB_PASSWORD("db.password", false),
    NON_EXSISTENT("non.existent", false),

    MAIL_ENABLED("mail.enabled", true),
    MAIL_FROM("mail.from", true),
    MAIL_SMTP_HOST("mail.smtp.host", true),
    MAIL_SMTP_USER("mail.smtp.user", true),
    MAIL_SMTP_PORT("mail.smtp.port", true),

    URLBASE("urlbase", true);
    
    private String key;
    private boolean isVolatile;

    /**
     * Construct a new ApplicationConfigurationParameter.
     * 
     * @param key The key used to identify this parameter.
     */
    private ApplicationConfigurationParameter(String key, boolean isVolatile) {
        setKey(key);
        setVolatile(isVolatile);
    }

    /**
     * Set the key for this configuration parameter.
     * 
     * @param key The key for this configuration parameter.
     */
    private void setKey(String key) {
        this.key = key;
    }

    /**
     * This method will get the key for this configuration parameter.
     * 
     * @return The key for this configuration parameter.
     */
    public String getKey() {
        return this.key;
    }

    public boolean isVolatile() {
        return isVolatile;
    }

    public void setVolatile(boolean isVolatile) {
        this.isVolatile = isVolatile;
    }
}
