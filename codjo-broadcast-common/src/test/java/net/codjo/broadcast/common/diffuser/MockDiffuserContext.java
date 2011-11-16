/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.diffuser;
import java.io.File;
import java.sql.Date;
import java.util.NoSuchElementException;
/**
 * DOCUMENT ME!
 *
 * @author $Author: gonnot $
 * @version $Revision: 1.1.1.1 $
 */
public class MockDiffuserContext implements DiffuserContext {
    public MockDiffuserContext() {}

    public Object getParameter(String parameterName)
            throws NoSuchElementException {
        throw new java.lang.UnsupportedOperationException(
            "La méthode getParameter() n'est pas encore implémentée.");
    }


    public Date getToday() {
        throw new java.lang.UnsupportedOperationException(
            "La méthode getToday() n'est pas encore implémentée.");
    }


    public boolean hasParameter(String parameterName) {
        throw new java.lang.UnsupportedOperationException(
            "La méthode hasParameter() n'est pas encore implémentée.");
    }


    public void putParameter(String parameterName, Object value) {
        throw new java.lang.UnsupportedOperationException(
            "La méthode putParameter() n'est pas encore implémentée.");
    }


    public File replaceVariables(File templateFile)
            throws IllegalArgumentException {
        return templateFile;
    }


    public String replaceVariables(String template)
            throws IllegalArgumentException {
        throw new java.lang.UnsupportedOperationException(
            "La méthode replaceVariables() n'est pas encore implémentée.");
    }
}
