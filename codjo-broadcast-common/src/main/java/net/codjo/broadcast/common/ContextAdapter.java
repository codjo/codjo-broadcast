/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import java.io.File;
import java.sql.Date;
/**
 * Classe permettant d'adapter un contexte.
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
class ContextAdapter {
    private Context ctxt;

    ContextAdapter(Context context) {
        ctxt = context;
    }

    public Object getParameter(String paramName) {
        return ctxt.getParameter(paramName);
    }


    public Date getToday() {
        return ctxt.getToday();
    }


    public boolean hasParameter(String paramName) {
        return ctxt.hasParameter(paramName);
    }


    public void putParameter(String name, Object value) {
        ctxt.putParameter(name, value);
    }


    public File replaceVariables(File file) {
        return ctxt.replaceVariables(file);
    }


    public String replaceVariables(String template) {
        return ctxt.replaceVariables(template);
    }
}
