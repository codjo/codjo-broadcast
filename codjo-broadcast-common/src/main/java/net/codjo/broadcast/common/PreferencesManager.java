/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common;
import net.codjo.broadcast.common.diffuser.Diffuser;
import net.codjo.broadcast.common.diffuser.DiffuserManager;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
/**
 * Manager des préférences de diffusion.
 */
public class PreferencesManager {

    private static final String DEFAULT_FILE_TABLENAME = "PM_BROADCAST_FILES";
    private static final String DEFAULT_FILE_CONTENTS_TABLENAME = "PM_BROADCAST_FILE_CONTENTS";
    private static final String DEFAULT_SECTION_TABLENAME = "PM_BROADCAST_SECTION";
    private static final String DEFAULT_COLUMN_TABLENAME = "PM_BROADCAST_COLUMNS";

    private DiffuserManager diffuserManager = new DiffuserManager();
    private Map<String, Preferences> preferences = new HashMap<String, Preferences>();
    private RootContext rootContext;


    public PreferencesManager() {
        this(DEFAULT_FILE_TABLENAME,
             DEFAULT_FILE_CONTENTS_TABLENAME,
             DEFAULT_SECTION_TABLENAME,
             DEFAULT_COLUMN_TABLENAME,
             null);
    }


    /**
     * @deprecated Use default constructor #PreferencesManager().
     */
    @Deprecated
    public PreferencesManager(String fileTableName, String fileContentsTableName,
                              String sectionTableName, String columnTableName) {
        this(fileTableName, fileContentsTableName, sectionTableName, columnTableName, null);
    }


    public PreferencesManager(String fileTableName,
                              String fileContentsTableName,
                              String sectionTableName,
                              String columnTableName,
                              Map<String, Object> variables) {
        this.rootContext =
              new RootContext(fileTableName, fileContentsTableName, sectionTableName,
                              columnTableName, variables);
    }


    /**
     * Retourne un tableau de noms des diffuseurs possibles.
     *
     * @return Le tableau.
     */
    public final String[] getDiffusersCode() {
        return diffuserManager.getDiffusersCode();
    }


    public void addAll(PreferencesManager prefMan) {
        preferences.putAll(prefMan.preferences);
    }


    /**
     * Ajout d'une nouvelle préférence.
     *
     * @param pref
     */
    public void addPreferences(Preferences pref) {
        preferences.put(pref.getFamily(), pref);
    }


    /**
     * Construit le <code>Diffuser</code> approprié.
     *
     * @param diffuserCode
     * @param arg
     *
     * @return un diffuseur
     */
    public Diffuser buildDiffuser(String diffuserCode, String arg) {
        return diffuserManager.buildDiffuser(diffuserCode, arg);
    }


    /**
     * Efface toutes les Préférence initialisés.
     */
    public void clearPreferences() {
        preferences.clear();
    }


    /**
     * Declare une methode de diffusion.
     *
     * @param diffuserCode  code de diffusion.
     * @param diffuserClass class du diffuseur
     *
     * @throws NoSuchMethodException La classe ne possede pas de constructeur avec un argument de type
     *                               String.
     */
    public void declareDiffuser(String diffuserCode, Class<? extends Diffuser> diffuserClass)
          throws NoSuchMethodException {
        diffuserManager.declareDiffuser(diffuserCode, diffuserClass);
    }


    public String getColumnsTableName() {
        return rootContext.getColumnsTableName();
    }


    public String getFileContentsTableName() {
        return rootContext.getFileContentsTableName();
    }


    public String getFileTableName() {
        return rootContext.getFileTableName();
    }


    /**
     * retourne la préférence de diffusion pour une famille.
     *
     * @param family
     *
     * @return une préférence
     *
     * @throws NoSuchElementException
     */
    public Preferences getPreferences(String family) {
        Preferences pref = preferences.get(family);
        if (pref == null) {
            throw new NoSuchElementException("Famille inconnue : " + family);
        }
        return pref;
    }


    public Context getRootContext() {
        return rootContext;
    }


    public String getSectionTableName() {
        return rootContext.getSectionTableName();
    }


    /**
     * Retourne un iterateur sur les <code>Preferences</code> définies.
     */
    public Iterator<Preferences> iterator() {
        return preferences.values().iterator();
    }


    /**
     * Retourne un contexte pour un export particulier. Cette methode est utilisé par la session EJB.
     *
     * @param user           L'utilisateur demandant l'export
     * @param fileName       le fichier a exporter
     * @param generationDate la date de generation.
     * @param broadcastDate  La date utilisé pour la diffusion
     * @param outFolder      Le repertoire de sortie
     *
     * @return un contexte d'export.
     */
    public Context buildContext(final String user, final String fileName,
                                final java.util.Date generationDate, final java.util.Date broadcastDate,
                                final File outFolder) {
        return new Context(new java.sql.Date(broadcastDate.getTime()));
    }
}
