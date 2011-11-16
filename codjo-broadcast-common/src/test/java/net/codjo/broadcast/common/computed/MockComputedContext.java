/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.computed;
import java.sql.Date;
import java.util.NoSuchElementException;
/**
 * Description of the Class
 *
 * @author $Author: palmont $
 * @version $Revision: 1.2 $
 */
public class MockComputedContext implements ComputedContext {
    public MockComputedContext() {}

    public String getBroadcastTableName() {
        throw new java.lang.UnsupportedOperationException(
            "La méthode getBroadcastTableName() n'est pas encore implémentée.");
    }


    public String getComputedTableName() {
        return "#COMPUTED";
    }


    public Object getParameter(String parameterName)
            throws NoSuchElementException {
        throw new java.lang.UnsupportedOperationException(
            "La méthode getParameter() n'est pas encore implémentée.");
    }


    public String getSelectionTableName() {
        return "#TEMP";
    }


    public Date getToday() {
        throw new java.lang.UnsupportedOperationException(
            "La méthode getToday() n'est pas encore implémentée.");
    }


    public boolean hasParameter(String parameterName) {
        throw new java.lang.UnsupportedOperationException(
            "La méthode hasParameter() n'est pas encore implémentée.");
    }


    public String joinToBroadcastTable() {
        throw new java.lang.UnsupportedOperationException(
            "La méthode joinToBroadcastTable() n'est pas encore implémentée.");
    }


    public void putParameter(String parameterName, Object value) {
        throw new java.lang.UnsupportedOperationException(
            "La méthode putParameter() n'est pas encore implémentée.");
    }


    public String replaceVariables(String template)
            throws IllegalArgumentException {
        throw new java.lang.UnsupportedOperationException(
            "La méthode replaceVariables() n'est pas encore implémentée.");
    }
}
