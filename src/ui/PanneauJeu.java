package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import misc.Parametres;

import javax.imageio.ImageIO;
import javax.swing.*;
import terrains.Carte;
import misc.Position;

/**
 * Panneau permettant d'afficher un plateau de jeu
 */
public class PanneauJeu extends JPanel implements wargame.IConfig, MouseWheelListener {
    private static final long serialVersionUID = -4874781269011185234L;
    private static final float MAXZOOM = 2f, VITESSEZOOM = 0.1f;
    private int xPlateau = -MARGX, yPlateau = -MARGY, wPlateau, hPlateau;
    private double zoomPlateau = 1;
    private transient BufferedImage plateau = null, fond;
    private byte[][][] tabHitbox;
    private Point posSouris;
    private Dimension tailleFenetre = null, tailleVirtuelle = null;
    private Carte carte;
    private ArrayList<TranslucentButton> boutonsJeu = new ArrayList<>();
    private ArrayList<TranslucentButton> boutonsMenu = new ArrayList<>();
    private boolean afficherMenu = false;
    private transient Position pos1;

    public PanneauJeu(Carte carte, MenuSimple parent) {
        super();
        this.carte = carte;
        Dimension s = new Dimension(300, 75); // Taille des boutons
        TranslucentButton tmp = null;
        GridBagConstraints gc;
        ImageIcon icon;

        // Initialisations
        setOpaque(false);
        setLayout(new GridBagLayout());
        addMouseWheelListener(this);
        wPlateau = MARGX * 2 + TAILLEX * LARGEUR_CARTE + TAILLEX / 2; // Largeur du plateau (8866)
        hPlateau = MARGY * 2 + TAILLEY * (HAUTEUR_CARTE + 1); // Hauteur du plateau (4850)
        tabHitbox = new byte[hPlateau][wPlateau][2];

        /* Boutons du jeu */

        // Menu
        gc = new GridBagConstraints();
        InputStream stream = getClass().getResourceAsStream("/img/icon/settings.png");
        try {
            icon = new ImageIcon(ImageIO.read(stream));
            tmp = new TranslucentButton(icon, new Dimension(45, 45));
        } catch (IOException e1) {
            stream = null;
        }

        if (stream == null) tmp = new TranslucentButton("Menus", new Dimension(100, 100), 400, false);

        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // On cache tous les boutons du jeu
                for (int i = 0; i < boutonsJeu.size(); i++) boutonsJeu.get(i).setVisible(false);
                // On affiche tous les boutons du menu
                for (int i = 0; i < boutonsMenu.size(); i++) boutonsMenu.get(i).setVisible(true);
                afficherMenu = true;
                repaint();
            }
        });
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.insets = new Insets(10,10,0,0);
        add(tmp, gc);
        boutonsJeu.add(tmp);

        /* Boutons du menu */
        
        // Retour au jeu
        tmp = new TranslucentButton("Retour au jeu", s, 400, false);
        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // On affiche tous les boutons du jeu
                for (int i = 0; i < boutonsJeu.size(); i++) boutonsJeu.get(i).setVisible(true);
                // On cache tous les boutons du menu
                for (int i = 0; i < boutonsMenu.size(); i++) boutonsMenu.get(i).setVisible(false);
                afficherMenu = false;
                repaint();
            }
        });

        gc.insets = new Insets(10,10,10,10);
        gc.gridy = 1;
        gc.anchor = GridBagConstraints.CENTER;
        add(tmp, gc);
        tmp.setVisible(false);
        boutonsMenu.add(tmp);

        // Retour au menu
        tmp = new TranslucentButton("Menu principal", s, 400, false);
        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setMenu(parent);
            }
        });

        gc.insets = new Insets(10,10,10,10);
        gc.gridy = 2;
        gc.anchor = GridBagConstraints.CENTER;
        add(tmp, gc);
        tmp.setVisible(false);
        boutonsMenu.add(tmp);

        // Taille
        tmp = new TranslucentButton("Taille: " + Parametres.getParametre("tailleFenetre"), s, 400, false);
        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // On cherche la valeur actuellement utilisé
                int l = PARAMETRES[0].length, j = 2;
                while (j < l && !Parametres.getParametre("tailleFenetre").equals(PARAMETRES[0][j])) j++;
                // On repasse à la première valeur si rien n'a été trouvé ou qu'il s'agit de la dernière 
                if (j >= l-1) j = 2;
                else j++; // Sinon on passe simplement à la prochaine valeur
                // On modifie le texte du bouton et le paramètre
                Parametres.setParametre("tailleFenetre", PARAMETRES[0][j]);
                boutonsMenu.get(2).setText(PARAMETRES[0][1] + ": " + PARAMETRES[0][j]);
                // Modification de la taille de fenêtre
                TailleFenetre.setTailleFenetre(PARAMETRES[0][j], (JFrame) SwingUtilities.getWindowAncestor(PanneauJeu.this));
                tailleFenetre = null;
                revalidate();
                repaint();
            }
        });

        gc.insets = new Insets(10,10,10,10);
        gc.gridy = 3;
        gc.anchor = GridBagConstraints.CENTER;
        add(tmp, gc);
        tmp.setVisible(false);
        boutonsMenu.add(tmp);

        // Sauvegarder
        tmp = new TranslucentButton("Sauvegarder", s, 400, false);
        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // TODO
            }
        });

        gc.insets = new Insets(10,10,10,10);
        gc.gridy = 4;
        gc.anchor = GridBagConstraints.CENTER;
        add(tmp, gc);
        tmp.setVisible(false);
        boutonsMenu.add(tmp);

        // Quitter le jeu
        tmp = new TranslucentButton("Quitter le jeu", s, 400, false);
        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
        });

        gc.insets = new Insets(10,10,10,10);
        gc.gridy = 5;
        gc.anchor = GridBagConstraints.CENTER;
        add(tmp, gc);
        tmp.setVisible(false);
        boutonsMenu.add(tmp);
        
        // Déplacement sur le plateau
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(afficherMenu) return;

                posSouris = e.getPoint();
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if(afficherMenu) return;

                Point curseurMap;
                try { curseurMap = getPosCurseurPlateau(); }
                catch (NullPointerException ex) { return; }
                Position pos = new Position(tabHitbox[curseurMap.y][curseurMap.x][0] & 0xFF, tabHitbox[curseurMap.y][curseurMap.x][1] & 0xFF);

                if (pos1 == null) pos1 = pos;
                else {
                    System.out.println("trying action");
                    if (carte.actionHeros(pos1, pos)) {
                        System.out.println("succeeded");
                        carte.getElement(pos1).setReafficher(true);
                        carte.getElement(pos).setReafficher(true);
                        repaint();
                    } else System.out.println("failed");
                    pos1 = null;
                } 
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(afficherMenu) return;

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
        if(afficherMenu) return;
        
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
        Graphics2D g2d;

        // Initialisation lors du premier affichage
        if (tailleFenetre == null) {
            tailleFenetre = ((JFrame) SwingUtilities.getWindowAncestor(this)).getSize();
            tailleVirtuelle = (Dimension) tailleFenetre.clone();
            requestFocusInWindow();

            // On dessine le fond (trop couteux en mémoire)
            /*fond = new BufferedImage(tailleFenetre.width, tailleFenetre.height, BufferedImage.TYPE_INT_RGB);
            g2d = (Graphics2D) fond.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, BGCOLOR.brighter(), 0, tailleFenetre.height, BGCOLOR.darker());
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, tailleFenetre.width, tailleFenetre.height);
            g2d.dispose();*/

            if (plateau == null) {
                // On crée l'image sur laquelle le plateau sera dessiné
                plateau = new BufferedImage(wPlateau, hPlateau, BufferedImage.TYPE_INT_RGB);
                // On dessine le plateau sur l'image
                carte.afficher(plateau.getGraphics(), tabHitbox, false);
            }
        } else {
            // On Met à jour le plateau sur l'image
            carte.afficher(plateau.getGraphics(), tabHitbox, true);
        }

        // On affiche le fond
        g2d = (Graphics2D) g;
        //g.drawImage(fond, 0, 0, null);

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

        // On rend le plateau semi-transparent
        if (afficherMenu) {
            g.setColor(new Color(0, 0, 0, 0.5f));
            g.fillRect(0, 0, tailleFenetre.width, tailleFenetre.height);
        } 
        
        super.paintComponent(g);
    }
}
