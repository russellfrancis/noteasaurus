package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.util.PersonUtility;
import com.metrosix.noteasaurus.security.SecuredResource;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import com.metrosix.noteasaurus.util.SecureUtility;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
@Entity
@Table(name="person")
public class Person extends AbstractEntity<Person> implements SecurityPrincipal, SecuredResource, JSONString {
    static private final long serialVersionUID = 1L;
    static private final Logger log = LoggerFactory.getLogger(Person.class);
    static private final int PASSWORD_KEY_SIZE = 128;
    static private final Pattern SHA1_PATTERN = Pattern.compile("^[0-9a-fA-F]{40}$");

    static public enum JSONField {
        /* Actively ommit setting
         * a field for password, is_verified and created_on
         */
        ID("id"),
        VERSION("version"),
        CORKBOARD("corkboard"),
        EMAIL("email"),;

        private String key;

        JSONField(String key) {
            setKey(key);
        }

        public String getKey() {
            return key;
        }

        private void setKey(String key) {
            this.key = key;
        }
    }

    @Transient
    transient private PersonUtility personUtility;

    @Transient
    transient private SecureUtility secureUtility;
    
    @Id
    @GenericGenerator(name="person_id_generator",strategy="native",parameters={@Parameter(name="sequence",value="person_id_seq")})
    @GeneratedValue(generator="person_id_generator")
    @Column(name="id")
    private long id = 0;

    @OneToMany(mappedBy="owner")
    @Cascade({CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    private Set<Corkboard> corkboards = new HashSet<Corkboard>();

    @OneToMany(mappedBy="owner")
    @Cascade({CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    private Set<VerificationToken> verificationTokens = new HashSet<VerificationToken>();

    @Basic
    @Column(name="is_verified")
    @Type(type = "true_false")
    private boolean verified;

    @Basic
    @Column(name="created_on")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @Basic
    @NotNull
    @NotEmpty
    @Length(max=64)
    @Column(name="password")
    private String password;

    @Basic
    @Email
    @NotNull
    @NotEmpty
    @Length(max=256)
    @Column(name="email")
    private String email;

    @Basic
    @Length(max=24)
    @NotNull
    @NotEmpty
    @Column(name="secret_key")
    private String secretKey;
    
    public Person(PersistenceManager persistenceManager, PersonUtility personUtility, SecureUtility secureUtility) {
        super(persistenceManager);
        setPersonUtility(personUtility);
        setSecureUtility(secureUtility);
    }

    public String getName() {
        return getEmail();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Date getCreatedOn() {
        return (Date) (createdOn == null ? null : createdOn.clone());
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = (Date) (createdOn == null ? null : createdOn.clone());
    }

    synchronized public String getPassword() {
        try {
            if (password != null) {
                SecureUtility secure = getSecureUtility();
                byte[] decoded = Base64.decodeBase64(password.getBytes());
                byte[] decrypted = secure.decryptAES(secure.scramble(getSecretKey()), decoded);
                return (new String(decrypted)).toUpperCase();
            }
            return null;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    synchronized public void setPassword(String password) {
        try {
            if (password == null) {
                this.password = null;
            } else {
                // We could have an sha1 password
                if (!SHA1_PATTERN.matcher(password).matches()) {
                    // We have a plaintext password
                    password = DigestUtils.shaHex(password);
                }
                // At this point password is an SHA1 hex encoded value.

                SecureUtility secure = getSecureUtility();
                byte[] encrypted = secure.encryptAES(secure.scramble(getSecretKey()), password.getBytes());
                byte[] encoded = Base64.encodeBase64(encrypted);
                this.password = new String(encoded);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    synchronized protected void setSecretKey(byte[] skey) {
        String result = null;
        if (skey != null && skey.length > 0) {
            result = new String(Base64.encodeBase64(skey));
        }
        this.secretKey = result;
    }

    synchronized public byte[] getSecretKey() {
        try {
            if (secretKey == null) {
                byte[] key = getSecureUtility().generateAESKey(PASSWORD_KEY_SIZE);
                setSecretKey(key);
            }
            return Base64.decodeBase64(secretKey.getBytes());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Corkboard> getCorkboards() {
        return Collections.unmodifiableSet(corkboards);
    }

    protected void setCorkboards(Set<Corkboard> corkboards) {
        this.corkboards = corkboards;
    }

    public boolean addCorkboard(Corkboard corkboard) {
        corkboard.setOwner(this);
        return corkboards.add(corkboard);
    }

    public boolean removeCorkboard(Corkboard corkboard) {
        if (corkboards.remove(corkboard)) {
            corkboard.setOwner(null);
            return true;
        }
        return false;
    }

    public Set<VerificationToken> getVerificationTokens() {
        return verificationTokens;
    }

    protected void setVerificationTokens(Set<VerificationToken> verificationTokens) {
        this.verificationTokens = verificationTokens;
    }

    public boolean addVerificationToken(VerificationToken token) {
        token.setOwner(this);
        return verificationTokens.add(token);
    }

    public boolean removeVerificationToken(VerificationToken token) {
        if (verificationTokens.remove(token)) {
            token.setOwner(null);
            return true;
        }
        return false;
    }

    public boolean canRead(Class<? extends SecuredResource> resource) {
        if (    Person.class.isAssignableFrom(resource)     ||
                Corkboard.class.isAssignableFrom(resource)  ||
                Note.class.isAssignableFrom(resource)       )
        {
            return true;
        }
        return false;
    }

    public boolean canWrite(Class<? extends SecuredResource> resource) {
        if (    Person.class.isAssignableFrom(resource)     ||
                Corkboard.class.isAssignableFrom(resource)  ||
                Note.class.isAssignableFrom(resource)       )
        {
            return true;
        }
        return false;
    }

    public boolean canRead(SecuredResource resource) {
        if (resource instanceof Note) {
            return canReadNote((Note)resource);
        }
        else if (resource instanceof Corkboard) {
            return canReadCorkboard((Corkboard)resource);
        }
        return false;
    }

    public boolean canWrite(SecuredResource resource) {
        if (resource instanceof Note) {
            return canWriteNote((Note)resource);
        }
        else if (resource instanceof Corkboard) {
            return canWriteCorkboard((Corkboard)resource);
        }
        return false;
    }    

    public boolean canRead(Collection<? extends SecuredResource> resources) {
        for (SecuredResource resource : resources) {
            if (!canRead(resource)) {
                return false;
            }
        }
        return true;
    }

    public boolean canWrite(Collection<? extends SecuredResource> resources) {
        for (SecuredResource resource : resources) {
            if (!canWrite(resource)) {
                return false;
            }
        }
        return true;
    }

    protected boolean canReadCorkboard(Corkboard corkboard) {
        boolean canRead = isVerified() && equals(corkboard.getOwner());
        if (!canRead &&log.isInfoEnabled()) {
            log.info("Denying read access for '" + getName() + "' to '" + corkboard + "'.");
        }
        return canRead;
    }

    protected boolean canReadNote(Note note) {
        boolean canRead = isVerified() && canReadCorkboard(note.getCorkboard());
        if (!canRead && log.isInfoEnabled()) {
            log.info("Denying read access for '" + getName() + "' to '" + note + "'.");
        }
        return canRead;
    }

    protected boolean canWriteNote(Note note) {
        boolean canWrite = isVerified() && canWriteCorkboard(note.getCorkboard());
        if (!canWrite && log.isInfoEnabled()) {
            log.info("Denying write access for '" + getName() + "' to '" + note + "'.");
        }
        return canWrite;
    }

    protected boolean canWriteCorkboard(Corkboard corkboard) {
        boolean canWrite = isVerified() && equals(corkboard.getOwner());
        if (!canWrite && log.isInfoEnabled()) {
            log.info("Denying write access for '" + getName() + "' to '" + corkboard + "'.");
        }
        return canWrite;
    }
    
    /**
     * Generate a representation of this Person in JSON format.
     *
     * @return A representation of this Person in JSON format.
     */
    public String toJSONString() {
        JSONObject jsonObject = newJSONObject();
        try {
            jsonObject.put(JSONField.ID.getKey(), getId());
            jsonObject.put(JSONField.VERSION.getKey(), getVersion());
            // Omit PASSWORD
            // Omit CREATED_ON
            // Omit VERIFIED
            jsonObject.put(JSONField.EMAIL.getKey(), getEmail() == null ? JSONObject.NULL : getEmail());
            return jsonObject.toString();
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Return a new instance of a JSONObject, primarily for testing.
     *
     * @return A new instance of a JSONObject.
     */
    protected JSONObject newJSONObject() {
        return new JSONObject();
    }

    @Override
    protected boolean validate(Collection<String> fields, Collection<String> messages) {
        super.validate(fields, messages);

        if (getPassword() == null || getPassword().length() == 0) {
            fields.add("password");
            messages.add("must not be empty.");
        }

        if (this.getEmail() != null) {
            if (getPersonUtility().getPersonByEmail(this.getEmail()) != null) {
                fields.add("email");
                messages.add("The provided email address is not unique.");
            }
        }

        return fields.isEmpty();
    }

    public PersonUtility getPersonUtility() {
        return personUtility;
    }

    public void setPersonUtility(PersonUtility personUtility) {
        this.personUtility = personUtility;
    }

    public SecureUtility getSecureUtility() {
        return secureUtility;
    }

    public void setSecureUtility(SecureUtility secureUtility) {
        this.secureUtility = secureUtility;
    }
}
