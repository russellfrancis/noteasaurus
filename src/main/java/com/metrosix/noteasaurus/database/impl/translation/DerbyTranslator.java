package com.metrosix.noteasaurus.database.impl.translation;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DerbyTranslator.java 247 2010-08-07 23:15:10Z adam $
 */
public class DerbyTranslator extends AbstractSQLTranslator {
    /**
     * @TODO -- We need to implement this to support Derby.
     */
    public String translateStatement(DatabaseMetaData dbmd, String sql) throws SQLException {
        return sql;
    }
}
