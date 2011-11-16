/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.columns;
import net.codjo.sql.builder.FieldInfo;
/**
 * Classe responsable de l'extraction et du formatage de donnees de type <code>String</code> .
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class FakeColumnGenerator extends AbstractFileColumnGenerator {
    public FakeColumnGenerator(FieldInfo fieldInfo) {
        super(fieldInfo, fieldInfo.getDBFieldName(), null, null, false);
    }


    @Override
    protected String format(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }
}
