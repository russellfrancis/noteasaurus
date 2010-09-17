package com.metrosix.noteasaurus.database;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: SQLTranslator.java 247 2010-08-07 23:15:10Z adam $
 */
public interface SQLTranslator {
    public String translateStatement(DatabaseMetaData dbmd, String sql) throws SQLException;
}
