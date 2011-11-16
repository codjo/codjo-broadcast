/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
/**
 * Test du <code>Context</code>.
 */
public class ContextTest extends TestCase {
    private Map<String, Object> params;


    /**
     * Test que l'on peut connecter deux contextes.
     */
    public void test_connectTo() {
        // Creation du contexte initial
        Map<String, Object> initial = new HashMap<String, Object>();
        initial.put("var.a", "A");
        initial.put("var.b", "B");
        Context initCtxt = new Context(initial);

        // Creation du contexte courant
        Map<String, Object> current = new HashMap<String, Object>();
        current.put("var.a", "OVERRIDED by current");
        current.put("autre", "AUTRE");
        Context currentCtxt = new Context(current);

        // Connecte au contexte initial
        currentCtxt.connectTo(initCtxt);

        // CurrentCtxt est la fusion des 2 ctxt
        assertEquals(3, currentCtxt.getParameters().size());
        assertEquals("OVERRIDED by current", currentCtxt.getParameters().get("var.a"));
        assertEquals("B", currentCtxt.getParameters().get("var.b"));
        assertEquals("AUTRE", currentCtxt.getParameters().get("autre"));

        // initCtxt est inchangé
        assertEquals(initial, initCtxt.getParameters());
    }


    /**
     * Test que getParameter fonctionne et lance une exception si le parametre est indefini.
     */
    public void test_getParameter() {
        Context ctxt = new Context(params);

        assertEquals("A", ctxt.getParameter("var.a"));
        try {
            ctxt.getParameter("unknown");
            fail("Le parametre unknown n'est pas definit");
        }
        catch (Exception ex) {
        }
        ctxt.putParameter("temp", null);
        assertNull(ctxt.getParameter("temp"));
    }


    /**
     * Test que les paramtres sont bien protege par le contexte.
     */
    public void test_getParameters() {
        Context ctxt = new Context(params);

        assertTrue(params != ctxt.getParameters());
        assertEquals(params, ctxt.getParameters());

        Map<String, Object> copyDeParams = new HashMap<String, Object>(params);
        params.clear();
        assertEquals(copyDeParams, ctxt.getParameters());
    }


    public void test_hasParameter() {
        Context ctxt = new Context(params);

        assertTrue(ctxt.hasParameter("var.a"));
        assertTrue(!ctxt.hasParameter("unknown"));

        ctxt.putParameter("unknown", "xxx");
        assertTrue(ctxt.hasParameter("unknown"));
    }


    /**
     * Test que le contexte accepte la definition de nouvelle variable.
     */
    public void test_put_newParameter() {
        Context ctxt = new Context(params);
        ctxt.putParameter("var.x", "x");

        assertEquals("le x et B", ctxt.replaceVariables("le $var.x$ et $var.b$"));
    }


    /**
     * Test que le contexte accepte la re-definition de nouvelle variable.
     */
    public void test_put_newParameter_override() {
        Context ctxt = new Context(params);
        ctxt.putParameter("var.x", "x");
        ctxt.putParameter("var.x", "y");

        assertEquals("le y et B", ctxt.replaceVariables("le $var.x$ et $var.b$"));
    }


    /**
     * Test que le contexte refuse la re-definition de variable d'origine. Une variable d'origine est une
     * variable definit a la creation du contexte.
     */
    public void test_put_override_immutable_Parameter() {
        Context ctxt = new Context(params);
        try {
            ctxt.putParameter("var.a", "x");
            fail("La surdefinition de la variable est interdite car le ctxte"
                 + " est construite avec une definition de var.a");
        }
        catch (Exception ex) {
        }
        assertEquals("le A et B", ctxt.replaceVariables("le $var.a$ et $var.b$"));
    }


    /**
     * Test que le contexte remplace correctement les parties variables.
     */
    public void test_replaceVariables() {
        Context initCtxt = new Context(params);

        Map<String, Object> current = new HashMap<String, Object>();
        current.put("today", new Date());
        Context ctxt = new Context(current);
        ctxt.connectTo(initCtxt);

        assertEquals("le A et B", ctxt.replaceVariables("le $var.a$ et $var.b$"));
    }


    /**
     * Teste le remplacement des variables du fichier templateFile par les valeurs appropriées.
     *
     * @throws Exception Description of the Exception
     */
    public void test_replaceVariables_File() throws Exception {
        File templateFile = new File("$var.a$/aFile");
        File expectedFile = new File("A/aFile");

        Context initCtxt = new Context(params);

        assertEquals(expectedFile, initCtxt.replaceVariables(templateFile));
    }


    @Override
    protected void setUp() throws java.lang.Exception {
        params = new HashMap<String, Object>();
        params.put("var.a", "A");
        params.put("var.b", "B");
    }
}
