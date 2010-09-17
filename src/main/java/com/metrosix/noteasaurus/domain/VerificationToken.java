package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.database.PersistenceManager;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: VerificationToken.java 247 2010-08-07 23:15:10Z adam $
 */
@Entity
@Table(name="verification_token")
public class VerificationToken extends AbstractEntity<VerificationToken> {
    @Id
    @GenericGenerator(name="verification_token_id_generator",strategy="native",parameters={@Parameter(name="sequence",value="verification_token_id_seq")})
    @GeneratedValue(generator="verification_token_id_generator")
    @Column(name="id")
    private long id = 0;

    @ManyToOne
    @NotNull
    @JoinColumn(name="owner_id")
    private Person owner;

    @Basic
    @NotNull
    @Length(min=40,max=40)
    private String token;

    public VerificationToken(PersistenceManager persistenceManager) {
        super(persistenceManager);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
