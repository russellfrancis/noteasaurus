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
import com.metrosix.noteasaurus.util.PicoContainerFactory;
import org.hibernate.Session;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: CreateNoteProcedure.java 247 2010-08-07 23:15:10Z adam $
 */
@AssertSecurity(canRead={},canWrite={Note.class,Corkboard.class})
public class CreateNoteProcedure extends AbstractProcedure {
    private long corkboardId;
    private String content;
    private short x;
    private short y;
    private short width;
    private short height;
    private String skin;
    private boolean collapsed;

    public CreateNoteProcedure(PersistenceManager persistenceManager) {
        super(persistenceManager);
    }

    public Object executeAs(SecurityPrincipal principal) throws ProcedureException {
        Session session = getPersistenceManager().getSession();
        Corkboard corkboard = (Corkboard) session.get(Corkboard.class, getCorkboardId());
        if (corkboard == null) {
            throw new EntityNotFoundException(Corkboard.class, getCorkboardId());
        }

        if (!principal.canWrite(corkboard)) {
            throw new SecurityDeniedException(principal, corkboard);
        }

        Note note = newNote();
        corkboard.addNote(note);
        note.setContent(getContent());
        note.setX(getX());
        note.setY(getY());
        note.setWidth(getWidth());
        note.setHeight(getHeight());
        note.setSkin(getSkin());
        note.setCollapsed(isCollapsed());
        note.setZindex(System.currentTimeMillis());

        note.assertValid();

        getPersistenceManager().getTransaction().commit();

        return note;
    }

    protected Note newNote() {
        return PicoContainerFactory.getPicoContainer().getComponent(Note.class);
    }

    @Argument(name="corkboardId",required=true)
    public long getCorkboardId() {
        return corkboardId;
    }

    public void setCorkboardId(long corkboardId) {
        this.corkboardId = corkboardId;
    }

    @Argument(name="content",required=true)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Argument(name="x",required=true)
    public short getX() {
        return x;
    }

    public void setX(short x) {
        this.x = x;
    }

    @Argument(name="y",required=true)
    public short getY() {
        return y;
    }

    public void setY(short y) {
        this.y = y;
    }

    @Argument(name="width",required=true)
    public short getWidth() {
        return width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

    @Argument(name="height",required=true)
    public short getHeight() {
        return height;
    }

    public void setHeight(short height) {
        this.height = height;
    }

    @Argument(name="skin",required=true)
    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    @Argument(name="collapsed",required=true)
    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }
}
