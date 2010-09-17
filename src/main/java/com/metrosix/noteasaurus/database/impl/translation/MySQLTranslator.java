package com.metrosix.noteasaurus.database.impl.translation;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * This implements a SQL translator which converts from our sql format to the MySQL format.  We have used MySQL as the
 * default vendor SQL implementation so this translator should pass any statement through with no operations.
 *
 * @author Russell Francis (russ@metro-six.com)
 */
public class MySQLTranslator extends AbstractSQLTranslator {
    @Override
    public String translateStatement(DatabaseMetaData dbmd, String sql) throws SQLException {
        return sql;
    }
}
