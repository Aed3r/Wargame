package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Currency;

import javax.swing.*;
import terrains.Carte;

/**
 * Panneau permettant d'afficher un plateau de jeu
 */
public class PanneauJeu extends JPanel implements wargame.IConfig, MouseWheelListener {
    private static final long serialVersionUID = -4874781269011185234L;
    private static final float MAXZOOM = 2f, VITESSEZOOM = 0.1f;
    private int xPlateau = -MARGX, yPlateau = -MARGY, wPlateau, hPlateau;
    private double zoomPlateau = 1;
    private transient BufferedImage plateau, fond;
    private Point posSouris;
    private Dimension tailleFenetre = null, tailleVirtuelle = null;
    private Carte carte;

    public PanneauJeu (Carte carte) {
        super();
        wPlateau = MARGX*2+TAILLEX*LARGEUR_CARTE+TAILLEX/2; // Largeur du plateau
        hPlateau = MARGY*2+TAILLEY*(HAUTEUR_CARTE+1); // Hauteur du plateau
        addMouseWheelListener(this);
        this.carte = carte;

        setBackground(BGCOLOR);

        // Déplacement sur le plateau
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                posSouris = e.getPoint();
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                plateau.getGraphics().drawOval((int) ((e.getX()-xPlateau)*zoomPlateau), (int) ((e.getX()-yPlateau)*zoomPlateau), 20, 20);
                repaint();
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - posSouris.x;
                int dy = e.getY() - posSouris.y;

                xPlateau = xPlateau + dx;
                yPlateau = yPlateau + dy;

                posSouris = e.getPoint();
                repaint();
            }
        });
    }

    /**
     * Définie le zoom lorsque la molette est utilisé
     * @param e l'évenement généré
     */
    @Override
	public void mouseWheelMoved(MouseWheelEvent e) {
        zoomPlateau += e.getWheelRotation() * -VITESSEZOOM;
        if (zoomPlateau > MAXZOOM) zoomPlateau = MAXZOOM;
        repaint();
    }

    /**
     *  Affiche le plateau sur le panneau
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d;

        // Initialisation lors du premier affichage
        if (tailleFenetre == null) {
            tailleFenetre = ((JFrame) SwingUtilities.getWindowAncestor(this)).getSize();
            tailleVirtuelle = (Dimension) tailleFenetre.clone();
            requestFocusInWindow();

            // On crée l'image sur laquelle le plateau sera dessiné
            plateau = new BufferedImage(wPlateau, hPlateau, BufferedImage.TYPE_INT_ARGB);

            // On dessine le fond
            fond = new BufferedImage(tailleFenetre.width, tailleFenetre.height, BufferedImage.TYPE_INT_RGB);
            g2d = (Graphics2D) fond.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, BGCOLOR.brighter(), 0, tailleFenetre.height, BGCOLOR.darker());
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, tailleFenetre.width, tailleFenetre.height);
            g2d.dispose();
        }

        // On dessine le plateau sur l'image
        carte.afficher(plateau.getGraphics());

        // On affiche le fond
        g2d = (Graphics2D) g;
        g.drawImage(fond, 0, 0, null);

        // On vérifie si le dézoom n'est pas allé trop loin
        double zoomMin;
        if (tailleFenetre.width > tailleFenetre.height)
            zoomMin = (double) tailleFenetre.width / wPlateau;
        else
            zoomMin = (double) tailleFenetre.height / hPlateau;
        
        if (zoomPlateau < zoomMin) zoomPlateau = zoomMin;

        // On récupère un nouveau buffer pour effectuer un zoom
        AffineTransform og = g2d.getTransform(), at = new AffineTransform(og);
        at.scale(zoomPlateau, zoomPlateau);
        g2d.setTransform(at);
        tailleVirtuelle.setSize(tailleFenetre.width/zoomPlateau, tailleFenetre.height/zoomPlateau);

        // On vérifie si la position n'est pas allé trop loin
        /*if (xPlateau > 0) xPlateau = 0;
        if (-xPlateau > wPlateau-tailleVirtuelle.width) xPlateau = -(wPlateau-tailleVirtuelle.width);
        if (yPlateau > 0) yPlateau = 0;
        if (-yPlateau > hPlateau-tailleVirtuelle.height) yPlateau = -(hPlateau-tailleVirtuelle.height);*/

        // On affiche la partie du plateau voulu
        g2d.drawImage(plateau, xPlateau, yPlateau, null);

        // On replace l'ancien Transform
        g2d.setTransform(og);
    }
}
