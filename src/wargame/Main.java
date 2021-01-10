package wargame;

import javax.swing.*;
import ui.*;
import java.awt.*;
import java.util.*;
import misc.Parametres;

public class Main implements wargame.IConfig {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Menu Principal Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 

        Locale.setDefault(Locale.FRENCH);

        // On charge les paramètres et la taille enregistré
        Parametres.loadParametres();
        TailleFenetre.setTailleFenetre(Parametres.getParametre(0), frame);
        
        // Evite le redimensionnement 
        frame.setResizable(false);
        // Centre la fenêtre
        frame.setLocationRelativeTo(null);

        // Chargement des polices
        GenPolice.loadFonts();

        MenuPrincipal m = new MenuPrincipal(); 
        frame.add(m, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}