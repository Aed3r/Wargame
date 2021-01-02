package tests;

import javax.swing.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import interfaces.MenuPrincipale;
import interfaces.TailleFenetre;

public class TestMenuPrincipale implements wargame.IConfig {

    /**
     * Trouve toutes les polices dans data et les charge.<br>
     * Allume également l'anti-aliasage des caractères.
     */
    private static void loadFonts() {
        findFonts(new File("data/font").listFiles());
    }

    /**
     * Cherche récursivement, à partir des fichiers dans files, toutes les polices TTF puis les charge
     * @param files un tableau de fichiers dans lequel chercher
     */
    private static void findFonts (File[] files) {
        for (File file : files) {
            if (file.isDirectory()) {
                findFonts(file.listFiles()); // Appel récursif 
            } else {
                if (Optional.ofNullable(file.getName())
                        .filter(f -> f.contains("."))
                        .map(f -> f.substring(file.getName().lastIndexOf(".") + 1))
                        .filter(f -> f.equals("ttf"))
                        .isPresent()) {
                            loadFont(file);
                        }
            }
        }    
    }

    /**
     * Charge la police
     * @param font le fichier de la police à charger
     * @author https://docs.oracle.com/javase/tutorial/2d/text/fonts.html
     */
    private static void loadFont(File font) {
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, font).deriveFont(12f));
        } catch (IOException|FontFormatException e) {
            System.out.println("Erreur lors du chargement d'une police! '" + font.getName() + "' n'existe pas ou est invalide.");
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Menu Principal Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 

        Locale.setDefault(Locale.FRENCH);

        // On charge la taille enregistré
        try (InputStream in = new FileInputStream(CONFIGFILE)) {
            Properties p = new Properties();
            p.load(in);
            TailleFenetre.setTailleFenetre(p.getProperty(PARAMETRES[0][0]), frame);
        } catch (IOException io) {
            TailleFenetre.setTailleFenetre(PARAMETRES[0][2], frame);
        }
        // Evite le redimensionnement 
        frame.setResizable(false);
        // Centre la fenêtre
        frame.setLocationRelativeTo(null);

        // Chargement des polices
        loadFonts();

        MenuPrincipale m = new MenuPrincipale(); 
        frame.add(m, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
