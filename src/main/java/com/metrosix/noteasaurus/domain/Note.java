package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.security.SecuredResource;
import com.metrosix.noteasaurus.util.SecureUtility;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
@Entity
@Table(name="note")
public class Note extends AbstractEntity<Note> implements SecuredResource, JSONString {
    static private final long serialVersionUID = 1L;

    static public enum JSONField {
        ID("id"),
        VERSION("version"),
        CORKBOARD("corkboard"),
        CONTENT("content"),
        X("x"),
        Y("y"),
        WIDTH("width"),
        HEIGHT("height"),
        SKIN("skin"),
        COLLAPSED("collapsed");

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
    transient private SecureUtility secureUtility;
    
    @Id
    @GenericGenerator(name="note_id_generator",strategy="native",parameters={@Parameter(name="sequence",value="note_id_seq")})
    @GeneratedValue(generator="note_id_generator")
    @Column(name="id")
    private long id = 0;

    @ManyToOne
    @JoinColumn(name="corkboard_id")
    private Corkboard corkboard;

    @Basic
    @NotNull
    @Length(max=65000)
    @Column(name="content")
    private String content;

    @Basic
    @Column(name="x")
    private short x;

    @Basic
    @Column(name="y")
    private short y;

    @Basic
    @Column(name="width")
    private short width;

    @Basic
    @Column(name="height")
    private short height;

    @Basic
    @NotNull
    @NotEmpty
    @Length(max=24)
    @Column(name="skin")
    private String skin;

    @Basic
    @Column(name="is_collapsed")
    @Type(type = "true_false")
    private boolean collapsed;

    @Basic
    @Column(name="z_index")
    private long zIndex;

    public Note(PersistenceManager persistenceManager, SecureUtility secureUtility) {
        super(persistenceManager);
        setSecureUtility(secureUtility);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    
    public Corkboard getCorkboard() {
        return corkboard;
    }

    public void setCorkboard(Corkboard corkboard) {
        this.corkboard = corkboard;
    }

    @Override
    public void delete() {
        if (getCorkboard() != null) {
            getCorkboard().removeNote(this);
        }
        super.delete();
    }

    synchronized public String getContent() {
        try {
            if (content != null) {
                SecureUtility secure = getSecureUtility();
                byte[] secretKey = getCorkboard().getOwner().getSecretKey();
                secretKey = secure.scramble(secretKey);
                byte[] decoded = Base64.decodeBase64(content.getBytes());
                byte[] decrypted = secure.decryptAES(secretKey, decoded);
                return new String(decrypted);
            }
            return null;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    synchronized public void setContent(String content) {
        try {
            if (content == null) {
                this.content = null;
            } else {
                SecureUtility secure = getSecureUtility();
                byte[] secretKey = getCorkboard().getOwner().getSecretKey();
                secretKey = secure.scramble(secretKey);
                byte[] encryptedContent = secure.encryptAES(secretKey, content.getBytes());
                byte[] encoded = Base64.encodeBase64(encryptedContent);
                this.content = new String(encoded);
            }
        }
        catch (RuntimeException e) {
            throw e;
        } 
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public short getX() {
        return x;
    }

    public void setX(short X) {
        this.x = X;
    }

    public short getY() {
        return y;
    }

    public void setY(short Y) {
        this.y = Y;
    }

    public short getWidth() {
        return width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public short getHeight() {
        return height;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public long getZindex() {
        return zIndex;
    }

    public void setZindex(long zIndex) {
        this.zIndex = zIndex;
    }

    /**
     * Generate a representation of this Note in JSON format.
     *
     * @return A representation of this Note in JSON format.
     */
    public String toJSONString() {
        JSONObject jsonObject = newJSONObject();
        try {
            jsonObject.put(JSONField.ID.getKey(), getId());
            jsonObject.put(JSONField.VERSION.getKey(), getVersion());
            jsonObject.put(JSONField.CONTENT.getKey(), getContent() == null ? JSONObject.NULL : getContent());
            jsonObject.put(JSONField.X.getKey(), getX());
            jsonObject.put(JSONField.Y.getKey(), getY());
            jsonObject.put(JSONField.WIDTH.getKey(), getWidth());
            jsonObject.put(JSONField.HEIGHT.getKey(), getHeight());
            jsonObject.put(JSONField.SKIN.getKey(), getSkin() == null ? JSONObject.NULL : getSkin());
            jsonObject.put(JSONField.COLLAPSED.getKey(), isCollapsed());
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


    protected SecureUtility getSecureUtility() {
        return secureUtility;
    }

    protected void setSecureUtility(SecureUtility secureUtility) {
        this.secureUtility = secureUtility;
    }
}
