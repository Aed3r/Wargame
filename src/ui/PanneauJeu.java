package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.*;

import misc.Element;
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
    private byte[][][] tabHitbox;
    private Point posSouris;
    private Dimension tailleFenetre = null, tailleVirtuelle = null;
    private Carte carte;

    public PanneauJeu (Carte carte) {
        super();
        this.carte = carte;

        // Initialisations
        setBackground(BGCOLOR);
        addMouseWheelListener(this);
        wPlateau = MARGX*2+TAILLEX*LARGEUR_CARTE+TAILLEX/2; // Largeur du plateau (8866)
        hPlateau = MARGY*2+TAILLEY*(HAUTEUR_CARTE+1); // Hauteur du plateau (4850)
        tabHitbox = new byte[hPlateau][wPlateau][2];

        // Déplacement sur le plateau
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                posSouris = e.getPoint();
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Point curseurMap;
                try { curseurMap = getPosCurseurPlateau(); }
                catch (NullPointerException ex) { return; }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    System.out.println("("+(tabHitbox[curseurMap.y][curseurMap.x][0] & 0xFF) + ", "+(tabHitbox[curseurMap.y][curseurMap.x][1] & 0xFF) +")");
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    Element elem = carte.getElement(tabHitbox[curseurMap.y][curseurMap.x][0], tabHitbox[curseurMap.y][curseurMap.x][1]);
                    if (elem.getVisible()) elem.setCache();
                    else elem.setVisible();
                    elem.setReafficher(true);
                    repaint();
                }
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
     * @return la position du curseur sur le plateau
     * @throws NullPointerException si le curseur se trouve en dehors de la fenêtres
     */
    private Point getPosCurseurPlateau () throws NullPointerException {
        Point curseur = this.getMousePosition();
        if (curseur == null) throw new NullPointerException(); // Lorsque le curseur se trouve en dehors de la fenêtre

        Point pointVirt = new Point((int) (curseur.getX()/zoomPlateau), (int) (curseur.getY()/zoomPlateau));
        return new Point((int) pointVirt.getX()-xPlateau, (int) pointVirt.getY()-yPlateau);
    }

    /**
     * Définie le zoom lorsque la molette est utilisé
     * @param e l'évenement généré
     */
    @Override
	public void mouseWheelMoved(MouseWheelEvent e) {
        // la position du curseur sur le plateau avant le zoom
        Point curseurMap;
        try { curseurMap = getPosCurseurPlateau(); }
        catch (NullPointerException ex) { return; }
        
        zoomPlateau += e.getWheelRotation() * -VITESSEZOOM;
        if (zoomPlateau > MAXZOOM) zoomPlateau = MAXZOOM;

        // la position du curseur sur le plateau après le zoom
        Point curseurMapZoom;
        try { curseurMapZoom = getPosCurseurPlateau(); }
        catch (NullPointerException ex) { return; }

        // On replace le plateau de façon à ce que le curseur reste sur le même point de la carte
        xPlateau += curseurMapZoom.getX() - curseurMap.getX();
        yPlateau += curseurMapZoom.getY() - curseurMap.getY();

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

            // On dessine le plateau sur l'image
            carte.afficher(plateau.getGraphics(), tabHitbox, false);
        } else {
            // On Met à jour le plateau sur l'image
            carte.afficher(plateau.getGraphics(), tabHitbox, true);
        }

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
        if (xPlateau > 0) xPlateau = 0;
        if (-xPlateau > wPlateau-tailleVirtuelle.width) xPlateau = -(wPlateau-tailleVirtuelle.width);
        if (yPlateau > 0) yPlateau = 0;
        if (-yPlateau > hPlateau-tailleVirtuelle.height) yPlateau = -(hPlateau-tailleVirtuelle.height);

        // On affiche la partie du plateau voulu
        g2d.drawImage(plateau, xPlateau, yPlateau, null);

        // On replace l'ancien Transform
        g2d.setTransform(og);
    }
}
