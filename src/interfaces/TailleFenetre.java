package interfaces;

import java.awt.*;
import javax.swing.*;

/**
 * S'occupe de changer la taille de la fenêtre en fonction de la configuration choisi
 */
public class TailleFenetre implements wargame.IConfig {
    private TailleFenetre() {
        throw new IllegalStateException("Classe Utilitaire");
    }

    /**
     * Définit la taille de la fenêtre à l'aide d'un code
     * @param code une des valeurs définies dans IConfig
     * @param fenetre la fenêtre à redimensionner
     * @see wargame.IConfig#PARAMETRES
     */
    public static void setTailleFenetre (String code, JFrame fenetre) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        device.setFullScreenWindow(null);

        if (code.equals(PARAMETRES[0][4])) { // petite
            fenetre.setSize((int) (screenSize.getWidth()*(1/4f)), 
                          (int) (screenSize.getHeight()*(1/4f)));
        } else if (code.equals(PARAMETRES[0][5])) { // moyenne
            fenetre.setSize((int) (screenSize.getWidth()*(1/2f)), 
                          (int) (screenSize.getHeight()*(1/2f)));
        } else if (code.equals(PARAMETRES[0][3])) { // plein écran
            device.setFullScreenWindow(fenetre);
        } else { // grande ou code invalide
            fenetre.setSize((int) (screenSize.getWidth()*(3/4f)), 
                          (int) (screenSize.getHeight()*(3/4f)));
        }
        fenetre.revalidate();
        fenetre.repaint();
    }
}