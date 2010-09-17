package com.metrosix.noteasaurus.database.impl.translation;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
public class PostgreSQLTranslator extends AbstractSQLTranslator {

    static private final Pattern autoIncrementPattern = Pattern.compile(
            "([\\(|,])(.*?)(INT|INTEGER|BIGINT)(.*?)AUTO_INCREMENT(.*?)([\\)|,])",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    static private final Pattern textDataTypePattern = Pattern.compile(
            "([\\(|,])(.*?)(?:TINY|MEDIUM|LONG)(TEXT)(.*?)([\\)|,])",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Override
    public String translateStatement(DatabaseMetaData dbmd, String sql) throws SQLException {
        sql = stripEngineClause(sql);
        sql = stripAutoIncrement(sql);
        sql = rewriteTextDataTypes(sql);
        return sql;
    }

    protected String rewriteTextDataTypes(String sql) {
        if (isCreateTableStatement(sql)) {
            boolean foundMatch = false;
            do {
                Matcher m = textDataTypePattern.matcher(sql);
                if (m.find()) {
                    int startIndex = m.start();
                    int endIndex = m.end();
                    StringBuilder s = new StringBuilder("");
                    for (int i = 1; i <= m.groupCount(); ++i) {
                        String group = m.group(i);
                        if (group != null) {
                            s.append(group);
                        }
                    }

                    sql = sql.substring(0, startIndex) + s.toString() + sql.substring(endIndex);
                    foundMatch = true;
                } else {
                    foundMatch = false;
                }
            } while (foundMatch);
        }
        return sql;
    }

    protected String stripAutoIncrement(String sql) {
        if (isCreateTableStatement(sql)) {
            boolean foundMatch = true;
            while (foundMatch) {
                Matcher m = autoIncrementPattern.matcher(sql);
                if (m.find()) {
                    int startIndex = m.start();
                    int endIndex = m.end();
                    StringBuilder s = new StringBuilder("");
                    for (int i = 1; i <= m.groupCount(); ++i) {
                        String group = m.group(i);
                        if (group != null) {
                            if (group.equalsIgnoreCase("INT") || group.equalsIgnoreCase("INTEGER")) {
                                s.append("SERIAL");
                            } else if (group.equalsIgnoreCase("BIGINT")) {
                                s.append("BIGSERIAL");
                            } else {
                                s.append(group);
                            }
                        }
                    }

                    sql = sql.substring(0, startIndex) + s.toString() + sql.substring(endIndex);
                } else {
                    foundMatch = false;
                }
            }
        }
        return sql;
    }
}
