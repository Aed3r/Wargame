package ui;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import terrains.Carte;

public class PanneauJeu extends JPanel implements wargame.IConfig {
    private static final long serialVersionUID = -4874781269011185234L;
    private Carte carte;
    private ContentPane jeu;

    private class ContentPane extends JPanel {
        private static final long serialVersionUID = 8629488155048805255L;
        private Dimension taille;

        public ContentPane() {
            super();
            taille = new Dimension(MARGX*2+TAILLEX*LARGEUR_CARTE+TAILLEX/2,
                               MARGY*2+TAILLEY*(HAUTEUR_CARTE+1));
            setSize(taille);
            setBackground(BGCOLOR);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            carte.afficher(g);
        }
    }

    public PanneauJeu (Carte carte) {
        this.carte = carte;
        setBackground(BGCOLOR);
        jeu = new ContentPane();
        JScrollPane scrollPane = new JScrollPane(jeu);

        add(scrollPane, BorderLayout.CENTER);
    }
}
