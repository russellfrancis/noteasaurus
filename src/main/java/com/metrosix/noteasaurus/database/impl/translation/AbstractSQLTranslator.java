package com.metrosix.noteasaurus.database.impl.translation;

import com.metrosix.noteasaurus.database.SQLTranslator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Russell Francis (russ@metro-six.com)
 */
abstract public class AbstractSQLTranslator implements SQLTranslator {
    static private final Pattern createTablePattern = 
            Pattern.compile("^\\s*CREATE\\s+(TEMPORARY\\s+)?TABLE", Pattern.CASE_INSENSITIVE);
    static private final Pattern enginePattern =
            Pattern.compile("\\)\\s*ENGINE\\s*=\\s*\\S+\\s*$", Pattern.CASE_INSENSITIVE);

    protected boolean isCreateTableStatement(String sql) {
        Matcher matcher = createTablePattern.matcher(sql);
        return matcher.find();
    }

    protected String stripEngineClause(String sql) {
        if (isCreateTableStatement(sql)) {
            Matcher matcher = enginePattern.matcher(sql);
            if (matcher.find()) {
                sql = matcher.replaceFirst(")");
            }
        }
        return sql;
    }
}
