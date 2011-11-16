/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.gui;
import net.codjo.mad.common.structure.FieldStructure;
/**
 * Description of the Class
 *
 * @author $Author: GONNOT $
 * @version $Revision: 1.2 $
 */
public class GuiField implements Comparable {
    private String fieldName;
    private String joinKeyName;
    private String label;


    public GuiField(String tableName, FieldStructure structure) {
        this(tableName, structure.getSqlName(), structure.getLabel());
    }


    public GuiField(String joinKeyName, String fieldName, String label) {
        this.fieldName = fieldName;
        this.joinKeyName = joinKeyName;
        this.label = label;
    }


    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }


    public void setJoinKeyName(String joinKeyName) {
        this.joinKeyName = joinKeyName;
    }


    public void setLabel(String label) {
        this.label = label;
    }


    public String getFieldName() {
        return fieldName;
    }


    public String getJoinKeyName() {
        return joinKeyName;
    }


    public String getLabel() {
        return label;
    }


    public int compareTo(Object field) {
        return getLabel().compareTo(((GuiField)field).getLabel());
    }


    @Override
    public String toString() {
        return getLabel();
    }
}
