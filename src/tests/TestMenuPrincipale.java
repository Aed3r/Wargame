package tests;

import javax.swing.*;

import ui.*;

import java.awt.*;
import java.io.*;
import java.util.*;

public class TestMenuPrincipale implements wargame.IConfig {

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
        GenPolice.loadFonts();

        MenuPrincipale m = new MenuPrincipale(); 
        frame.add(m, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
