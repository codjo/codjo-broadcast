/*
 * codjo.net
 *
 * Common Apache License 2.0
 */
package net.codjo.broadcast.common.diffuser;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
/**
 * Gestionnaire des modules de diffusion.
 */
public class DiffuserManager {
    private Map<String, Class<? extends Diffuser>> diffuserMap = new TreeMap<String, Class<? extends Diffuser>>();


    public DiffuserManager() {
        diffuserMap.put("NONE", null);
        try {
            declareDiffuser("CFT", CFTDiffuser.class);
        }
        catch (NoSuchMethodException ex) {
            // Impossible
            Logger.getLogger(DiffuserManager.class).info("Impossible de déclarer le diffuseur CFT", ex);
        }
    }


    /**
     * Construction du diffuseur.
     *
     * @param diffuserCode Code du diffuseur
     * @param arg          argument de construction
     *
     * @return le diffuseur.
     *
     * @throws IllegalArgumentException diffuseur inconnue
     */
    public Diffuser buildDiffuser(String diffuserCode, String arg) {
        try {
            Class clazz = getDiffuserClass(diffuserCode);
            if (clazz == null) {
                return null;
            }
            Constructor constructor = getConstructor(clazz);
            return (Diffuser)constructor.newInstance(arg);
        }
        catch (NoSuchMethodException ex) {
            // Cas normalement impossible
            throw new IllegalArgumentException("Ca merde grave dans le noyau " + diffuserCode, ex);
        }
        catch (InvocationTargetException ex) {
            throw new IllegalArgumentException("Echec lors de l'initialisation "
                                               + "de la méthode de diffusion " + diffuserCode + " (" + arg
                                               + ") - "
                                               + ex.getTargetException().getLocalizedMessage(),
                                               ex);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Echec lors de l'initialisation "
                                               + "de la méthode de diffusion " + diffuserCode + " (" + arg
                                               + ") - "
                                               + ex.getLocalizedMessage(),
                                               ex);
        }
    }


    /**
     * Declare une methode de diffusion.
     *
     * @param diffuserCode  code de diffusion.
     * @param diffuserClass class du diffuseur
     *
     * @throws NoSuchMethodException    La classe ne possede pas de constructeur avec un argument de type String.
     * @throws IllegalArgumentException Le diffuseur est deja defini
     */
    public void declareDiffuser(String diffuserCode, Class<? extends Diffuser> diffuserClass)
          throws NoSuchMethodException {
        getConstructor(diffuserClass);
        if (diffuserMap.containsKey(diffuserCode)) {
            throw new IllegalArgumentException("Le diffuseur " + diffuserCode + " est deja defini!");
        }
        diffuserMap.put(diffuserCode, diffuserClass);
    }


    public String[] getDiffusersCode() {
        return diffuserMap.keySet().toArray(new String[]{});
    }


    private Constructor getConstructor(Class clazz) throws NoSuchMethodException {
        return clazz.getDeclaredConstructor(String.class);
    }


    private Class<? extends Diffuser> getDiffuserClass(String diffuserCode) {
        if (!diffuserMap.containsKey(diffuserCode)) {
            throw new IllegalArgumentException("Code de diffusion inconnu " + diffuserCode);
        }
        return diffuserMap.get(diffuserCode);
    }
}
