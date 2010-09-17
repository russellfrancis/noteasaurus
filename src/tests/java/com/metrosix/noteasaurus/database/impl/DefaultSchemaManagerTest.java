package com.metrosix.noteasaurus.database.impl;

import com.metrosix.noteasaurus.database.impl.DefaultSchemaManager;
import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.impl.NonVolatileApplicationConfiguration;
import com.metrosix.noteasaurus.database.ConnectionManager;
import com.metrosix.noteasaurus.database.SQLTranslator;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import org.easymock.classextension.ConstructorArgs;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DefaultSchemaManagerTest.java 247 2010-08-07 23:15:10Z adam $
 */
public class DefaultSchemaManagerTest {

    @Test
    public void testApplySchemaVersion_NullConnection_ThrowsIllegalArgumentException() throws Exception {
        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);
        try {
            sm.applySchemaVersion(null, 1);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testExecuteStatement_NullConnection_ThrowsIllegalArgumentException() throws Exception {
        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);
        try {
            sm.executeStatement(null, "SELECT * FROM table where blah");
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testExecuteStatement_NullStatement_ThrowsIllegalArgumentException() throws Exception {
        Connection conn = createStrictMock(Connection.class);
        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);
        replay(conn);
        try {
            sm.executeStatement(conn, null);
            fail();
        } catch (IllegalArgumentException e) {
            // success
        }
        verify(conn);
    }

    @Test
    public void testIsInitialized_ReturnFalse() throws Exception {
        Connection conn = createStrictMock(Connection.class);
        DefaultSchemaManager sm = createStrictMock(DefaultSchemaManager.class,
                DefaultSchemaManager.class.getDeclaredMethod("getSchemaVersion", Connection.class));

        expect(sm.getSchemaVersion(conn)).andReturn(-1);

        replay(sm, conn);
        assertFalse(sm.isInitialized(conn));
        verify(sm, conn);
    }

    @Test
    public void testIsInitialized_ReturnTrue() throws Exception {
        Connection conn = createStrictMock(Connection.class);
        DefaultSchemaManager sm = createStrictMock(DefaultSchemaManager.class,
                DefaultSchemaManager.class.getDeclaredMethod("getSchemaVersion", Connection.class));

        expect(sm.getSchemaVersion(conn)).andReturn(0);

        replay(sm, conn);
        assertTrue(sm.isInitialized(conn));
        verify(sm, conn);
    }

    public void testSetSchemaVersion_NullConnection_ThrowsException() throws Exception {
        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);
        try {
            sm.setSchemaVersion(null, 4);
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    public void testSetSchemaVersion_IllegalDbVersion_ThrowsException() throws Exception {
        Connection conn = createStrictMock(Connection.class);
        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);
        replay(conn);
        try {
            sm.setSchemaVersion(conn, -1);
        } catch (IllegalArgumentException e) {
            // success
        }
        verify(conn);
    }

    @Test
    public void testReadSchema_NullInputStream_ThrowsException() throws Exception {
        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);
        try {
            sm.readSchema((InputStream)null);
            fail();
        } catch (IllegalArgumentException e) {
            // success.
        }
    }

    @Test
    public void testReadSchema_NullFile_ThrowsException() throws Exception {
        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);
        try {
            sm.readSchema((File)null);
            fail();
        } catch (IllegalArgumentException e) {
            // success.
        }
    }

    @Test
    public void testReadSchema_File() throws Exception {
        
        ConstructorArgs args = new ConstructorArgs(
                DefaultSchemaManager.class.getConstructor(
                    ConnectionManager.class, NonVolatileApplicationConfiguration.class, SQLTranslator.class),
                    null, null, null);
        DefaultSchemaManager sm = createMock(DefaultSchemaManager.class, args,
                DefaultSchemaManager.class.getDeclaredMethod("setConnectionManager", ConnectionManager.class),
                DefaultSchemaManager.class.getDeclaredMethod("setApplicationConfiguration", ApplicationConfiguration.class),
                DefaultSchemaManager.class.getDeclaredMethod("setSQLTranslator", SQLTranslator.class),
                DefaultSchemaManager.class.getDeclaredMethod("newFileInputStream",File.class),
                DefaultSchemaManager.class.getDeclaredMethod("readSchema",InputStream.class)
                );
        File file = createMock(File.class);
        InputStream ins = createMock(InputStream.class);

        expect(sm.newFileInputStream(file)).andReturn(ins);
        sm.readSchema(ins);
        ins.close();

        replay(sm, file, ins);

        sm.setConnectionManager(null);
        sm.setApplicationConfiguration(null);
        sm.setSQLTranslator(null);
        sm.readSchema(file);

        verify(sm, file, ins);
    }

    @Test
    public void testGetApplicationConfiguration() {
        ConnectionManager cm = createMock(ConnectionManager.class);
        NonVolatileApplicationConfiguration appConfig = createMock(NonVolatileApplicationConfiguration.class);
        SQLTranslator translator = createMock(SQLTranslator.class);

        DefaultSchemaManager sm = new DefaultSchemaManager(cm, appConfig, translator);
        assertEquals(cm, sm.getConnectionManager());
        assertEquals(appConfig, sm.getApplicationConfiguration());
        assertEquals(translator, sm.getSSQLTranslator());
    }

    @Test
    public void testStripSqlComments() {
        StringBuilder sql = new StringBuilder("NOTE");
        sql.append("-- This is a comment\n");
        sql.append("ASAURUS");

        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);
        sql = sm.stripSqlComments(sql);
        assertEquals("NOTE\nASAURUS", sql.toString());
    }

    @Test
    public void testGetVersionElementFor() throws Exception {
        ConstructorArgs args = new ConstructorArgs(
                DefaultSchemaManager.class.getConstructor(
                    ConnectionManager.class, NonVolatileApplicationConfiguration.class, SQLTranslator.class),
                    null, null, null);
        DefaultSchemaManager sm = createMock(DefaultSchemaManager.class, args,
                DefaultSchemaManager.class.getDeclaredMethod("setConnectionManager", ConnectionManager.class),
                DefaultSchemaManager.class.getDeclaredMethod("setApplicationConfiguration", ApplicationConfiguration.class),
                DefaultSchemaManager.class.getDeclaredMethod("setSQLTranslator", SQLTranslator.class),
                DefaultSchemaManager.class.getDeclaredMethod("getSchema"),
                DefaultSchemaManager.class.getDeclaredMethod("getNodeByNameAndId", Document.class, String.class, String.class)
                );
        Document doc = createMock(Document.class);
        Element element = createMock(Element.class);

        expect(sm.getSchema()).andReturn(doc).times(2);
        expect(sm.getNodeByNameAndId(doc, "version", "4")).andReturn(element);

        replay(sm, doc, element);

        sm.setConnectionManager(null);
        sm.setApplicationConfiguration(null);
        sm.setSQLTranslator(null);
        assertEquals(element, sm.getVersionElementFor(4));

        verify(sm, doc, element);
    }

    @Test
    public void testGetNodeByNameAndId() {
        Document doc = createStrictMock(Document.class);
        NodeList nodeList = createStrictMock(NodeList.class);
        Element node0 = createStrictMock(Element.class);
        NamedNodeMap nodeMap0 = createStrictMock(NamedNodeMap.class);
        Node attribute0 = createStrictMock(Node.class);
        Element node1 = createStrictMock(Element.class);
        NamedNodeMap nodeMap1 = createStrictMock(NamedNodeMap.class);
        Node attribute1 = createStrictMock(Node.class);

        expect(doc.getElementsByTagName("version")).andReturn(nodeList);
        expect(nodeList.getLength()).andReturn(2);

        expect(nodeList.item(0)).andReturn(node0);
        expect(node0.getAttributes()).andReturn(nodeMap0);
        expect(nodeMap0.getNamedItem("id")).andReturn(attribute0);
        expect(attribute0.getNodeValue()).andReturn("3");

        expect(nodeList.item(1)).andReturn(node1);
        expect(node1.getAttributes()).andReturn(nodeMap1);
        expect(nodeMap1.getNamedItem("id")).andReturn(attribute1);
        expect(attribute1.getNodeValue()).andReturn("4");

        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);

        // Assert we get the right thing back.
        replay(doc, nodeList, node0, node1, attribute0, attribute1, nodeMap0, nodeMap1);
        assertEquals(node1, sm.getNodeByNameAndId(doc, "version", "4"));
        verify(doc, nodeList, node0, node1, attribute0, attribute1, nodeMap0, nodeMap1);
    }

    @Test
    public void testGetNodeByNameAndId_ReturnNull() {
        Document doc = createStrictMock(Document.class);
        NodeList nodeList = createStrictMock(NodeList.class);
        Element node0 = createStrictMock(Element.class);
        NamedNodeMap nodeMap0 = createStrictMock(NamedNodeMap.class);
        Node attribute0 = createStrictMock(Node.class);
        Element node1 = createStrictMock(Element.class);
        NamedNodeMap nodeMap1 = createStrictMock(NamedNodeMap.class);
        Node attribute1 = createStrictMock(Node.class);

        expect(doc.getElementsByTagName("version")).andReturn(nodeList);
        expect(nodeList.getLength()).andReturn(2);

        expect(nodeList.item(0)).andReturn(node0);
        expect(node0.getAttributes()).andReturn(nodeMap0);
        expect(nodeMap0.getNamedItem("id")).andReturn(attribute0);
        expect(attribute0.getNodeValue()).andReturn("3");

        expect(nodeList.item(1)).andReturn(node1);
        expect(node1.getAttributes()).andReturn(nodeMap1);
        expect(nodeMap1.getNamedItem("id")).andReturn(attribute1);
        expect(attribute1.getNodeValue()).andReturn("4");
        
        DefaultSchemaManager sm = new DefaultSchemaManager(null, null, null);

        // Assert we get null back when the item we expect isn't in the list.
        replay(doc, nodeList, node0, node1, attribute0, attribute1, nodeMap0, nodeMap1);
        assertNull(sm.getNodeByNameAndId(doc, "version", "5"));
        verify(doc, nodeList, node0, node1, attribute0, attribute1, nodeMap0, nodeMap1);
    }
}