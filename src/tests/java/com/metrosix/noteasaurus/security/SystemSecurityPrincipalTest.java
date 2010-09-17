package com.metrosix.noteasaurus.security;

import com.metrosix.noteasaurus.domain.Person;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class SystemSecurityPrincipalTest {
    @Test
    public void testGetName() {
        SystemSecurityPrincipal principal = new SystemSecurityPrincipal();
        assertNotNull(principal.getName());
    }

    @Test
    public void testCanRead() {
        SystemSecurityPrincipal principal = new SystemSecurityPrincipal();
        assertTrue(principal.canRead(Person.class));
    }

    @Test
    public void testCanWrite() {
        SystemSecurityPrincipal principal = new SystemSecurityPrincipal();
        assertTrue(principal.canWrite(Person.class));
    }

    @Test
    public void testCanReadResource() {
        Person person = createStrictMock(Person.class);
        SystemSecurityPrincipal principal = new SystemSecurityPrincipal();

        replay(person);
        assertTrue(principal.canRead(person));
        verify(person);
    }

    @Test
    public void testCanWriteResource() {
        Person person = createStrictMock(Person.class);
        SystemSecurityPrincipal principal = new SystemSecurityPrincipal();

        replay(person);
        assertTrue(principal.canWrite(person));
        verify(person);
    }

    @Test
    public void testCanReadResourceCollection() {
        Person personA = new Person(null, null, null);
        Person personB = new Person(null, null, null);
        SystemSecurityPrincipal principal = new SystemSecurityPrincipal();

        Collection col = new ArrayList<Person>();
        col.add( personA );
        col.add( personB );

        assertTrue(principal.canRead(col));
    }

    @Test
    public void testCanWriteResourceCollection() {
        Person personA = new Person(null, null, null);
        Person personB = new Person(null, null, null);
        SystemSecurityPrincipal principal = new SystemSecurityPrincipal();

        Collection col = new ArrayList<Person>();
        col.add( personA );
        col.add( personB );

        assertTrue(principal.canWrite(col));
    }
}
