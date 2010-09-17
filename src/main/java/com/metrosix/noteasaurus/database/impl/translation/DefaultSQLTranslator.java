package com.metrosix.noteasaurus.database.impl.translation;

import com.metrosix.noteasaurus.database.SQLTranslator;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author Russell Francis (russell.francis@gmail.com)
 * @version $Id: DefaultSQLTranslator.java 247 2010-08-07 23:15:10Z adam $
 */
public class DefaultSQLTranslator implements SQLTranslator {

    private MySQLTranslator mysqlTranslator;
    private PostgreSQLTranslator postgresqlTranslator;
    private DerbyTranslator derbyTranslator;

    public DefaultSQLTranslator(
            MySQLTranslator mysqlTranslator,
            PostgreSQLTranslator postgresqlTranslator,
            DerbyTranslator derbyTranslator)
    {
        setMysqlTranslator(mysqlTranslator);
        setPostgresqlTranslator(postgresqlTranslator);
        setDerbyTranslator(derbyTranslator);
    }

    public String translateStatement(DatabaseMetaData dbmd, String sql) throws SQLException {
        if (isMySQL(dbmd)) {
            return getMysqlTranslator().translateStatement(dbmd, sql);
        } else if (isPostgreSQL(dbmd)) {
            return getPostgresqlTranslator().translateStatement(dbmd, sql);
        } else if (isDerby(dbmd)) {
            return getDerbyTranslator().translateStatement(dbmd, sql);
        }

        throw new SQLException("The database '" + dbmd.getDatabaseProductName() + "' does not currently have a " +
                "SQLTranslator implemented for it yet.");
    }

    protected boolean isMySQL(DatabaseMetaData dbmd) throws SQLException {
        return dbmd.getDatabaseProductName().trim().toUpperCase().contains("MYSQL");
    }

    protected boolean isPostgreSQL(DatabaseMetaData dbmd) throws SQLException {
        return dbmd.getDatabaseProductName().trim().toUpperCase().contains("POSTGRESQL");
    }

    protected boolean isDerby(DatabaseMetaData dbmd) throws SQLException {
        return dbmd.getDatabaseProductName().trim().toUpperCase().contains("DERBY");
    }

    public MySQLTranslator getMysqlTranslator() {
        return mysqlTranslator;
    }

    public void setMysqlTranslator(MySQLTranslator mysqlTranslator) {
        this.mysqlTranslator = mysqlTranslator;
    }

    public PostgreSQLTranslator getPostgresqlTranslator() {
        return postgresqlTranslator;
    }

    public void setPostgresqlTranslator(PostgreSQLTranslator postgresqlTranslator) {
        this.postgresqlTranslator = postgresqlTranslator;
    }

    public DerbyTranslator getDerbyTranslator() {
        return derbyTranslator;
    }

    public void setDerbyTranslator(DerbyTranslator derbyTranslator) {
        this.derbyTranslator = derbyTranslator;
    }
}
