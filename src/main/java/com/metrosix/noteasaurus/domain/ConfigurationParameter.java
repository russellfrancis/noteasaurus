package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.database.PersistenceManager;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: ConfigurationParameter.java 247 2010-08-07 23:15:10Z adam $
 */
@Entity
@Table(name="configuration_parameter")
public class ConfigurationParameter extends AbstractEntity<ConfigurationParameter> {
   
    @Id
    @GenericGenerator(name="configuration_parameter_id_generator",strategy="native",parameters={@Parameter(name="sequence",value="configuration_parameter_id_seq")})
    @GeneratedValue(generator="configuration_parameter_id_generator")
    @Column(name="id")
    private long id = 0;

    @Basic
    @NotNull
    @Length(max=64)
    @Column(name="label")
    private String label;

    @Basic
    @Length(max=4000)
    @Column(name="value")
    private String value;

    public ConfigurationParameter(PersistenceManager persistenceManager) {
        super(persistenceManager);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
