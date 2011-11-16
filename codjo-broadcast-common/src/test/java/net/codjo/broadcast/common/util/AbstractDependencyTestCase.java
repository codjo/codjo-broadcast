/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.util;
import net.codjo.test.common.PathUtil;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import junit.framework.TestCase;
/**
 * Classe abstraite servant a faciliter l'ecriture des tests de dépendance entre package. La classe est basée
 * sur <code>JDepend</code> (dont l'url est <code>http://www.clarkware.com/software/jdepend2.2.zip</code>).
 *
 * <p> Exemple de méthode de test:
 * <pre>
 *  public void test_dependency() {
 *  String[] dependsUpon = {
 *      "net.codjo.orbis.controls.shipment"
 *      , "net.codjo.utils"
 *      , "net.codjo.orbis.utils"
 *      };
 *  assertDependency("net.codjo.orbis.controls", dependsUpon);
 *  assertNoCycle("net.codjo.orbis.controls");
 *  }
 *  </pre>
 * </p>
 */
public abstract class AbstractDependencyTestCase extends TestCase {
    private JDepend jdepend;


    /**
     * Verifie que le package <code>currentPackage</code> n'a comme dépendance directe seulement les package
     * se trouvant dans <code>dependsUpon</code>.
     *
     * @param currentPackage Le package a verifier (ex : "net.codjo.orbis")
     * @param dependsUpon    Tableau de package.
     *
     * @throws IllegalArgumentException TODO
     */
    protected void assertDependency(String currentPackage, String[] dependsUpon) {
        jdepend.analyze();

        JavaPackage testedPack = jdepend.getPackage(currentPackage);
        if (testedPack == null) {
            throw new IllegalArgumentException("Package " + currentPackage
                                               + " est inconnu");
        }
        SortedSet<String> trueDependency = new TreeSet<String>();
        for (Object object : testedPack.getEfferents()) {
            JavaPackage obj = (JavaPackage)object;
            if (!obj.getName().startsWith("java")) {
                trueDependency.add(obj.getName());
            }
        }

        List wantedDepency = Arrays.asList(dependsUpon);
        if (!trueDependency.containsAll(wantedDepency)
            || !wantedDepency.containsAll(trueDependency)) {
            StringWriter strWriter = new StringWriter();
            doTrace(currentPackage, dependsUpon, new PrintWriter(strWriter));
            fail("Contraintes de Dependance non respectée : \n" + strWriter.toString());
        }
    }


    /**
     * Verfie que le package <code>packageName</code> n'a pas de dependance circulaire.
     *
     * @param packageName Nom du package
     */
    protected void assertNoCycle(String packageName) {
        assertEquals("Cycle de dépendance pour " + packageName, false,
                     jdepend.getPackage(packageName).containsCycle());
    }


    @Override
    protected void setUp() throws Exception {
        jdepend = new JDepend();
        String path = PathUtil.findTargetDirectory(this.getClass()) + "/classes";
        jdepend.addDirectory(path);
    }


    /**
     * Trace en cas d'erreur.
     *
     * @param packName    Nom du package
     * @param dependsUpon Tableau de package
     * @param os          flux d'écriture
     */
    private void doTrace(String packName, String[] dependsUpon, PrintWriter os) {
        JavaPackage pack = jdepend.getPackage(packName);
        os.println("********* " + pack.getName());

        os.println("*** Différence "
                   + "(++ nouvelle dépendance / -- dépendance en moins):");

        List<String> oldDependence = new ArrayList<String>(Arrays.asList(dependsUpon));

        printNewDependency(os, pack, oldDependence);

        printOldDependency(os, oldDependence);
    }


    private void printNewDependency(PrintWriter os, JavaPackage pack, List<String> oldDependence) {
        for (Object object : pack.getEfferents()) {
            JavaPackage obj = (JavaPackage)object;
            if (!oldDependence.remove(obj.getName())
                && !obj.getName().startsWith("java")) {
                os.println("  <++> " + obj.getName());
            }
        }
    }


    private void printOldDependency(PrintWriter os, List oldDependence) {
        for (Object anOldDependence : oldDependence) {
            os.println("  <--> " + anOldDependence);
        }
    }
}
