package com.metrosix.noteasaurus.rpc.proc.impl;

import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Note;
import com.metrosix.noteasaurus.rpc.proc.Argument;
import com.metrosix.noteasaurus.rpc.proc.AssertSecurity;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.EntityNotFoundException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.ProcedureException;
import com.metrosix.noteasaurus.rpc.proc.impl.exception.SecurityDeniedException;
import com.metrosix.noteasaurus.security.SecurityPrincipal;
import org.hibernate.Session;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
@AssertSecurity(canRead={},canWrite={Corkboard.class,Note.class})
public class UpdateNoteProcedure extends AbstractProcedure {
    private long id;
    private Long corkboardId;
    private String content;
    private Short x;
    private Short y;
    private Short width;
    private Short height;
    private String skin;
    private Boolean collapsed;

    public UpdateNoteProcedure(PersistenceManager persistenceManager) {
        super(persistenceManager);
    }

    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        Session session = getPersistenceManager().getSession();

        Note note = (Note) session.get(Note.class, getId());

        if (note == null) {
            throw new EntityNotFoundException(Note.class, getId());
        }

        if (!principal.canWrite(note)) {
            throw new SecurityDeniedException(principal, note);
        }

        // If we are changing corkboards, verify security on the new corkboard and make the switch.
        if (getCorkboardId() != null && !getCorkboardId().equals(note.getCorkboard().getId())) {
            Corkboard newCorkboard = (Corkboard) session.get(Corkboard.class, getCorkboardId().longValue());

            if (newCorkboard == null) {
                throw new EntityNotFoundException(Corkboard.class, getCorkboardId().longValue());
            }

            if (!principal.canWrite(newCorkboard)) {
                throw new SecurityDeniedException(principal, newCorkboard);
            }

            newCorkboard.addNote(note);
        }

        if (getContent() != null) {
            note.setContent(getContent());
        }

        if (getX() != null) {
            note.setX(getX());
        }

        if (getY() != null) {
            note.setY(getY());
        }

        if (getWidth() != null) {
            note.setWidth(getWidth());
        }

        if (getHeight() != null) {
            note.setHeight(getHeight());
        }

        if (getSkin() != null) {
            note.setSkin(getSkin());
        }

        if (isCollapsed() != null) {
            note.setCollapsed(isCollapsed());
        }

        note.setZindex(System.currentTimeMillis());

        note.assertValid();

        session.getTransaction().commit();

        return null;
    }

    @Argument(name="id",required=true)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Argument(name="corkboardId",required=false)
    public Long getCorkboardId() {
        return corkboardId;
    }

    public void setCorkboardId(Long corkboardId) {
        this.corkboardId = corkboardId;
    }

    @Argument(name="content",required=false)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Argument(name="x",required=false)
    public Short getX() {
        return x;
    }

    public void setX(Short x) {
        this.x = x;
    }

    @Argument(name="y",required=false)
    public Short getY() {
        return y;
    }

    public void setY(Short y) {
        this.y = y;
    }

    @Argument(name="width",required=false)
    public Short getWidth() {
        return width;
    }

    public void setWidth(Short width) {
        this.width = width;
    }

    @Argument(name="height",required=false)
    public Short getHeight() {
        return height;
    }

    public void setHeight(Short height) {
        this.height = height;
    }

    @Argument(name="skin",required=false)
    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    @Argument(name="collapsed",required=false)
    public Boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(Boolean collapsed) {
        this.collapsed = collapsed;
    }
}
