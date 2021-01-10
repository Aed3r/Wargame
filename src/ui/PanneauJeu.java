package ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import misc.Parametres;
import javax.imageio.ImageIO;
import misc.Element;
import misc.GameSave;

import javax.swing.*;
import terrains.Carte;
import unites.Soldat;
import misc.Position;

/**
 * Panneau permettant d'afficher un plateau de jeu et l'interface associé
 */
public class PanneauJeu extends JPanel implements wargame.IConfig, MouseWheelListener {
    private static final long serialVersionUID = -4874781269011185234L;
    private static final float MAXZOOM = 2f, VITESSEZOOM = 0.1f;
    private final Carte carte; // La cart à afficher
    private final ArrayList<TranslucentButton> boutonsJeu = new ArrayList<>(); // Boutons apparaissant au dessus du plateau de jeu
    private final ArrayList<TranslucentButton> boutonsMenu = new ArrayList<>(); // Boutons apparaissant dans le menu
    private final ArrayList<LabelAA> labelsInfo = new ArrayList<>(); // Labels d'informations sur un soldat
    private final MenuSimple menuParent;
    private final boolean perf; // Si le mode performance est activé ou non
    private final float multTaille; // Le multiplicateur des tailles d'images
    private final long debutJeu = System.currentTimeMillis();

    private int xPlateau = -MARGX, yPlateau = -MARGY, wPlateau, hPlateau; // Position et taille du plateau
    private double zoomPlateau = 1, zoomMin;
    private transient BufferedImage plateau = null, fond;
    private byte[][][] tabHitbox; // Le tableau indiquant la position des éléments sur le plateau
    private Point posSouris;
    private Dimension tailleFenetre = null, tailleVirtuelle = null;
    private boolean afficherMenu = false, barreInfosCache = true;
    private transient Position pos1;
    
    /**
     * Construit un nouveau panneau affichant la carte c
     * @param carte la carte à afficher
     * @param parent le menu auquel revenir en quittant
     */
    public PanneauJeu(Carte carte, MenuSimple parent) {
        super();
        this.carte = carte;
        this.menuParent = parent;

        // Initialisations
        setOpaque(false);
        setLayout(new GridBagLayout());
        addMouseWheelListener(this);

        // Mode Performance
        String val = Parametres.getParametre("modePerf");
        if (val != null) perf = val.equals(PARAMETRES[3][2]);
        else perf = true;
        if (perf) multTaille = MULTTAILLEPERF;
        else multTaille = 1;

        Element.TypeElement.setDeplacement(multTaille);

        wPlateau = (int) ((MARGX * 2 + TAILLEX * LARGEUR_CARTE + TAILLEX / 2) * multTaille); // Largeur du plateau
        hPlateau = (int) ((MARGY * 2 + TAILLEY * (HAUTEUR_CARTE + 1)) * multTaille); // Hauteur du plateau
        tabHitbox = new byte[hPlateau][wPlateau][2];

        /* Création des boutons */
        creerBoutonJeu();
        creerBoutonMenu();

        /* Création de la barre d'infos */
        creerInfoBar();
        
        /* Création des listener */
        creerMouseListener();
        creerMouseMotionListener();
        creerKeyListener();
    }
    

             /* Création de l'interface */


    /**
     * Crée les boutons s'affichant au dessus du plateau
     */
    private void creerBoutonJeu () {
        TranslucentButton tmp = null;
        GridBagConstraints gc;
        ImageIcon icon;

        // Menu
        gc = new GridBagConstraints();

        icon = chargerIcon("settings");
        if (icon == null) tmp = new TranslucentButton("Menus", new Dimension(100, 100), 400, false);
        else tmp = new TranslucentButton(icon, new Dimension(45, 45));

        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                swapMenu();
                requestFocusInWindow();
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

        // Passer le tour, puis jouer celui des monstres
        gc = new GridBagConstraints();
        tmp = new TranslucentButton("Finir le tour >", new Dimension(200, 70), 400, false);

        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("finir tour");
                for (Soldat s : carte.terminerTour(true)) {
                    RunnableAfficherDegat r = new RunnableAfficherDegat(carte.getElement(s.getPos()));
                    Thread t = new Thread(r);
                    t.start();
                }
                PanneauJeu.this.repaint();
                for (Soldat s : carte.jouerEnnemis()) {
                    RunnableAfficherDegat r = new RunnableAfficherDegat(carte.getElement(s.getPos()));
                    Thread t = new Thread(r);
                    t.start();
                }
                for (Soldat s : carte.terminerTour(false)) {
                    RunnableAfficherDegat r = new RunnableAfficherDegat(carte.getElement(s.getPos()));
                    Thread t = new Thread(r);
                    t.start();
                }
            }
        });
        gc.gridx = 1;
        gc.gridy = 6;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.SOUTHEAST;
        gc.insets = new Insets(10,10,10,10);
        add(tmp, gc);
        boutonsJeu.add(tmp);
    }

    /**
     * Crée les boutons du menu
     */
    private void creerBoutonMenu () {
        Dimension s = new Dimension(300, 75); // Taille des boutons
        TranslucentButton tmp = null;
        GridBagConstraints gc = new GridBagConstraints();

        // Retour au jeu
        tmp = new TranslucentButton("Retour au jeu", s, 400, false);
        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                swapMenu();
                requestFocusInWindow();
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
                plateau.flush();
                tabHitbox = null;
                menuParent.setMenu(menuParent);
            }
        });

        gc.gridy = 2;
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

        gc.gridy = 3;
        add(tmp, gc);
        tmp.setVisible(false);
        boutonsMenu.add(tmp);

        // Sauvegarder
        tmp = new TranslucentButton("Sauvegarder", s, 400, false);
        tmp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                carte.addMinutesJouees((int) ((System.currentTimeMillis()-debutJeu) / 6000));
                new GameSave(new Date(), Element.getCompteurSoldat(), 
                            carte.getMinutesJouees(), creerThumbnail(), carte).enregistrement();
            }   
        });

        gc.gridy = 4;
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

        gc.gridy = 5;
        add(tmp, gc);
        tmp.setVisible(false);
        boutonsMenu.add(tmp);
    }

    /**
     * @return une capture d'écran du plateau actuel pouvant être utilisé dans une save
     */
    private ImageIcon creerThumbnail () {
        int w = tailleFenetre.width/5,
            h = tailleFenetre.height/5;

        BufferedImage thumb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = thumb.createGraphics();
        g2d.setRenderingHint (RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage (plateau, 0, 0, w, h, null);
        g2d.dispose();

        return new ImageIcon(thumb);
    }

    /**
     * Ouvre/Ferme le meu
     */
    private void swapMenu() {
        if (afficherMenu) { // On ferme le menu
            // On affiche tous les boutons du jeu
            for (int i = 0; i < boutonsJeu.size(); i++) boutonsJeu.get(i).setVisible(true);
            // On cache tous les boutons du menu
            for (int i = 0; i < boutonsMenu.size(); i++) boutonsMenu.get(i).setVisible(false);
        } else { // On affiche le menu
            // On cache tous les boutons du jeu
            for (int i = 0; i < boutonsJeu.size(); i++) boutonsJeu.get(i).setVisible(false);
            // On affiche tous les boutons du menu
            for (int i = 0; i < boutonsMenu.size(); i++) boutonsMenu.get(i).setVisible(true);
            // On cache tous les boutons de la barre d'informations
            if (!barreInfosCache) for (int i = 0; i < labelsInfo.size(); i++) labelsInfo.get(i).setVisible(false);
        }
        afficherMenu = !afficherMenu;
        repaint();
    }

    /**
     * Crée les labels affichant des informations sur les soldats
     */
    private void creerInfoBar () {
        GridBagConstraints gc = new GridBagConstraints();
        LabelAA tmp;
        ImageIcon icon;
        Dimension s = new Dimension();
            
        gc.anchor = GridBagConstraints.NORTHEAST;
        gc.insets = new Insets(10,10,10,10);
        gc.gridx = 1;
        gc.ipadx = 50;

        // Nom du soldat
        tmp = new LabelAA("");
        tmp.setForeground(Color.white);
        tmp.setHorizontalAlignment(SwingConstants.RIGHT);
        s.setSize(300, 45);
        tmp.setPreferredSize(s);
        gc.gridy = 0;
        tmp.setVisible(false);
        add(tmp, gc);
        labelsInfo.add(tmp);

        // Points de vies
        icon = chargerIcon("heart");
        tmp = new LabelAA("", icon);
        tmp.setHorizontalAlignment(SwingConstants.RIGHT);
        tmp.setForeground(Color.white);
        s.setSize(200, 45);
        tmp.setPreferredSize(s);
        gc.gridy = 1;
        tmp.setVisible(false);
        add(tmp, gc);
        labelsInfo.add(tmp);

        // Portee
        icon = chargerIcon("eye");
        tmp = new LabelAA("", icon);
        tmp.setHorizontalAlignment(SwingConstants.RIGHT);
        tmp.setForeground(Color.white);
        s.setSize(200, 45);
        tmp.setPreferredSize(s);
        gc.gridy = 2;
        tmp.setVisible(false);
        add(tmp, gc);
        labelsInfo.add(tmp);

        // Puissance au corps à corps
        icon = chargerIcon("swords");
        tmp = new LabelAA("", icon);
        tmp.setHorizontalAlignment(SwingConstants.RIGHT);
        tmp.setForeground(Color.white);
        s.setSize(100, 45);
        tmp.setPreferredSize(s);
        gc.gridy = 3;
        tmp.setVisible(false);
        add(tmp, gc);
        labelsInfo.add(tmp);

        // Puissance au tir
        icon = chargerIcon("bow");
        tmp = new LabelAA("", icon);
        tmp.setHorizontalAlignment(SwingConstants.RIGHT);
        tmp.setForeground(Color.white);
        s.setSize(100, 45);
        tmp.setPreferredSize(s);
        gc.gridy = 4;
        tmp.setVisible(false);
        add(tmp, gc);
        labelsInfo.add(tmp);

        // Tour joué
        tmp = new LabelAA("");
        tmp.setForeground(Color.white);
        tmp.setHorizontalAlignment(SwingConstants.RIGHT);
        s.setSize(300, 45);
        tmp.setPreferredSize(s);
        gc.gridy = 5;
        tmp.setVisible(false);
        add(tmp, gc);
        labelsInfo.add(tmp);
    }

    /**
     * JLabel dont le texte est anti-aliasé et la police pré-définie
     */
    class LabelAA extends JLabel {
        private static final long serialVersionUID = 1L;

        public LabelAA(String text) { super(text); }

        public LabelAA(String text, Icon ico) { super(text, ico, SwingConstants.RIGHT); }
        
        @Override
        public void paintComponent (Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setFont(GenPolice.genFont("Raleway", 500, false));
            super.paintComponent(g2d);
        }
    }

    /**
     * Charge une icône depuis data/img/icon
     * @param nom le nom de l'icône sans extension
     * @return l'icône chargée
     */
    private ImageIcon chargerIcon (String nom) {
        InputStream stream = getClass().getResourceAsStream("/img/icon/" + nom + ".png");
        ImageIcon icon = null;

        if (stream == null) return null;

        try {
            icon = new ImageIcon(ImageIO.read(stream));
        } catch (IOException|IllegalArgumentException e) {
            System.err.println(e.getLocalizedMessage());
        }

        return icon;
    }

    /**
     * Affiche/cache la barre d'info 
     */
    private void switchBarreInfo (Soldat s) {
        if (s != null) {
            // Nom
            labelsInfo.get(0).setText(s.getNom());
            labelsInfo.get(0).setVisible(true);
            // Points de vies
            labelsInfo.get(1).setText(s.getPoints() + "/" + s.getPointsMax());
            labelsInfo.get(1).setVisible(true);
            // Portée de vue
            labelsInfo.get(2).setText(s.getPortee() + "");
            labelsInfo.get(2).setVisible(true);
            // Puissance au corps à corps
            labelsInfo.get(3).setText(s.getPUISSANCE() + "");
            labelsInfo.get(3).setVisible(true);
            // Puissance au tir
            labelsInfo.get(4).setText(s.getTIR() + "");
            labelsInfo.get(4).setVisible(true);
            // Tour Joué
            if (!s.getTour()) {
                labelsInfo.get(5).setText("Tour joué");
                labelsInfo.get(5).setVisible(true);
            }
            barreInfosCache = false;
        } else if (!barreInfosCache) {
            // On cache tous les boutons de la barre d'informations
            for (int i = 0; i < labelsInfo.size(); i++) labelsInfo.get(i).setVisible(false);
            barreInfosCache = true;
        }
    }



             /* Création des listener */


    /**
     * Crée le listener des évènements souris
     */
    private void creerMouseListener () {
        this.addMouseListener(new MouseAdapter() {
            // Déplacement de la carte
            @Override
            public void mousePressed(MouseEvent e) {
                if(afficherMenu) return;

                posSouris = e.getPoint();
                repaint();
            }

            // Action d'un héros
            @Override
            public void mouseClicked(MouseEvent e) {
                Soldat s;

                if(afficherMenu) return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    // On retrouve l'élémetn cliqué
                    Point curseurMap = getPosCurseurPlateau();
                    if (curseurMap == null) return;

                    Position pos = new Position(tabHitbox[curseurMap.y][curseurMap.x][0] & 0xFF, tabHitbox[curseurMap.y][curseurMap.x][1] & 0xFF);
                    
                    // Action du héros
                    s = carte.getElement(pos).getSoldat();
                    if (pos1 == null && s != null && s.estHeros()) pos1 = pos;
                    else if (pos1 != null) {
                        if (carte.actionHeros(pos1, pos)) {
                            // Affichage des dégats
                            if (s != null) {
                                RunnableAfficherDegat r = new RunnableAfficherDegat(carte.getElement(pos));
                                Thread t = new Thread(r);
                                t.start();
                            } else repaint();
                            pos1 = null;
                        } else if (s != null && s.estHeros()) pos1 = pos; 
                        else pos1 = null;
                    } 

                    // Affichage d'informations en mode performance
                    if (perf) switchBarreInfo(s);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    pos1 = null;
                }
            }
        });
    }

    /**
     * Crée le listener des mouvements de souris
     */
    private void creerMouseMotionListener () {
        if (perf) {
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
        } else {
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
    
                // Affichage d'informations sur le soldat
                @Override
                public void mouseMoved(MouseEvent e) {
                    Soldat s;
    
                    if(afficherMenu) return;

                    Point curseurMap = getPosCurseurPlateau();
                    if (curseurMap == null) return;

                    Position pos = new Position(tabHitbox[curseurMap.y][curseurMap.x][0] & 0xFF, tabHitbox[curseurMap.y][curseurMap.x][1] & 0xFF);
    
                    s = carte.getElement(pos).getSoldat();
                    switchBarreInfo(s);
                }
            });
        }
    }

    /**
     * Crée le listener des évènements clavier
     */
    private void creerKeyListener () {
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed (KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_Q:
                    case KeyEvent.VK_A:
                        // On bouge la carte vers la gauche
                        // Pas de déplacement pendant que le menu est ouvert
                        if (afficherMenu) return;
                        xPlateau += DEPLACEMENTCLAVIER;
                        repaint();
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        // On bouge la carte vers la droite
                        if (afficherMenu) return;
                        xPlateau -= DEPLACEMENTCLAVIER;
                        repaint();
                        break;
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_Z:
                    case KeyEvent.VK_W:
                        // On bouge la carte vers le haut
                        if (afficherMenu) return;
                        yPlateau += DEPLACEMENTCLAVIER;
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        // On bouge la carte vers le bas
                        if (afficherMenu) return;
                        yPlateau -= DEPLACEMENTCLAVIER;
                        repaint();
                        break;
                    case KeyEvent.VK_PAGE_DOWN:
                        // On dézoom
                        if (afficherMenu) return;
                        zoomCentre(-VITESSEZOOM);
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        // On zoom
                        if (afficherMenu) return;
                        zoomCentre(VITESSEZOOM);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        // On ouvre/ferme le menu
                        swapMenu();
                        break;
                    case KeyEvent.VK_F5:
                        // Force le réaffichage du plateau
                        System.out.println("Rechargement du plateau...");
                        tailleFenetre = null;
                        plateau = null;
                        repaint();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * Définie le zoom lorsque la molette est utilisé
     * @param e l'évenement généré
     */
    @Override
	public void mouseWheelMoved(MouseWheelEvent e) {
        if(afficherMenu) return;
        
        // la position du curseur sur le plateau avant le zoom
        Point curseurMap = getPosCurseurPlateau();
        if (curseurMap == null) return;
        
        zoomPlateau += e.getWheelRotation() * -VITESSEZOOM;
        if (zoomPlateau > MAXZOOM) zoomPlateau = MAXZOOM;
        if (zoomPlateau < zoomMin) zoomPlateau = zoomMin;

        // la position du curseur sur le plateau après le zoom
        Point curseurMapZoom = getPosCurseurPlateau();
        if (curseurMapZoom == null) return;

        // On replace le plateau de façon à ce que le curseur reste sur le même point de la carte
        deplacement(curseurMap, curseurMapZoom);
    }

    

            /* Gestion de la navigation du plateau */


    /**
     * Effectue un zoom centré sur le centre de l'écran
     * @param zoom l'indice du zoom
     */
    private void zoomCentre (Float zoom) {
        Point avant, apres;

        avant = new Point((int) ((tailleFenetre.width/2.)/zoomPlateau)-xPlateau,  (int) ((tailleFenetre.height/2.)/zoomPlateau)-yPlateau);
        zoomPlateau += zoom;
        if (zoomPlateau > MAXZOOM) zoomPlateau = MAXZOOM;
        apres = new Point((int) ((tailleFenetre.width/2.)/zoomPlateau)-xPlateau,  (int) ((tailleFenetre.height/2.)/zoomPlateau)-yPlateau);
        deplacement(avant, apres);
    }

    /**
     * @return la position du curseur sur le plateau ou null s'il ne s'y trouve pas
     */
    private Point getPosCurseurPlateau () {
        Point tmp;
        
        tmp = MouseInfo.getPointerInfo().getLocation(); // Position sur l'écran
        Point posFenetre = null;
        try { posFenetre = this.getLocationOnScreen(); } 
        catch (IllegalComponentStateException e) { return null; }
        
        tmp.setLocation(tmp.x - posFenetre.x, tmp.y - posFenetre.y); // Position dans la fenêtre
        tmp.setLocation((int) (tmp.x/zoomPlateau), (int) (tmp.y/zoomPlateau)); // Position dans la fenêtre virtuelle
        tmp.setLocation(tmp.x-xPlateau, tmp.y-yPlateau); // Point sur la carte
        
        if (tmp.x > 0 && tmp.y > 0 && tmp.x < wPlateau && tmp.y < hPlateau) return tmp;
        else return null;
    }

    /**
     * Effectue le déplacement nécessaire après le zoom
     */
    private void deplacement (Point avant, Point apres) {
        xPlateau += apres.getX() - avant.getX();
        yPlateau += apres.getY() - avant.getY();
        repaint();
    }

    /**
     * Lance l'animation lorsqu'un soldat prend des dégats
     */
    public class RunnableAfficherDegat implements Runnable {
        private Element e;

        /**
         * @param e le soldat sur lequel affiché l'animation
         */
		public RunnableAfficherDegat(Element e) {
            this.e = e;
		}
        
		public void run() {
            Soldat s = e.getSoldat();
            if (s == null) return;

            s.setAfficherSpriteDegat(true);
            e.setReafficher();
            PanneauJeu.this.repaint();

            try {
				Thread.sleep(500);
            } catch (InterruptedException e) { }
            
            s.setAfficherSpriteDegat(false);
            if (s.getPos() != null) // Le soldat peux avoir été tué entre-temps
                carte.getElement(s.getPos()).setReafficher();
            PanneauJeu.this.repaint();
		}
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

            if (!perf) {
                fond = new BufferedImage(tailleFenetre.width, tailleFenetre.height, BufferedImage.TYPE_INT_RGB);
                g2d = (Graphics2D) fond.getGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, BGCOLOR.brighter().brighter(), 0, tailleFenetre.height, BGCOLOR.darker().darker());
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, tailleFenetre.width, tailleFenetre.height);
                g2d.dispose();
            }

            // zoomMin
            if (tailleFenetre.width > tailleFenetre.height)
                zoomMin = (double) tailleFenetre.width / wPlateau;
            else
                zoomMin = (double) tailleFenetre.height / hPlateau;

            zoomPlateau = zoomMin;

            if (plateau == null) {
                // On crée l'image sur laquelle le plateau sera dessiné
                if (perf)
                    plateau = new BufferedImage(wPlateau, hPlateau, BufferedImage.TYPE_INT_RGB);
                else
                    plateau = new BufferedImage(wPlateau, hPlateau, BufferedImage.TYPE_INT_ARGB);
                // On dessine le plateau sur l'image
                carte.afficher(plateau.getGraphics(), tabHitbox);
            }
        } else {
            // On Met à jour le plateau sur l'image
            Element.reafficherFile(plateau.getGraphics(), tabHitbox, carte);
        }

        // Préparation du Graphics2D
        g2d = (Graphics2D) g;
        if (perf) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        }

        // On affiche le fond
        if (!perf) g.drawImage(fond, 0, 0, null);

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
