package ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import terrains.Carte;

public class PanneauJeu extends JPanel implements wargame.IConfig, MouseWheelListener {
    private static final long serialVersionUID = -4874781269011185234L;
    private static final float MAXZOOM = 2f, VITESSEZOOM = 0.1f;
    private Carte carte;
    private int xPlateau = 0, yPlateau = 0, wPlateau, hPlateau;
    private float zoomPlateau = 1;
    private transient BufferedImage plateau;
    private Point posSouris;
    private Dimension tailleFenetre = null, tailleVirtuelle = null;

    public PanneauJeu (Carte carte) {
        super();
        this.carte = carte;
        wPlateau = MARGX*2+TAILLEX*LARGEUR_CARTE+TAILLEX/2; // Largeur du plateau
        hPlateau = MARGY*2+TAILLEY*(HAUTEUR_CARTE+1); // Hauteur du plateau
        addMouseWheelListener(this);

        // On crée l'image sur lequel le plateau sera dessiné
        plateau = new BufferedImage(wPlateau, hPlateau, BufferedImage.TYPE_INT_RGB);
        Graphics g = plateau.getGraphics();
        // On dessine le fond
        g.setColor(BGCOLOR);
        g.fillRect(0, 0, wPlateau, hPlateau);
        // On dessine le plateau sur l'image
        carte.afficher(g);
        g.dispose();

        // Déplacement et zoom sur le plateau
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                posSouris = e.getPoint();
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
     * Restreint le plateau dans la fenêtre
     */
    private void verifPosition () {
        if (xPlateau > 0) xPlateau = 0;
        if (-xPlateau > wPlateau-tailleVirtuelle.width) xPlateau = -(wPlateau-tailleVirtuelle.width);
        if (yPlateau > 0) yPlateau = 0;
        if (-yPlateau > hPlateau-tailleVirtuelle.height) yPlateau = -(hPlateau-tailleVirtuelle.height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tailleFenetre == null) {
            tailleFenetre = ((JFrame) SwingUtilities.getWindowAncestor(this)).getSize();
            tailleVirtuelle = (Dimension) tailleFenetre.clone();
            requestFocusInWindow();
        }

        Graphics2D g2d = (Graphics2D)g;
        AffineTransform og = g2d.getTransform(), at = new AffineTransform(og);

        g2d.setClip(0, 0, tailleFenetre.width, tailleFenetre.height);
        at.scale(zoomPlateau, zoomPlateau);
        g2d.setTransform(at);
        tailleVirtuelle.setSize(g2d.getClipBounds().width, g2d.getClipBounds().height);
        // On vérifie si le zoom est allé trop loin
        if (tailleVirtuelle.width > wPlateau || tailleVirtuelle.height > hPlateau) {
            // On reste à l'ancienne position
            zoomPlateau += VITESSEZOOM;
            repaint();
            return;
        }
        verifPosition();
        g2d.drawImage(plateau, xPlateau, yPlateau, null);

        g2d.setTransform(og);
    }
}
