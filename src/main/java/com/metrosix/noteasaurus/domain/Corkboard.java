package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.security.SecuredResource;
import org.hibernate.annotations.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * This class represents a Corkboard which is a logical container for Notes.  It is a persistent class whose state is
 * persisted in the configured database.
 *
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: Corkboard.java 251 2010-08-08 00:03:00Z adam $
 */
@Entity
@Table(name="corkboard")
public class Corkboard extends AbstractEntity<Corkboard> implements SecuredResource, JSONString {

    static public enum JSONField {
        ID("id"),
        VERSION("version"),
        WEIGHT("weight"),
        LABEL("label"),
        IS_FOCUSED("is_focused"),
        OWNER_ID("ownerId");

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

    @Id
    @GenericGenerator(name="corkboard_id_generator",strategy="native",parameters={@Parameter(name="sequence",value="corkboard_id_seq")})
    @GeneratedValue(generator="corkboard_id_generator")
    @Column(name="id")
    private long id = 0;

    @OneToMany(mappedBy="corkboard")
    @Cascade({CascadeType.ALL,CascadeType.DELETE_ORPHAN})
    private Set<Note> notes = new HashSet<Note>();

    @ManyToOne
    @NotNull
    @JoinColumn(name="owner_id")
    private Person owner;

    @Basic
    @NotNull
    @NotEmpty
    @Length(max=128)
    @Column(name="label")
    private String label;

    @Basic
    @Column(name="is_focused")
    @Type(type = "true_false")
    private boolean focused;

    @Basic
    @NotNull
    @Column(name="weight")
    private short weight;

    /**
     * Construct a new Corkboard instance which will use the provided persistence manager.
     *
     * @param persistenceManager The PersistenceManager which this class should use.
     */
    public Corkboard(PersistenceManager persistenceManager) {
        super(persistenceManager);
    }

    /**
     * Get the unique id which uniquely represents this Corkboard in the database.
     *
     * @return The unique id of this Corkboard.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the unique id of this corkboard.
     *
     * @param id The unique id of this corkboard.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the label for this Corkboard, this is a human readable identifier for the Corkboard.
     *
     * @return The label used to identify the Corkboard to the user.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the label for this Corkboard, this is a human readable identifier for the Corkboard.
     *
     * @param label The label used to identify the Corkboard to the user.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * The weight of the Corkboard, this is a number indicating how a users corkboards should be ordered.  The larger
     * the number the closer to the bottom of the screen the corkboard will appear.
     *
     * TODO - This seems like it should be renamed, it is essentially a numeric sequence on which to order the
     * corkboards for a given user.  Weight only makes sense when we are sorting them along a vertical axis where the
     * "heavier" ones are at the bottom and the "lighter" ones are at the top but doesn't make any sense when sorting
     * along a vertical axis.
     *
     * @return The weight of the Corkboard.
     */
    public short getWeight() {
        return weight;
    }

    /**
     * Set the weight or sort order of this Corkboard.
     *
     * @param weight The weight or sort order of this corkboard.
     */
    public void setWeight(short weight) {
        this.weight = weight;
    }

    /**
     * Whether this corkboard has the users focus or not.
     *
     * @return true if the corkboard has the users focus false otherwise.
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * Set whether this corkboard has the users focus.
     *
     * @param focused true if the corkboard has the users focus, false otherwise.
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    /**
     * Get the Person who owns this Note this is the same as the creator or author.
     *
     * @return A Person instance for the Owner of this note.
     */
    public Person getOwner() {
        return owner;
    }

    /**
     * Set the Person who owns this note.  If you wish to associate a Corkboard with an individual please use the
     * methods on Person.
     *
     * @param owner The Person who owns this note.
     */
    protected void setOwner(Person owner) {
        this.owner = owner;
    }

    /**
     * Get a Set of notes which are logically contained within this Corkboard.  This returns an unmodifiable view of the 
     * notes within this corkboard, to modify the collection see
     * {@link Corkboard#addNote(com.metrosix.noteasaurus.domain.Note)},
     *
     * @return A Set of notes which are logically contained within this Corkboard.
     */
    public Set<Note> getNotes() {
        return Collections.unmodifiableSet(notes);
    }

    /**
     * This method will add a Note to the Corkboard.  It will assign the Corkboard reference on the Note as well as add
     * the Note to the Corkboards collection of notes.
     *
     * @param note The note to add to the collection of notes on this Corkboard.
     * @return true if this Corkboard did not already contain the Note, false otherwise.
     */
    public boolean addNote(Note note) {
        note.setCorkboard(this);
        return notes.add(note);
    }

    /**
     * Remove the provided note from the set of notes associated with this corkboard.
     * 
     * @param note The note to remove from this corkboard.
     * @return true if the corkboard contained the note and it was removed, false if this corkboard did not contain a
     * reference to this note.
     */
    public boolean removeNote(Note note) {
        if (notes.remove(note)) {
            note.setCorkboard(null);
            return true;
        }
        return false;
    }

    /**
     * This method will delete the corkboard and remove it from any collections which may be holding a reference to it.
     */
    @Override
    public void delete() {
        if (getOwner() != null) {
            getOwner().removeCorkboard(this);
        }
        super.delete();
    }

    /**
     * Generate a representation of this Corkboard in JSON format.
     *
     * @return A representation of this Corkboard in JSON format.
     */
    public String toJSONString() {
        JSONObject jsonObject = newJSONObject();
        try {
            jsonObject.put(JSONField.ID.getKey(), getId());
            jsonObject.put(JSONField.VERSION.getKey(), getVersion());
            jsonObject.put(JSONField.WEIGHT.getKey(), getWeight());
            jsonObject.put(JSONField.IS_FOCUSED.getKey(), isFocused());
            jsonObject.put(JSONField.LABEL.getKey(), getLabel() == null ? JSONObject.NULL : getLabel());
            jsonObject.put(JSONField.OWNER_ID.getKey(), getOwner() == null ? JSONObject.NULL : getOwner().getId());
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
}
