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
        try (InputStream in = Parametres.class.getResourceAsStream(CONFIGFILE)) {
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

    /**
     * Change le paramètre nomParam. Ne vérifie pas s'il s'agit d'un paramètre valide
     * @param nomParam le nom du paramètre à changer
     * @param valeur la nouvelle valeur du paramètre
     */
    public static void setParametre(String nomParam, String valeur) {
        Properties p = new Properties();

        try (InputStream in = Parametres.class.getResourceAsStream(CONFIGFILE)) {
            p.load(in);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier de configuration: " + e.getLocalizedMessage());
            System.exit(-1);
        }

        p.setProperty(nomParam, valeur);
        params.put(nomParam, valeur);

        try (OutputStream out = new FileOutputStream("data/config.properties")) {
            p.store(out, null);
        } catch (IOException io) {
            System.err.println("Erreur lors de l'écriture vers le fichier de configuration! " + io.getLocalizedMessage());
        }
        try (OutputStream out = new FileOutputStream("bin/config.properties")) {
            p.store(out, null);
        } catch (IOException io) {
            System.err.println("Erreur lors de l'écriture vers le fichier de configuration! " + io.getLocalizedMessage());
        }
    }
}
