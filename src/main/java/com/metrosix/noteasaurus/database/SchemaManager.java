package com.metrosix.noteasaurus.database;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;

/**
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public interface SchemaManager {
    
    /**
     * This method will connect to the database using the provided connection and attempt to
     * install the latest schema into that database.
     * 
     * @return true if we were able to install the schema successfully, false otherwise.
     */
    public boolean install();

    /**
     * This method will return the current schema version.
     *
     * @param connection The database connection to use to query the schema version.
     * @return -1 if the schema could not be determined otherwise the version of the schema the
     * database is currently at.
     */
    public int getSchemaVersion(Connection connection) throws Exception;

    /**
     * This method will read the schema definition from a File.  The format of this file is
     * defined by the implementor of this interface.
     *
     * @param inputFile The file from which to load the schema definition.
     */
    public void readSchema(File inputFile) throws Exception;

    /**
     * This method will read the schema definition from an InputStream.  The format of this
     * InputStream is defined by the implementor.
     *
     * @param ins An InputStream from which to read the schema definition.
     */
    public void readSchema(InputStream ins) throws Exception;
}
