/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import java.util.Map;

class RootContext extends Context {
    private String columnsTableName;
    private String fileContentsTableName;
    private String fileTableName;
    private String sectionTableName;


    RootContext(String fileTableName,
                String fileContentsTableName,
                String sectionTableName,
                String columnsTableName) {
        this(fileTableName, fileContentsTableName, sectionTableName, columnsTableName,
             null);
    }


    RootContext(String fileTableName,
                String fileContentsTableName,
                String sectionTableName,
                String columnsTableName,
                Map<String, Object> map) {
        super(map);
        if (fileTableName == null
            || fileContentsTableName == null
            || sectionTableName == null
            || columnsTableName == null) {
            throw new NullPointerException();
        }
        else {
            this.fileTableName = fileTableName;
            this.sectionTableName = sectionTableName;
            this.columnsTableName = columnsTableName;
            this.fileContentsTableName = fileContentsTableName;

            putParameter("broadcast.fileTable", fileTableName);
            putParameter("broadcast.fileContentsTable", fileContentsTableName);
            putParameter("broadcast.sectionTable", sectionTableName);
            putParameter("broadcast.columnsTable", columnsTableName);
        }
    }


    public String getColumnsTableName() {
        return columnsTableName;
    }


    public String getFileContentsTableName() {
        return fileContentsTableName;
    }


    public String getFileTableName() {
        return fileTableName;
    }


    public String getSectionTableName() {
        return sectionTableName;
    }
}
