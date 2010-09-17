package com.metrosix.noteasaurus.database;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public interface SQLTranslator {
    public String translateStatement(DatabaseMetaData dbmd, String sql) throws SQLException;
}
