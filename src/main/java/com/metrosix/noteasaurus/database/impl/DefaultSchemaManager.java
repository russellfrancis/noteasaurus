package com.metrosix.noteasaurus.database.impl;

import com.metrosix.noteasaurus.config.ApplicationConfiguration;
import com.metrosix.noteasaurus.config.impl.NonVolatileApplicationConfiguration;
import com.metrosix.noteasaurus.database.ConnectionManager;
import com.metrosix.noteasaurus.database.SQLTranslator;
import com.metrosix.noteasaurus.database.SchemaManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements a SchemaManager which can be used to apply schema versions to a configured
 * database.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class DefaultSchemaManager implements SchemaManager {
    static private final Logger log = LoggerFactory.getLogger(DefaultSchemaManager.class);
    
    private ConnectionManager connectionManager;
    private ApplicationConfiguration applicationConfiguration;
    private SQLTranslator sqlTranslator;
    private Document schema;

    /**
     * Construct a new instance of the schema manager.
     *
     * @param connectionManager The ConnectionManager to use for this instance.
     * @param applicationConfiguration  The ApplicationConfiguration to use for this instance.
     */
    public DefaultSchemaManager(
            ConnectionManager connectionManager, 
            NonVolatileApplicationConfiguration applicationConfiguration,
            SQLTranslator sqlTranslator)
    {
        setConnectionManager(connectionManager);
        setApplicationConfiguration(applicationConfiguration);
        setSQLTranslator(sqlTranslator);
    }

    /**
     * Read the schema from the '/schema.xml' file within the classpath.  This file contains
     * information regarding the tables and columns that the application expects to be available
     * in the database that we connect to.
     */
    protected void readSchema()
            throws ParserConfigurationException, IOException, SAXException
    {
        InputStream ins = getClass().getResourceAsStream("/schema.xml");
        try {
            readSchema(ins);
        } finally {
            ins.close();
        }
    }

    /**
     * Read the schema from the provided file within the classpath.  This file should contain
     * information regarding the tables and columns that the application expects to be available
     * in the database that we connect to.

     * @param schemaXml The File under which the schema definition for the database is contained.
     */
    @Override
    public void readSchema(File schemaXml)
            throws FileNotFoundException, ParserConfigurationException, SAXException, IOException
    {
        if (schemaXml == null) {
            throw new IllegalArgumentException("The parameter schemaXml must be non-null.");
        }

        InputStream ins = newFileInputStream(schemaXml);
        try {
            readSchema(ins);
        } finally {
            ins.close();
        }
    }

    /**
     * Construct a new FileInputStream from the given file.
     * @param file The file whose input stream we want.
     * @return A FileInputStream for the given file.
     */
    protected InputStream newFileInputStream( File file ) throws FileNotFoundException {
        return new FileInputStream( file );
    }

    /**
     * Read the schema from the provided InputStream.  This InputStream should contain
     * information regarding the tables and columns that the application expects to be available
     * in the database that we connect to.

     * @param ins The InputStream to read the database schema information from.
     */
    @Override
    public void readSchema(InputStream ins) throws ParserConfigurationException, SAXException, IOException
    {
        if (ins == null) {
            throw new IllegalArgumentException("The parameter ins must be non-null.");
        }

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(ins);
        setSchema(doc);
    }

    /**
     * This method will install the schema into the connected database.  This include creating any
     * tables as well as modifying any tables which are needed to migrate the database to an
     * expected state.
     *
     * @return true if we successfully installed the database schema into the database, false if
     * there was a failure while trying to do this.
     */
    @Override
    public boolean install()
    {
        try {
            Connection connection = getNewConnection();
            try {
                try {
                    if (!isInitialized(connection)) {
                        initializeSchema(connection);
                    }
                    while(applySchemaVersion(connection, getSchemaVersion(connection) + 1)){
                        // do it again!
                    }
                    return true;
                } finally {
                    try {
                        connection.commit();
                    } catch (Exception e) {
                        connection.rollback();
                    }
                }
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.toString(), e);
            }
        }
        return false;
    }

    /**
     * This method will query the database for the connection and return the
     * current version that the schema is at.
     *
     * TODO this doesn't rollback or handle failure gracefully.
     * 
     * @return -1 if the schema could not be determined otherwise the version
     * of the schema the database is currently at.
     */
    @Override
    public int getSchemaVersion(Connection connection) throws SQLException, ClassNotFoundException
    {
        int dbVersion = -1;
        PreparedStatement stmt = connection.prepareStatement("SELECT version FROM db_version");
        try {
            ResultSet rs = stmt.executeQuery();
            try {
                if (rs.next()) {
                    dbVersion = rs.getInt(1);
                    if (rs.next()) {
                        throw new IllegalStateException("It appears that there is more than one " +
                                "record in the db_version table!");
                    }
                }
            } finally {
                rs.close();
            }
        } catch (SQLException e) {
            // Perhaps the table doesn't exist.
        } finally {
            stmt.close();
        }

        return dbVersion;
    }

    /**
     * This method will set the schema version in the db_version table within the database.
     *
     * @param connection The database connection on which to set the schema version.
     * @param dbVersion The schema version that we wish to set in the database.
     */
    protected void setSchemaVersion(Connection connection, int dbVersion)
            throws SQLException, ClassNotFoundException
    {
        if (connection == null) {
            throw new IllegalArgumentException("The parameter connection must be non-null.");
        }
        if (dbVersion < 0) {
            throw new IllegalArgumentException("The parameter dbVersion must be >= 0");
        }
        
        PreparedStatement stmt = null;
        if (getSchemaVersion(connection) == -1) {
            stmt = connection.prepareStatement("INSERT INTO db_version (version) VALUES (?)");
        } else {
            stmt = connection.prepareStatement("UPDATE db_version SET version = ?");
        }
        
        try {
            stmt.setInt(1, dbVersion);
            int result = stmt.executeUpdate();
            if (result != 1) {
                throw new IllegalStateException("It appears that there is more than one record " +
                        "in the db_version table!");
            }
        } finally {
            stmt.close();
        }
    }

    /**
     * Determine if the database has been initialized.  This determines if the internal table
     * db_version has been created yet.
     * 
     * @return true if the database has been initialized and the table db_version could be found,
     * false otherwise.
     */
    protected boolean isInitialized(Connection connection) throws SQLException, ClassNotFoundException
    {
        try {
            return getSchemaVersion(connection) >= 0;
        } catch (SQLSyntaxErrorException e) {
            // There is a chicken and egg problem with derby, we get a syntax 
            // exception unless we create a schema "CREATE SCHEMA [SCHEMA-NAME]" or
            // implicitly create the schema with a "CREATE TABLE ..." unfortunately,
            // we get an exception if we call either of these and it has already 
            // been done ... so we catch this here and treat it as though the
            // database is uninitialized.
        }
        return false;
    }

    /**
     * This method will initialize the schema, creating a table db_version where we can store the
     * schema version number which was last successfully applied to the database.
     */
    protected void initializeSchema(Connection connection)
            throws SQLException, ClassNotFoundException
    {
        // Create the db_version table.
        PreparedStatement stmt = connection.prepareStatement("CREATE TABLE db_version (version INTEGER NOT NULL)");
        try {
            stmt.execute();

            // Create the initial version number.
            setSchemaVersion(connection, 0);
        } finally {
            stmt.close();
        }
    }

    /**
     * This method will execute the provided statement against the configured database connection.
     *
     * @param connection The database Connection instance on which to execute the statement.
     * @param sql The SQL which we wish to execute against the given database.
     */
    protected void executeStatement(Connection connection, String sql)
            throws SQLException, ClassNotFoundException
    {
        if (connection == null) {
            throw new IllegalArgumentException("The parameter connection must be non-null.");
        }
        if (sql == null) {
            throw new IllegalArgumentException("The parameter sql must be non-null.");
        }
        
        sql = sql.trim();
        if (sql.length() > 0) {
            PreparedStatement stmt = connection.prepareStatement(sql);
            try {
                log.warn("SQL STATEMENT: " + sql);
                stmt.execute();
            } finally {
                stmt.close();
            }
        }
    }

    /**
     * This method will return the Element of the XML document which has the id of the provided
     * version number or null if no such element could be found.
     *
     * @param version The version whose schema descriptor we wish to fetch.
     * @return The Element in the DOM under which the schema descriptor information for the
     * requested version may be found.
     */
    protected Element getVersionElementFor(int version)
            throws ParserConfigurationException, IOException, SAXException
    {
        if (getSchema() == null) {
            readSchema();
        }

        String versionId = Integer.toString(version);
        return getNodeByNameAndId(getSchema(), "version", versionId);
    }

    /**
     * This method will return the Element within the provided document which have the provided nodeName and an id
     * attribute which matches the provided id.  This method will return null if no such element is found.
     *
     * @param doc The XML Document we wish to search.
     * @param nodeName The name of the node we are interested in.
     * @param id The value of the id of the node we want to retrieve.
     * @return
     */
    protected Element getNodeByNameAndId(Document doc, String nodeName, String id) {
        NodeList versions = doc.getElementsByTagName("version");
        int len = versions.getLength();
        for (int i = 0; i < len; ++i) {
            Element node = (Element) versions.item(i);
            if (id.equals(node.getAttributes().getNamedItem("id").getNodeValue())) {
                return node;
            }
        }
        return null;
    }

    /**
     * Given a node of an XML document tree, this method will return all of the child text.
     *
     * @param node The node whose child text we wish to have.
     * @return The child text of the provided node.
     */
    protected StringBuilder getChildText(Element node) {
        StringBuilder s = new StringBuilder();
        NodeList sqlContent = node.getChildNodes();
        for (int j = 0; j < sqlContent.getLength(); ++j) {
            Node content = sqlContent.item(j);
            if (content.getNodeType() == Node.CDATA_SECTION_NODE) {
                s.append(content.getNodeValue());
            } else if (content.getNodeType() == Node.TEXT_NODE) {
                s.append(content.getNodeValue());
            }
        }

        return s;
    }

    /**
     * This method will strip SQL comments from a text string.
     *
     * @param sqlString The SQL string which may or may not contain comeents of the form '--'.
     * @return A new String which does not contain the comments.
     */
    protected StringBuilder stripSqlComments(StringBuilder sqlString) {
        int index;
        while ((index = sqlString.indexOf("--")) > 0) {
            int endIndex = sqlString.indexOf("\n", index);
            sqlString.replace(index, endIndex, "");
        }
        return sqlString;
    }

    /**
     * This method will apply the version defined in the schema XML document to the database.  This
     * method will return false if no such version is defined in the schema XML document.
     *
     * @param connection The database Connection instance on which to apply the schema version.
     * @param version An integer representing the version of the database schema which we wish to
     * apply to the currently configured database.
     * @return true if we were able to successfully apply the database version, false if the
     * database version requested could not be found in the schema xml document.
     */
    protected boolean applySchemaVersion(Connection connection, int version)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException,
            SQLException, ParserConfigurationException, IOException, SAXException 
    {
        if (connection == null) {
            throw new IllegalArgumentException("The parameter connection must be non-null.");
        }
        log.info("Applying version '" + version + "' to database.");
        
        Element versionElement = getVersionElementFor(version);

        if (versionElement != null) {
            NodeList sqlNodes = versionElement.getElementsByTagName("sql");
            for (int i = 0; i < sqlNodes.getLength(); ++i) {
                Element sql = (Element) sqlNodes.item(i);

                // get child text.
                StringBuilder sqlString = getChildText(sql);

                // strip out comments.
                sqlString = stripSqlComments(sqlString);

                String[] sqlStatements = sqlString.toString().split(";;");
                if (sqlStatements != null && sqlStatements.length > 0) {
                    for (String sqlStatement : sqlStatements) {
                        // modify statement based on database
                        sqlStatement = getSSQLTranslator().translateStatement(connection.getMetaData(), sqlStatement);
                        executeStatement(connection, sqlStatement);
                    }

                    // bump our version number.
                    setSchemaVersion(connection, version);
                } else {
                    throw new SQLException("We found a sql statement block with no statements, " +
                            "this doesn't make sense.");
                }
            }
            
            // the commit the transaction.
            connection.commit();
            return true;
        }
        log.info("versionElement is null.");
        return false;
    }

    /**
     * This method will return a new database connection regardless of the state of any previously
     * returned connections from {@see DefaultSchemaManager#getConnection()}.
     *
     * @return A new Connection to the currently configured database.
     */
    protected Connection getNewConnection() throws ClassNotFoundException, SQLException {
        return getConnectionManager().getConnection();
    }

    /**
     * Get the XML Document which represents the schema we should apply to this database.
     *
     * @return An XML document which represents the schema we should use for this database.
     */
    protected Document getSchema() {
        return schema;
    }

    /**
     * Set the schema which we should apply to the database for this instance.
     *
     * @param aSchema An XML document representing the schema we should apply to this instance.
     */
    protected void setSchema(Document aSchema) {
        schema = aSchema;
    }

    /**
     * Get the ConnectionManager for this instance.
     *
     * @return The ConnectionManager for this instance.
     */
    protected ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * Set the ConnectionManager for this instance.
     *
     * @param aConnectionManager The ConnectionManager to use for this instance.
     */
    protected void setConnectionManager(ConnectionManager aConnectionManager)
    {
        connectionManager = aConnectionManager;
    }

    /**
     * Get the ApplicationConfiguration instance to use for this instance.
     *
     * @return The ApplicationConfiguration instance for this instance.
     */
    protected ApplicationConfiguration getApplicationConfiguration() {
        return applicationConfiguration;
    }

    /**
     * Set the application configuration instance for this.
     *
     * @param anApplicationConfiguration An ApplicationConfiguration instance to use for this.
     */
    protected void setApplicationConfiguration(ApplicationConfiguration anApplicationConfiguration)
    {
        applicationConfiguration = anApplicationConfiguration;
    }

    /**
     * @return the sqlTranslator
     */
    public SQLTranslator getSSQLTranslator() {
        return sqlTranslator;
    }

    /**
     * @param sqlTranslator the sqlTranslator to set
     */
    public void setSQLTranslator(SQLTranslator sqlTranslator) {
        this.sqlTranslator = sqlTranslator;
    }
}
