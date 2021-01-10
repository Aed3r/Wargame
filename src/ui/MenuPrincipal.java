package ui;

import java.awt.*;
import java.awt.event.*;
import terrains.Carte;

/**
 * Crée le menu principale
 */
public class MenuPrincipal extends MenuSimple {
    private static final long serialVersionUID = 1L;
    
    public MenuPrincipal() {
        super(null);
        Dimension s = new Dimension(300, 75); // Taille des boutons
        
        // Création des boutons
        TranslucentButton[] buttons = {
            new TranslucentButton("Nouveau jeu", s, 500, false),
            new TranslucentButton("Charger un jeu", s, 500, false),
            new TranslucentButton("Options", s, 500, false),
            new TranslucentButton("Quitter", s, 500, false)};

        // Action des boutons
            // Nouveau jeu
        buttons[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Carte c = new Carte();
                c.placementSoldatAlea();
                setMenu(new PanneauJeu(c, MenuPrincipal.this));
            }
        });
            // Charger un jeu
        buttons[1].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setMenu(new MenuChargerJeu(MenuPrincipal.this));
            }
        });
            // Options
        buttons[2].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setMenu(new MenuOptions(MenuPrincipal.this));
            }
        });
            // Quitter
        buttons[3].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        // Placement des boutons
        setButtons(buttons);
        
        // Placement de l'image de fond
        setBgImage("/img/menuPrincipale.jpg");

        // Placement du logo
        setLogo("/img/icon/logo.png", 0.3);
    }
}
