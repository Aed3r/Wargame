package ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;
import javax.swing.*;

/**
 * Menu affichant tous les paramètres présentes dans IConfig
 * @see wargame.IConfig
 */
public class MenuOptions extends MenuSimple implements wargame.IConfig {
    private static final long serialVersionUID = 1L;
    // Nombre d'essais pour créer le fichier de configuration maximal (<999)
    private static final int NBESSAISMAX = 5; 

    /**
     * Construit un menu affichant les paramètres se trouvant dans IConfig
     * @param parent le menu créant celui-ci, auquel revenir en appuyant sur le bouton "Retour"
     */
    public MenuOptions(MenuSimple parent) {
        super();
        Dimension s = new Dimension(300, 75); // Taille des boutons
        int nbEssais = 0, i, nbParam = PARAMETRES.length;
        String nomParam, valParam;

        // On réutilise l'image chargé par le menu parent
        setBgImage(parent.getBgImage());

        while(nbEssais < NBESSAISMAX) {
            try (InputStream in = new FileInputStream(CONFIGFILE)) {
                // Chargement des paramètres actuel
                Properties p = new Properties();
                p.load(in);

                // Création des boutons pour chaque paramètre
                TranslucentButton[] buttons = new TranslucentButton[nbParam+1];
                for (i = 0; i < nbParam; i++) {
                    // Vérification des valeurs
                    if (PARAMETRES[i].length < 3) {
                        System.out.println("Erreur, propriété n°" + (i+1) + " invalide");
                        continue;
                    }
                    // Nom du paramètre
                    nomParam = PARAMETRES[i][0];
                    // On définit la valeur par défaut si le paramètre n'existe pas
                    valParam = p.getProperty(nomParam);
                    if (valParam == null) {
                        valParam = PARAMETRES[i][2];
                        p.setProperty(PARAMETRES[i][0], valParam);
                    }
                    // Définition du bouton
                    buttons[i] = new TranslucentButton(PARAMETRES[i][1] + ": " + valParam, s, 500, false);
                    
                    // Action
                    buttons[i].addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            // On cherche la valeur actuellement utilisé
                            int i = ((TranslucentButton) e.getSource()).getID(),
                                l = PARAMETRES[i].length, j = 2;
                            String nomParam = PARAMETRES[i][0];
                            while (j < l && !p.getProperty(nomParam).equals(PARAMETRES[i][j])) j++;

                            // On repasse à la première valeur si rien n'a été trouvé ou qu'il s'agit de la dernière 
                            if (j >= l-1) j = 2;
                            else j++; // Sinon on passe simplement à la prochaine valeur

                            // On modifie le paramètre et le texte du bouton
                            p.setProperty(nomParam, PARAMETRES[i][j]);
                            buttons[i].setText(PARAMETRES[i][1] + ": " + PARAMETRES[i][j]);

                            // Modification de la taille de fenêtre immédiate
                            if (i == 0)
                                TailleFenetre.setTailleFenetre(PARAMETRES[i][j], (JFrame) SwingUtilities.getWindowAncestor(MenuOptions.this));
                        }
                    });
                }

                // Bouton de retour
                buttons[nbParam] = new TranslucentButton("Retour", s, 500, false);
                buttons[nbParam].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // On enregistre les changements dans le fichier de configuration
                        try (OutputStream out = new FileOutputStream(CONFIGFILE)) {
                            p.store(out, null);
                        } catch (IOException io) {
                            System.err.println("Erreur lors de l'écriture vers le fichier de configuration! " + io.getLocalizedMessage());
                        }
                        // On ajuste le menu parent aux potentiels changement de taille
                        parent.setBounds(0, 0, getWidth(), getHeight());
                        // On repasse au menu précédent
                        parent.setMenu(parent);
                    }
                });

                // Placement des boutons
                setButtons(buttons);

                nbEssais = 999;
            } catch (IOException e) {
                // On crée le fichier de configuration s'il n'existe pas
                try { 
                    File newFile = new File(CONFIGFILE);
                    if (!newFile.createNewFile()) throw new IOException();
                } catch (IOException io) {
                    System.err.println("Erreur lors de la création du fichier de configurations: " + io.getLocalizedMessage());
                    System.exit(-1);
                }
                nbEssais++;
            }
        }

        if (nbEssais == NBESSAISMAX) {
            System.out.println("Erreur lors du chargement du fichier de configurations");
            System.exit(-1);
        }
    }
}
