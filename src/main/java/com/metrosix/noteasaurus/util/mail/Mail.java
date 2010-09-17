package com.metrosix.noteasaurus.util.mail;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.ApplicationConfigurationParameter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import javax.mail.internet.*;
import javax.mail.*;
import javax.mail.internet.MimeMessage.RecipientType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam M. Dutko (adam@metro-six.com)
 */
abstract public class Mail {
    static public final int MAIL_DISABLED = -2;
    static public final int NO_RECIPIENTS = -1;

    static private final Logger log = LoggerFactory.getLogger(Mail.class);

    private ApplicationConfiguration applicationConfiguration;
    private VelocityEngine velocityEngine;


    private VelocityContext velocityContext;

    private Properties emailProperties = new Properties();

    protected Mail(VelocityEngine velocityEngine, ApplicationConfiguration applicationConfiguration) {
        setVelocityEngine(velocityEngine);
        setApplicationConfiguration(applicationConfiguration);
    }

    abstract protected String getSubject();

    abstract protected void populateVelocityContext(VelocityContext velocityContext);

    abstract protected String getVelocityTemplate();

    abstract protected Set<String> getToAddresses();

    protected Set<String> getCcAddresses() {
        return new HashSet<String>();
    }

    protected Set<String> getBccAddresses() {
        return new HashSet<String>();
    }

    protected void configureEmailProperties() {
        String value;

        value = getApplicationConfiguration().getValueOf(ApplicationConfigurationParameter.MAIL_SMTP_HOST);
        if (value != null) {
            getEmailProperties().setProperty("mail.smtp.host", value);
        }

        value = getApplicationConfiguration().getValueOf(ApplicationConfigurationParameter.MAIL_SMTP_PORT);
        if (value != null) {
            getEmailProperties().setProperty("mail.smtp.port", value);
        }

        value = getApplicationConfiguration().getValueOf(ApplicationConfigurationParameter.MAIL_SMTP_USER);
        if (value != null) {
            getEmailProperties().setProperty("mail.smtp.user", value);
        }

        value = getApplicationConfiguration().getValueOf(ApplicationConfigurationParameter.MAIL_FROM);
        if (value != null) {
            getEmailProperties().setProperty("mail.from", value);
        }
    }

    public int send() throws Exception {
        configureEmailProperties();
        Properties props = getEmailProperties();

        MimeMessage message = new MimeMessage(Session.getDefaultInstance(props, null));
        // Set From
        message.setFrom((new InternetAddress(props.getProperty("mail.from"))));
        // Set Subject
        message.setSubject(getSubject());
        // Set Body Text
        message.setText(generateBodyText());

        // Add our recipients.
        Address[] toRecipients = getRecipientsFor(getToAddresses());
        Address[] ccRecipients = getRecipientsFor(getCcAddresses());
        Address[] bccRecipients = getRecipientsFor(getBccAddresses());
        int recipientCount = 0;

        if (toRecipients != null && toRecipients.length > 0) {
            recipientCount += toRecipients.length;
            message.addRecipients(RecipientType.TO, toRecipients);
        }

        if (ccRecipients != null && ccRecipients.length > 0) {
            recipientCount += ccRecipients.length;
            message.addRecipients(RecipientType.CC, ccRecipients);
        }

        if (bccRecipients != null && bccRecipients.length > 0) {
            recipientCount += bccRecipients.length;
            message.addRecipients(RecipientType.BCC, bccRecipients);
        }

        // Don't send the message if there are no recipients.
        if (recipientCount <= 0) {
            if (log.isInfoEnabled()) {
                log.info("Refusing to send email message as no recipients could be found!");
            }
            return NO_RECIPIENTS;
        }

        // Don't send the message if it is disabled.
        if (!Boolean.parseBoolean(getApplicationConfiguration().getValueOf(ApplicationConfigurationParameter.MAIL_ENABLED))) {
            if (log.isInfoEnabled()) {
                log.info("Refusing to send email message as mail has been disabled through configuration!");
            }
            return MAIL_DISABLED;
        }

        // Send the message.
        Transport.send(message);
        return recipientCount;
    }

    protected Address[] getRecipientsFor(Collection<String> addresses) {
        Set<Address> addressSet = new HashSet<Address>();
        if (addresses != null && !addresses.isEmpty()) {
            for (String addr : addresses) {
                try {
                    addressSet.add(new InternetAddress(addr));
                } catch (AddressException e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Unable to send message to invalid email address '" + addr + "' omitting from recipient list: " + e.toString());
                    }
                }
            }
        }
        return addressSet.toArray(new Address[addressSet.size()]);
    }

    protected String generateBodyText() throws Exception {
        StringWriter writer = newStringWriter();
        setVelocityContext(newVelocityContext());
        populateVelocityContext(getVelocityContext());
        Template template = getVelocityEngine().getTemplate(getVelocityTemplate());
        template.merge(getVelocityContext(), writer);
        return writer.toString();
    }

    protected Properties getEmailProperties() {
        return emailProperties;
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public VelocityContext getVelocityContext() {
        return velocityContext;
    }

    public void setVelocityContext(VelocityContext velocityContext) {
        this.velocityContext = velocityContext;
    }

    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    protected VelocityContext newVelocityContext() {
        return new VelocityContext();
    }

    protected StringWriter newStringWriter() {
        return new StringWriter();
    }
}
