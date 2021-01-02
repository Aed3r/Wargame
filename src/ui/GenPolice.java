package ui;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Classe utilitaire permettant de choisir une police en fonction de ses paramètres
 */
public class GenPolice implements wargame.IConfig {
    private GenPolice() {
        throw new IllegalStateException("Classe Utilitaire");
    }

    /**
     * Crée et renvoie une police
     * @param name le nom de la police
     * @param weight le poids de la police
     * @param isItalic si la police est italique ou non
     * @param size la taille de la police
     * @return la nouvelle police
     */
    public static Font genFont (String name, int weight, boolean isItalic, int size) {
        String fontName = name;
        Font f;

        if (weight <= 100) fontName += " Thin";
        else if (weight <= 200) fontName += " ExtraLight";
        else if (weight <= 300) fontName += " Light";
        else if (weight <= 400) fontName += " Regular";
        else if (weight <= 500) fontName += " Medium";
        else if (weight <= 600) fontName += " SemiBold";
        else if (weight <= 700) fontName += " Bold";
        else if (weight <= 800) fontName += " ExtraBold";
        else if (weight <= 900) fontName += " Black";
        else {
            System.out.println("Erreur lors de la création d'un bouton! " + 
                                weight + " n'est pas un poids valide!");
            fontName += " Regular";
        }
        if (isItalic) f = new Font(fontName, Font.ITALIC, size);
        else f = new Font(fontName, Font.PLAIN, size);

        return f;
    }

    /**
     * Crée et renvoie une police avec une taille par défaut définie dans {@link wargame.IConfig#FONTSIZE}
     * @param name le nom de la police
     * @param weight le poids de la police
     * @param isItalic si la police est italique ou non
     * @return la nouvelle police
     */
    public static Font genFont (String name, int weight, boolean isItalic) {
        return genFont(name, weight, isItalic, FONTSIZE);
    }

    /**
     * Trouve toutes les polices dans data et les charge.<br>
     * Allume également l'anti-aliasage des caractères.
     */
    public static void loadFonts() {
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
}
