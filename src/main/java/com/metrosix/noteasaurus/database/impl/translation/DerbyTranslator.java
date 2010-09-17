package com.metrosix.noteasaurus.database.impl.translation;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class DerbyTranslator extends AbstractSQLTranslator {
    /**
     * @TODO -- We need to implement this to support Derby.
     */
    @Override
    public String translateStatement(DatabaseMetaData dbmd, String sql) throws SQLException {
        return sql;
    }
}
