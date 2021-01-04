package misc;

import java.util.HashMap;
import java.util.Properties;
import java.io.*;

public class Parametres implements wargame.IConfig {
    private static HashMap<String, String> params = new HashMap<String, String>();

    private Parametres() {
        throw new IllegalStateException("Classe Utilitaire");
    }

    /**
     * Charge les paramètres se trouvant dans le fichier des configurations
     */
	public static void loadParametres() {
        try (InputStream in = new FileInputStream(CONFIGFILE)) {
            Properties p = new Properties();
            p.load(in);
            p.forEach( (k,v) -> params.put((String)k, (String)v));
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier de configuration: " + e.getLocalizedMessage());
            System.exit(-1);
        }
    }
    
    /**
     * Renvoie le paramètre au nom donné, ou null si ce dernier n'existe pas
     * @param nom le nom du paramètre
     * @return la valeur du paramètre ou null
     */
    public static String getParametre(String nom) {
        return params.get(nom);
    }

    /**
     * Renvoie le paramètre i se trouvant dans IConfig, ou null si ce dernier n'existe pas
     * @param i l'indice du paramètre
     * @return la valeur du paramètre ou null
     */
    public static String getParametre(int i) {
        return params.get(PARAMETRES[i][0]);
    }
}
