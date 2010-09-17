package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Note;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.database.PersistenceManager;
import org.hibernate.Session;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: CorkboardTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class CorkboardTest {

    @Test
    public void testIdAccessorAndMutator() {
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard = new Corkboard(pm);
        replay(pm);

        assertEquals(0, corkboard.getId());
        corkboard.setId(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, corkboard.getId());

        verify(pm);
    }

    @Test
    public void testLabelAccessorAndMutator() {
        String label = "My Label";
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard = new Corkboard(pm);
        replay(pm);

        assertNull(corkboard.getLabel());
        corkboard.setLabel(label);
        assertEquals(label, corkboard.getLabel());

        verify(pm);
    }

    @Test
    public void testWeightAccessorAndMutator() {
        short weight = 4;
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard = new Corkboard(pm);
        replay(pm);

        assertEquals(0, corkboard.getWeight());
        corkboard.setWeight(weight);
        assertEquals(weight, corkboard.getWeight());

        verify(pm);
    }

    @Test
    public void testOwnerAccessorAndMutator() {
        Person owner = createStrictMock(Person.class);
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard = new Corkboard(pm);
        replay(pm, owner);

        assertNull(corkboard.getOwner());
        corkboard.setOwner(owner);
        assertEquals(owner, corkboard.getOwner());
        corkboard.setOwner(null);
        assertNull(corkboard.getOwner());

        verify(pm, owner);
    }

    @Test
    public void testNotesAccessor() {
        Note note = createStrictMock(Note.class);
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard = new Corkboard(pm);

        note.setCorkboard(corkboard);
        note.setCorkboard(null);

        replay(pm, note);

        // assert our initial state.
        assertEquals(0, corkboard.getNotes().size());

        // add our note.
        corkboard.addNote(note);
        assertEquals(1, corkboard.getNotes().size());
        assertTrue(corkboard.getNotes().contains(note));

        // assert that getNotes returns an unmodifiable view.
        try {
            corkboard.getNotes().remove(note);
            fail();
        }
        catch (UnsupportedOperationException e) {
            // getNotes() should return an unmodifiable view.
        }
        assertEquals(1, corkboard.getNotes().size());

        // Test removal and return true.
        assertTrue(corkboard.removeNote(note));
        assertEquals(0, corkboard.getNotes().size());

        // Test removal and return false.
        assertFalse(corkboard.removeNote(note));

        verify(pm, note);
    }

    @Test
    public void testDelete_WithNoOwner() {
        Session session = createStrictMock(Session.class);
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard = new Corkboard(pm);

        expect(pm.getSession()).andReturn(session);
        session.delete(corkboard);

        replay(pm, session);

        corkboard.delete();

        verify(pm, session);
    }

    @Test
    public void testDelete_WithOwner() {
        Person owner = createStrictMock(Person.class);
        Session session = createStrictMock(Session.class);
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard = new Corkboard(pm);
        corkboard.setOwner(owner);

        expect(owner.removeCorkboard(corkboard)).andReturn(true);
        expect(pm.getSession()).andReturn(session);
        session.delete(corkboard);

        replay(pm, session, owner);

        corkboard.delete();

        verify(pm, session, owner);
    }

    @Test
    public void testToJSONString() throws Exception {
        String label = "my \"label'";
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Person owner = createStrictMock(Person.class);
        Corkboard corkboard = new Corkboard(pm);

        expect(owner.getId()).andReturn(4L);

        replay(pm, owner);

        JSONObject jsonObject = new JSONObject( corkboard.toJSONString() );

        System.err.println(jsonObject.toString());
        assertEquals(0, jsonObject.getInt(Corkboard.JSONField.ID.getKey()));
        assertEquals(0, jsonObject.getInt(Corkboard.JSONField.VERSION.getKey()));
        assertEquals(0, jsonObject.getInt(Corkboard.JSONField.WEIGHT.getKey()));
        assertEquals(JSONObject.NULL, jsonObject.get(Corkboard.JSONField.LABEL.getKey()));
        assertEquals(JSONObject.NULL, jsonObject.get(Corkboard.JSONField.OWNER_ID.getKey()));

        corkboard.setId(1);
        corkboard.setVersion(2);
        corkboard.setWeight((short)3);
        corkboard.setLabel(label);
        corkboard.setOwner(owner);

        jsonObject = new JSONObject(corkboard.toJSONString());
        System.err.println(jsonObject.toString());
        assertEquals(1, jsonObject.getInt(Corkboard.JSONField.ID.getKey()));
        assertEquals(2, jsonObject.getInt(Corkboard.JSONField.VERSION.getKey()));
        assertEquals(3, jsonObject.getInt(Corkboard.JSONField.WEIGHT.getKey()));
        assertEquals(label, jsonObject.get(Corkboard.JSONField.LABEL.getKey()));
        assertEquals(4, jsonObject.get(Corkboard.JSONField.OWNER_ID.getKey()));

        verify(pm, owner);
    }

    @Test
    public void testToJSONString_WithException() throws Exception {
        JSONException ex = new JSONException("Yikes!");
        JSONObject jsonObject = createStrictMock(JSONObject.class);
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard = createStrictMock(Corkboard.class,
                Corkboard.class.getDeclaredMethod("newJSONObject"));

        expect(corkboard.newJSONObject()).andReturn(jsonObject);
        jsonObject.put(Corkboard.JSONField.ID.getKey(),(long)0);
        expectLastCall().andThrow(ex);

        replay(pm, jsonObject, corkboard);
        try {
            corkboard.toJSONString();
            fail();
        }
        catch (RuntimeException e) {
            assertEquals(ex, e.getCause());
            // success
        }
        verify(pm, jsonObject, corkboard);
    }
    
}