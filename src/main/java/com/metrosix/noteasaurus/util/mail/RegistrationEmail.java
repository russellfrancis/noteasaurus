package com.metrosix.noteasaurus.util.mail;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import java.util.HashSet;
import java.util.Set;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: RegistrationEmail.java 247 2010-08-07 23:15:10Z adam $
 */
public class RegistrationEmail extends Mail {

    private String registrationUrl;
    private String toEmail;

    public RegistrationEmail(VelocityEngine velocityEngine, ApplicationConfiguration applicationConfiguration) {
        super(velocityEngine, applicationConfiguration);
    }

    @Override
    protected Set<String> getToAddresses() {
        Set<String> to = new HashSet<String>();
        to.add(getToEmail());
        return to;
    }

    @Override
    protected String getVelocityTemplate() {
        return "/email/registration.template";
    }

    @Override
    protected void populateVelocityContext(VelocityContext velocityContext) {
        velocityContext.put("appName", "Noteasaurus");
        velocityContext.put("registrationUrl", getRegistrationUrl());
    }

    @Override
    protected String getSubject() {
        return "Noteasaurus Registration";
    }

    public String getRegistrationUrl() {
        return registrationUrl;
    }

    public void setRegistrationUrl(String registrationUrl) {
        this.registrationUrl = registrationUrl;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
