package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.InvalidValuesException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.hibernate.validator.NotNull;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: AbstractEntity.java 247 2010-08-07 23:15:10Z adam $
 */
@MappedSuperclass
abstract public class AbstractEntity<T extends AbstractEntity> implements Serializable {
    static private final long serialVersionUID = 1L;

    @Transient
    transient private PersistenceManager persistenceManager;

    @Version
    @Column(name="version")
    private int version;

    @Basic
    @NotNull
    @Column(name="hashcode")
    private int hashCode = 0;

    public AbstractEntity(PersistenceManager persistenceManager) {
        setPersistenceManager(persistenceManager);
    }

    abstract public long getId();
    
    abstract public void setId(long id);

    public int getVersion() {
        return version;
    }

    protected void setVersion(int version) {
        this.version = version;
    }

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    public Serializable save() {
        return getSession().save(this);
    }

    public void saveOrUpdate() {
        getSession().saveOrUpdate(this);
    }

    public void update() {
        getSession().update(this);
    }

    public void delete() {
        getSession().delete(this);
    }

    public void evict() {
        getSession().evict(this);
    }

    public void merge() {
        getSession().merge(this);
    }

    public T reattach() {
        Criteria criteria = getSession().createCriteria(getClass());
        criteria.add(Restrictions.eq("id", getId()));
        return (T) criteria.uniqueResult();
    }

    protected Session getSession() {
        return getPersistenceManager().getSession();
    }

    protected PersistenceManager getPersistenceManager() {
        return persistenceManager;
    }

    protected void setPersistenceManager(PersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof AbstractEntity)) {
            return false;
        }
        if (!(getClass().equals(o.getClass()))) {
            return false;
        }
        if (getId() == 0) {
            return super.equals(o);
        }
        return getId() == ((AbstractEntity)o).getId();
    }

    @Override
    public int hashCode() {
        if (getId() == 0) {
            return super.hashCode();
        }
        return getHashCode();
    }

    public ClassValidator newClassValidator() {
        return new ClassValidator(this.getClass());
    }
    
    protected boolean validate(Collection<String> fields, Collection<String> messages) {
        ClassValidator validator = newClassValidator();
        InvalidValue[] badValueArray = validator.getInvalidValues(this);
        List<InvalidValue> invalidValues = 
                badValueArray != null ? Arrays.asList(badValueArray) : new ArrayList<InvalidValue>();

        for (InvalidValue invalidValue : invalidValues) {
            fields.add(invalidValue.getPropertyName());
            messages.add(invalidValue.getMessage());
        }
        
        return fields.isEmpty();
    }

    public void assertValid() throws InvalidValuesException {
        List<String> fields = new ArrayList<String>();
        List<String> messages = new ArrayList<String>();

        if (!validate(fields, messages)) {
            getPersistenceManager().getTransaction().rollback();
            throw new InvalidValuesException(fields, messages);
        }
    }
}
