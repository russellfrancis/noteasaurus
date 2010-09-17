package com.metrosix.noteasaurus.domain;

import com.metrosix.noteasaurus.domain.Corkboard;
import com.metrosix.noteasaurus.domain.Note;
import com.metrosix.noteasaurus.domain.Person;
import com.metrosix.noteasaurus.database.PersistenceManager;
import com.metrosix.noteasaurus.util.SecureUtility;
import java.util.Arrays;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import static org.easymock.classextension.EasyMock.*;
import java.util.HashSet;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: PersonTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class PersonTest {
    
    @Test
    public void testIdAccessorMutator() {
        long id = 4;
        Person person = new Person(null, null, null);
        person.setId(id);
        Assert.assertEquals("The accessor/mutator for the id field doesn't work.",
                id, person.getId());
    }

    @Test
    public void testVersionAccessorMutator() {
        int version = 54;
        Person person = new Person(null, null, null);
        person.setVersion(version);
        Assert.assertEquals("The accessor/mutator for the version field doesn't work.",
            version, person.getVersion());
    }

    @Test
    public void testVerifiedAccessorMutator() {
        boolean bonafide = true;
        Person person = new Person(null, null, null);
        person.setVerified(bonafide);
        Assert.assertTrue(person.isVerified());
    }

    @Test
    public void testCreatedOnAccessorMutator() {
        Date someDate = new Date();
        Person person = new Person(null, null, null);
        Assert.assertNull(person.getCreatedOn());
        person.setCreatedOn(someDate);
        Assert.assertEquals(person.getCreatedOn(), someDate);
    }

    @Test
    public void testSetSecretKey() {
        Person person = new Person(null, null, new SecureUtility());
        byte[] secretKey = "Hello World".getBytes();
        person.setSecretKey(secretKey);
        Assert.assertTrue( Arrays.equals(secretKey, person.getSecretKey() ) );
    }

    @Test
    public void testSetPassword() throws Exception {
        String pass = "thisIsAt3st";
        Person person = new Person(null, null, new SecureUtility());
        person.setPassword(pass);
        Assert.assertEquals(DigestUtils.shaHex(pass).toUpperCase(), person.getPassword());
    }
    
    @Test
    public void testSetPasswordWithHashedPassword() throws Exception {
        String pass = DigestUtils.shaHex("thisIsAt3st").toUpperCase();
        Person person = new Person(null, null, new SecureUtility());
        person.setPassword(pass);
        Assert.assertEquals(pass, person.getPassword());
    }
    
    @Test
    public void testSetNullPassword() throws Exception {
        String pass = null;
        Person person = new Person(null, null, new SecureUtility());
        person.setPassword(pass);
        Assert.assertEquals(person.getPassword(), pass);
    }
    
    @Test
    public void testNameAccessor() {
        String email = "russell.francis@gmail.com";
        Person person = new Person(null, null, null);
        person.setEmail(email);
        Assert.assertEquals(email, person.getName());
    }
    
    @Test
    public void testEmailAccessorMutator() {
        String mail = "noteasaurus.support@gmail.com";
        Person person = new Person(null, null, null);
        person.setEmail(mail);
        Assert.assertEquals(mail, person.getEmail());
    }
    
    @Test
    public void testSetCorkboards() {
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard1 = new Corkboard(pm);
        Corkboard corkboard2 = new Corkboard(pm);
        HashSet corkSet = new HashSet();
        corkSet.add(corkboard1);
        corkSet.add(corkboard2);
        Person person = new Person(null, null, null);
        person.setCorkboards(corkSet);
        Assert.assertEquals(person.getCorkboards(), corkSet);
    }

    @Test
    public void testAddCorkboardToSetOfCorkboards() {
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard1 = new Corkboard(pm);
        Corkboard corkboard2 = new Corkboard(pm);
        Corkboard corkboard3 = new Corkboard(pm);
        HashSet corkSet = new HashSet();
        corkSet.add(corkboard1);
        corkSet.add(corkboard2);
        Person person = new Person(null, null, null);
        person.setCorkboards(corkSet);
        Assert.assertTrue(person.addCorkboard(corkboard3));
    }
    
    @Test
    public void testRemoveCorkboardFromSetOfCorkboards() {
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard1 = new Corkboard(pm);
        Corkboard corkboard2 = new Corkboard(pm);
        HashSet corkSet = new HashSet();
        corkSet.add(corkboard1);
        corkSet.add(corkboard2);
        Person person = new Person(null, null, null);
        person.setCorkboards(corkSet);
        Assert.assertTrue(person.removeCorkboard(corkboard2));
    }
    
    @Test
    public void testFailureOfRemoveCorkboardFromSetOfCorkboards() {
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Corkboard corkboard1 = new Corkboard(pm);
        Corkboard corkboard2 = new Corkboard(pm);
        HashSet corkSet = new HashSet();
        corkSet.add(corkboard1);
        corkSet.add(corkboard2);
        Person person = new Person(null, null, null);
        person.setCorkboards(corkSet);
        Assert.assertFalse(person.removeCorkboard(null));
    }

    @Test
    public void testToJSONString() throws Exception {
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Person owner = new Person(pm, null, null);

        replay(pm);

        JSONObject jsonObject = new JSONObject( owner.toJSONString() );
        
        System.err.println(jsonObject.toString());
        Assert.assertEquals(0, jsonObject.getLong(Person.JSONField.ID.getKey()));
        Assert.assertEquals(0, jsonObject.getInt(Person.JSONField.VERSION.getKey()));
        Assert.assertEquals(JSONObject.NULL, jsonObject.get(Person.JSONField.EMAIL.getKey()));
        
        long id = 4L;
        int version = 2;
        String email = "noteasaurus.support@gmail.com";
        
        owner.setId(id);
        owner.setVersion(version);
        owner.setEmail(email);

        jsonObject = new JSONObject(owner.toJSONString());
        System.err.println(jsonObject.toString());
        Assert.assertEquals(id, jsonObject.getLong(Person.JSONField.ID.getKey()));
        Assert.assertEquals(version, jsonObject.getInt(Person.JSONField.VERSION.getKey()));
        Assert.assertEquals(email, jsonObject.get(Person.JSONField.EMAIL.getKey()));
        
        verify(pm);
    }

    @Test
    public void testNoteToJSONString_WithException() throws Exception {
        JSONException ex = new JSONException("Yikes!");
        JSONObject jsonObject = createStrictMock(JSONObject.class);
        
        PersistenceManager pm = createStrictMock(PersistenceManager.class);
        Person owner = createStrictMock(Person.class,
                Person.class.getDeclaredMethod("newJSONObject"));
        Corkboard corkboard = new Corkboard(pm);
        Note note = new Note(pm, null);

        expect(owner.newJSONObject()).andReturn(jsonObject);
        jsonObject.put(Person.JSONField.ID.getKey(),(long)0);
        expectLastCall().andThrow(ex);

        replay(pm, jsonObject, owner);
        try {
            owner.toJSONString();
            Assert.fail();
        }
        catch (RuntimeException e) {
            Assert.assertEquals(ex, e.getCause());
            // success
        }
        verify(pm, jsonObject, owner);
    }
    
}