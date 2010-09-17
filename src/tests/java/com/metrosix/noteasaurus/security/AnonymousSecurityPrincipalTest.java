package com.metrosix.noteasaurus.security;

import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Note;
import com.metrosix.noteasaurus.domain.Person;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class AnonymousSecurityPrincipalTest {

    @Test
    public void testGetName() {
        AnonymousSecurityPrincipal asp = new AnonymousSecurityPrincipal();
        assertNotNull(asp.getName());
        assertTrue(asp.getName().length() > 0);
    }

    @Test
    public void testCanWriteClass() {
        AnonymousSecurityPrincipal asp = new AnonymousSecurityPrincipal();
        assertFalse(asp.canWrite(Person.class));
        assertFalse(asp.canWrite(Note.class));
        assertFalse(asp.canWrite(Corkboard.class));
    }

    @Test
    public void testCanReadClass() {
        AnonymousSecurityPrincipal asp = new AnonymousSecurityPrincipal();
        // This allows someone to go from Anonymous to logged in, to do this the anonymous user has to read a Person
        // instance from the system.
        assertTrue(asp.canRead(Person.class));
        assertFalse(asp.canRead(Note.class));
        assertFalse(asp.canRead(Corkboard.class));
        assertFalse(asp.canRead((Class)null));
    }

    @Test
    public void testCanWriteResource() {
        Person person = new Person(null, null, null);
        Note note = new Note(null, null);
        Corkboard corkboard = new Corkboard(null);
        AnonymousSecurityPrincipal asp = new AnonymousSecurityPrincipal();
        assertFalse(asp.canWrite(person));
        assertFalse(asp.canWrite(note));
        assertFalse(asp.canWrite(corkboard));
        assertFalse(asp.canWrite((Class)null));
    }

    @Test
    public void testCanReadResource() {
        Person person = new Person(null, null, null);
        Note note = new Note(null, null);
        Corkboard corkboard = new Corkboard(null);
        AnonymousSecurityPrincipal asp = new AnonymousSecurityPrincipal();
        assertFalse(asp.canRead(person));
        assertFalse(asp.canRead(note));
        assertFalse(asp.canRead(corkboard));
    }

    @Test
    public void testCanWriteResources() {
        Person person = new Person(null, null, null);
        Collection persons = new ArrayList<Person>();
        persons.add(person);

        Note note = new Note(null, null);
        Collection notes = new ArrayList<Note>();
        notes.add(note);

        Corkboard corkboard = new Corkboard(null);
        Collection corkboards = new ArrayList<Corkboard>();
        corkboards.add(corkboard);

        AnonymousSecurityPrincipal asp = new AnonymousSecurityPrincipal();
        assertFalse(asp.canWrite(persons));
        assertFalse(asp.canWrite(notes));
        assertFalse(asp.canWrite(corkboards));
    }

    @Test
    public void testCanReadResources() {
        Person person = new Person(null, null, null);
        Collection persons = new ArrayList<Person>();
        persons.add(person);

        Note note = new Note(null, null);
        Collection notes = new ArrayList<Note>();
        notes.add(note);

        Corkboard corkboard = new Corkboard(null);
        Collection corkboards = new ArrayList<Corkboard>();
        corkboards.add(corkboard);

        AnonymousSecurityPrincipal asp = new AnonymousSecurityPrincipal();
        assertFalse(asp.canRead(persons));
        assertFalse(asp.canRead(notes));
        assertFalse(asp.canRead(corkboards));
    }
}