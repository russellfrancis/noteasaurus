package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Note;
import com.metrosix.noteasaurus.domain.AbstractEntity;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.domain.util.PersonUtility;
import com.metrosix.noteasaurus.util.SecureUtility;
import org.hibernate.Session;
import org.json.JSONObject;
import org.json.JSONException;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Adam M. Dutko (dutko.adam at gmail dot com)
 * @version $Id: NoteTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class NoteTest {

    @Test
    public void testIdAccessorMutator() {
        long id = 4;
        Note note = new Note(null, null);
        note.setId(id);
        assertEquals("The accessor/mutator for the id field doesn't work.",
                id, note.getId());
    }
        
    @Test
    public void testVersionAccessorMutator() {
        int version = 54;
        Note note = new Note(null, null);
        note.setVersion(version);
        assertEquals("The accessor/mutator for the version field doesn't work.",
            version, note.getVersion());
    }
    
    @Test
    public void testDelete() throws Exception {
        Session session = createStrictMock(Session.class);
        Note note = createStrictMock(Note.class,
                AbstractEntity.class.getDeclaredMethod("getSession"));
        Corkboard cork = new Corkboard(null);
        cork.addNote(note);

        expect(note.getSession()).andReturn(session);
        session.delete(note);

        replay(session, note);

        assertEquals(1, cork.getNotes().size());
        assertEquals(note, cork.getNotes().iterator().next());
        note.delete();
        assertEquals(0, cork.getNotes().size());
        verify(session, note);
    }
    
    @Test
    public void testDeleteWithoutCorkboard() throws Exception {
        Session session = createStrictMock(Session.class);
        Note note = createStrictMock(Note.class,
                AbstractEntity.class.getDeclaredMethod("getSession"));

        expect(note.getSession()).andReturn(session);
        session.delete(note);

        replay(note, session);
        note.delete();
        verify(note, session);
    }
    
    @Test
    public void testCorkboardAccessorMutator() {
        Note note = new Note(null, null);
        Corkboard cork = new Corkboard(null);
        note.setCorkboard(cork);
        assertEquals(note.getCorkboard(), cork);
    }
    
    @Test
    public void testContentAccessorMutator() {
        Person person = new Person(null, null, new SecureUtility());
        Corkboard corkboard = new Corkboard(null);
        Note note = new Note(null, new SecureUtility());
        person.addCorkboard(corkboard);
        corkboard.addNote(note);
        String someContent = "Some random note content.";
        note.setContent(someContent);
        assertEquals(someContent,note.getContent());
    }
    
    @Test
    public void testXOriginAccessorMutator() {
        Note note = new Note(null, null);
        short x1 = 0;
        note.setX(x1);
        assertEquals(x1,note.getX());
    }
    
    @Test
    public void testYOriginAccessorMutator() {
        Note note = new Note(null, null);
        short y1 = 0;
        note.setY(y1);
        assertEquals(y1,note.getY());
    }
    
    @Test
    public void testWidthAccessorMutator() {
        Note note = new Note(null, null);
        short w = 2;
        note.setWidth(w);
        assertEquals(w,note.getWidth());
    }
    
    @Test
    public void testHeightAccessorMutator() {
        Note note = new Note(null, null);
        short h = 2;
        note.setHeight(h);
        assertEquals(h,note.getHeight());
    }
    
    @Test
    public void testColorAccessorMutator() {
        Note note = new Note(null, null);
        String skin = "blue";
        note.setSkin(skin);
        assertEquals(skin,note.getSkin());
    }
    
    @Test
    public void testFalseCollapseAccessorMutator() {
        Note note = new Note(null, null);
        boolean state = false;
        note.setCollapsed(state);
        assertFalse(note.isCollapsed());
    }
    
    @Test
    public void testTrueCollapseAccessorMutator() {
        Note note = new Note(null, null);
        boolean state = true;
        note.setCollapsed(state);
        assertTrue(note.isCollapsed());
    }
    
    @Test
    public void testZIndexAccessorMutator() {
        Note note = new Note(null, null);
        long index = 3L;
        note.setZindex(index);
        assertEquals(index,note.getZindex());
    }
    
    @Test
    public void testToJSONString() throws Exception {
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Person owner = new Person(pm, new PersonUtility(pm), new SecureUtility());
        Corkboard corkboard = new Corkboard(pm);
        Note note = new Note(pm, new SecureUtility());

        replay(pm);

        owner.addCorkboard(corkboard);
        corkboard.addNote(note);

        JSONObject jsonObject = new JSONObject( note.toJSONString() );

        System.err.println(jsonObject.toString());
        assertEquals(0, jsonObject.getLong(Note.JSONField.ID.getKey()));
        assertEquals(0, jsonObject.getInt(Note.JSONField.VERSION.getKey()));
        assertEquals(JSONObject.NULL, jsonObject.get(Note.JSONField.CONTENT.getKey()));
        assertEquals(0, jsonObject.getInt(Note.JSONField.X.getKey()));
        assertEquals(0, jsonObject.getInt(Note.JSONField.Y.getKey()));
        assertEquals(0, jsonObject.getInt(Note.JSONField.WIDTH.getKey()));
        assertEquals(0, jsonObject.getInt(Note.JSONField.HEIGHT.getKey()));
        assertEquals(JSONObject.NULL, jsonObject.get(Note.JSONField.SKIN.getKey()));
        assertEquals(false, jsonObject.getBoolean(Note.JSONField.COLLAPSED.getKey()));

        long id = 4L;
        int version = 2;
        String content = "Some random note content.";
        short x1 = 2;
        short y1 = 3;
        short w = 3;
        short h = 4;
        String skin = "black";
        boolean state = true;
        
        note.setId(id);
        note.setVersion(version);
        note.setContent(content);
        note.setX(x1);
        note.setY(y1);
        note.setWidth(w);
        note.setHeight(h);
        note.setSkin(skin);
        note.setCollapsed(state);

        jsonObject = new JSONObject(note.toJSONString());
        System.err.println(jsonObject.toString());
        assertEquals(id, jsonObject.getLong(Note.JSONField.ID.getKey()));
        assertEquals(version, jsonObject.getInt(Note.JSONField.VERSION.getKey()));
        assertEquals(content, jsonObject.get(Note.JSONField.CONTENT.getKey()));
        assertEquals(x1, (short)jsonObject.getInt(Note.JSONField.X.getKey()));
        assertEquals(y1, (short)jsonObject.getInt(Note.JSONField.Y.getKey()));
        assertEquals(w, jsonObject.getInt(Note.JSONField.WIDTH.getKey()));
        assertEquals(h, jsonObject.getInt(Note.JSONField.HEIGHT.getKey()));
        assertEquals(skin, jsonObject.get(Note.JSONField.SKIN.getKey()));
        assertEquals(state, jsonObject.getBoolean(Note.JSONField.COLLAPSED.getKey()));

        verify(pm);
    }

    @Test
    public void testNoteToJSONString_WithException() throws Exception {
        JSONException ex = new JSONException("Yikes!");
        JSONObject jsonObject = createStrictMock(JSONObject.class);
        
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Note note = createStrictMock(Note.class,
                Note.class.getDeclaredMethod("newJSONObject"));

        expect(note.newJSONObject()).andReturn(jsonObject);
        jsonObject.put(Note.JSONField.ID.getKey(),(long)0);
        expectLastCall().andThrow(ex);

        replay(pm, jsonObject, note);
        try {
            note.toJSONString();
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(ex, e.getCause());
            // success
        }
        verify(pm, jsonObject, note);
    }
}
