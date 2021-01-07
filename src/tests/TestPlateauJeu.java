package tests;

import javax.swing.*;

import ui.*;

import java.awt.*;
import java.util.*;
import misc.Parametres;
import terrains.Carte;

public class TestPlateauJeu {
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

        Carte c = new Carte();
        c.placementSoldatAlea();
        frame.add(new PanneauJeu(c, null), BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
