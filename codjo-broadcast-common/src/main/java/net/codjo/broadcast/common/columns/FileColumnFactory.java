/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class FileColumnFactory {
    private static final String COLUMN_FIELD_NAME = "COLUMN_NAME";
    private static final String BREAK_FIELD_NAME = "BREAK_FIELD";
    private static final String DATE_FORMAT_FIELD_NAME = "COLUMN_DATE_FORMAT";
    private static final String DECIMAL_SEPARATOR_FIELD_NAME = "DECIMAL_SEPARATOR";
    private static final String NUMBER_FORMAT_FIELD_NAME = "COLUMN_NUMBER_FORMAT";

    private boolean hasAlreadyTryExpression = false;
    private boolean hasExpressionCol;


    public FileColumnFactory() {
    }


    public FileColumnGenerator newFileColumnGenerator(final ResultSet rs, final FieldInfo fieldInfo,
                                                      final int sqlType)
    throws SQLException {
        return newFileColumnGenerator(rs, fieldInfo, sqlType, null);
    }


    public FileColumnGenerator newFileColumnGenerator(final ResultSet rs, final FieldInfo fieldInfo,
                                                      final int sqlType, final FunctionHolder funcs)
          throws SQLException {

        String columnName = rs.getString(COLUMN_FIELD_NAME);
        boolean isBreakField = rs.getBoolean(BREAK_FIELD_NAME);
        GeneratorExpression expression = newExpression(rs, sqlType, funcs);
        Padder padder = newPadder(rs);

        switch (sqlType) {
            case Types.VARCHAR:
            case Types.CHAR:
                return new StringColumnGenerator(fieldInfo, columnName, padder, expression, isBreakField);
            case Types.DATE:
            case Types.TIMESTAMP:
                String dateFormat = rs.getString(DATE_FORMAT_FIELD_NAME);
                return new DateColumnGenerator(fieldInfo, columnName, dateFormat, padder, expression,
                                               isBreakField);
            case Types.DOUBLE:
            case Types.INTEGER:
            case Types.DECIMAL:
            case Types.NUMERIC:
                String decimalSeparator = rs.getString(DECIMAL_SEPARATOR_FIELD_NAME);
                String numberFormat = rs.getString(NUMBER_FORMAT_FIELD_NAME);
                return new NumberColumnGenerator(fieldInfo, columnName, decimalSeparator, numberFormat,
                                                 padder, expression, isBreakField);
            case Types.BIT:
                return new BooleanColumnGenerator(fieldInfo, columnName, padder, expression, isBreakField);
            default:
                throw new IllegalArgumentException("Type SQL inconnu : " + sqlType);
        }
    }


    GeneratorExpression newExpression(ResultSet rs, int sqlType, FunctionHolder funcs) throws SQLException {

        if (!hasAlreadyTryExpression) {
            try {
                rs.getString("EXPRESSION");
                hasExpressionCol = true;
            }
            catch (Exception ex) {
                hasExpressionCol = false;
            }

            hasAlreadyTryExpression = true;
        }
        if (!hasExpressionCol) {
            return null;
        }
        String expression = rs.getString("EXPRESSION");
        if (expression != null && !"".equals(expression.trim())) {
            return new GeneratorExpression(rs.getString("EXPRESSION"), sqlType, funcs);
        } else {
            return null;
        }
    }


    private static Padder newPadder(ResultSet rs) throws SQLException {

        String paddingCaracter = rs.getString("PADDING_CARACTER");
        if (rs.getBoolean("FIXED_LENGTH") && paddingCaracter != null) {
            return new Padder(paddingCaracter, rs.getInt("COLUMN_LENGTH"),
                              rs.getBoolean("RIGHT_COLUMN_PADDING"));
        }
        return null;
    }
}
