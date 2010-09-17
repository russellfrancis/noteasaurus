package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.ApplicationConfigurationParameter;
import com.metrosix.noteasaurus.rpc.proc.*;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.domain.VerificationToken;
import com.metrosix.noteasaurus.domain.util.PersonUtility;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import com.metrosix.noteasaurus.util.PicoContainerFactory;
import com.metrosix.noteasaurus.util.mail.Mail;
import com.metrosix.noteasaurus.util.mail.RegistrationEmail;
import java.util.Date;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Adam M. Dutko (dutko.adam@gmail.com)
 * @version $Id: RegisterPersonProcedure.java 247 2010-08-07 23:15:10Z adam $
 */
@AssertSecurity(canRead={Person.class},canWrite={})
public class RegisterPersonProcedure extends AbstractProcedure {
    private static final Logger log = LoggerFactory.getLogger(RegisterPersonProcedure.class);
    private static final Random rand = new Random();

    private ApplicationConfiguration applicationConfiguration;
    private PersonUtility personUtility;

    private String email;
    private String password;
    
    public RegisterPersonProcedure(PersistenceManager persistenceManager, 
            ApplicationConfiguration applicationConfiguration, PersonUtility personUtility)
    {
        super(persistenceManager);
        setApplicationConfiguration(applicationConfiguration);
        setPersonUtility(personUtility);
    }
    
    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        Person person = PicoContainerFactory.getPicoContainer().getComponent(Person.class);
        person.setEmail(getEmail());
        person.setPassword(getPassword());
        person.setCreatedOn(new Date());
        person.setVerified(false);

        VerificationToken token = PicoContainerFactory.getPicoContainer().getComponent(VerificationToken.class);
        byte[] b = new byte[1024];
        synchronized(rand) {
            rand.nextBytes(b);
        }
        String hash = DigestUtils.shaHex(b);
        token.setToken(hash);
        person.addVerificationToken(token);

        // validate that the parameters are valid.
        person.assertValid();
        token.assertValid();

        person.save();
        
        getPersistenceManager().getTransaction().commit();
        token = token.reattach();

        try {            
            String urlBase = getApplicationConfiguration().getValueOf(ApplicationConfigurationParameter.URLBASE);
            if (!urlBase.endsWith("/")) {
                urlBase += "/";
            }
            StringBuilder registrationUrl = new StringBuilder(urlBase);
            registrationUrl.append("index.html#/verify?uid=");
            registrationUrl.append(person.getId());
            registrationUrl.append("&token=");
            registrationUrl.append(token.getToken());

            RegistrationEmail message = PicoContainerFactory.getPicoContainer().getComponent(RegistrationEmail.class);
            message.setRegistrationUrl(registrationUrl.toString());
            message.setToEmail(getEmail());
            int recipientCount = message.send();

            if (log.isDebugEnabled()) {
                if (recipientCount >= 0) {
                    log.debug("Sent message to " + recipientCount + " addresses.");
                } else if (recipientCount == Mail.MAIL_DISABLED) {
                    log.debug("Refused to send message as email is disabled.");
                } else if (recipientCount == Mail.NO_RECIPIENTS) {
                    log.debug("Refused to send message as no recipients were listed.");
                } else {
                    log.debug("Unknown response while trying to send an email message.");
                }
            }

            if (log.isInfoEnabled()) {
                log.info("User registration\n\n" + registrationUrl + "\n\n");
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.toString(),e);
            }
        }
        
        return null;
    }

    @Argument(name="email",required=true)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    @Argument(name="password",required=true)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    public void setApplicationConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    public PersonUtility getPersonUtility() {
        return personUtility;
    }

    public void setPersonUtility(PersonUtility personUtility) {
        this.personUtility = personUtility;
    }
}
