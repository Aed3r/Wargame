package ui;

import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Permet de créer un menu
 */
public class MenuSimple extends JPanel {
    private static final long serialVersionUID = 1L;
    // La couleur potentiellement utilisé en fond
    private Color bgColor = Color.black;
    // Inidque s'il faut afficher une image ou une couleur en fond
    private boolean usesImage = false;
    // Le tableau de bouton à afficher
    private JButton[] buttons;
    // L'image potentiellement utilisé en fond
    private transient Image bgImage;
    // Le panneau sur lequel est créé le menu en question
    private MainPanel mainP = new MainPanel();
    // Un sous-menu/panneau pouvant être chargé par dessus le premier
    private JPanel secondaryP = null;
    // Indique s'il faut réafficher le menu lors de changements
    private boolean loadedOnce = false;
    // Logo potentiellement utilisé
    private transient Image logo = null;
    // Contient potentiellement un logo
    private JLabel picLabel = null;

    /**
     * Le panneau sur lequelle est construit le menu actuel
     */
    private class MainPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Dimension lastDim = null;
        // L'image de fond redimensionné à la taille de la fenêtre
        private transient Image scaledBgImg;

        public MainPanel() {
            super();
        }

        /**
         * Affiche le menu
         */
        @Override
        public void paintComponent (Graphics g) {
            super.paintComponent(g);

            if (usesImage) {
                /* On redimensionne l'image en fond si la taille de la fenêtre a été modifié */
                if (scaledBgImg == null || lastDim == null ||
                    lastDim.getWidth() != getWidth() || 
                    lastDim.getHeight() != getHeight()) scaleBgImg();
                g.drawImage(scaledBgImg, 
                            0, 0, 
                            getWidth(), getHeight(), 
                            0, 0, 
                            getWidth(), getHeight(), 
                            null);
                lastDim = getSize();
            } else setBackground(bgColor);
            MenuSimple.this.loadedOnce = true;
        }

        /**
         * Redimensionne l'image en fond pour remplir la fenêtre
         */
        private void scaleBgImg () {
            if (usesImage && bgImage != null && getWidth() != 0) {
                Dimension s = getSize();
                double ratioX = s.getWidth() / bgImage.getWidth(null),
                    ratioY = s.getHeight() / bgImage.getHeight(null);
                int newHeight, newWidth;
                
                if (ratioX < ratioY) {
                    newHeight = (int) s.getHeight();
                    newWidth = (int) (bgImage.getWidth(null) * ratioY);
                } else {
                    newHeight = (int) (bgImage.getHeight(null) * ratioX);
                    newWidth = (int) s.getWidth();
                }

                scaledBgImg = bgImage.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
            }
        }
    }


    /**
     * Constructeur créant un menu vide sur fond noir
     */
    public MenuSimple () {
        super(new BorderLayout());
        add(mainP, BorderLayout.CENTER);
    }

    /**
     * Constructeur créant un menu sur fond noir à l'aide des boutons passés en paramètre.
     * @param buttons un tableau de boutons quelconques  
     */
    public MenuSimple (JButton[] buttons) {
        this();
        setButtons(buttons);
    }

    /**
     * Constructeur créant un menu simple à l'aide des boutons passés en paramètre. Le fond est rempli avec la couleur souhaité.
     * @param bgColor une couleur quelconque
     * @param buttons un tableau de boutons quelconques  
     */
    public MenuSimple (Color bgColor, JButton[] buttons) {
        this(buttons);
        setBgColor(bgColor);
    }

    /**
     * Constructeur créant un menu simple à l'aide des boutons passés en paramètre. Le fond est rempli par l'image souhaité.
     * @param src le chemin vers l'image a utiliser en fond
     * @param buttons un tableau de boutons quelconques  
     */
    public MenuSimple (String src, JButton[] buttons) {
        this(buttons);
        setBgImage(src);
    }


    /**
     * @return le panneau mainP, sur lequel le menu est créé
     */
    protected JPanel getMainP () {
        return mainP;
    }

    /**
	 * @return la couleur de fond utilisé
	 */
	public Color getBgColor() {
		return bgColor;
	}

	/**
	 * @param bgColor la nouvelle couleur de fond à utiliser
	 */
	public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        usesImage = false;
        rechargerMenu();
	}

	/**
	 * @return l'image de fond utilisé
	 */
	public Image getBgImage() {
		return bgImage;
    }

	/**
	 * @param src le chemin vers l'image a utiliser en fond
	 */
	public void setBgImage(String src) {
        // On récupère l'image 
        try {
            Image img = loadImage(src);
            setBgImage(img);
        } catch (IOException e) {
            // L'image n'a pas pu être chargé, on utilise une couleur
            System.err.println(e.getLocalizedMessage());
            usesImage = false;
        }
    }
    
    /**
	 * @param img l'image à utiliser en fond
	 */
	public void setBgImage(Image img) {
        if (img == null) {
            // L'image est nulle, on utilise une couleur
            usesImage = false;
        } else {
            this.bgImage = img;
            usesImage = true;
            rechargerMenu();
        }
	}

	/**
	 * @return le tableau de boutons utilisés
	 */
	public JButton[] getButtons() {
		return buttons;
	}

	/**
	 * @param buttons les nouveaux boutons à utiliser
	 */
	public void setButtons(JButton[] buttons) {
        int i, l;

        if (buttons == null) return;
        l = buttons.length;

        // On enlève les anciens boutons avant d'ajouter les nouveaux
        for (i = 0; i < l; i++) if (buttons[i] != null) remove(buttons[i]);
        this.buttons = buttons;

        // On place les boutons sur le panneau principal
        mainP.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        for (i = 0; i < l; i++) {
            if (buttons[i] == null) continue;
            gbc.gridy = i+1;
            if (buttons[i] instanceof TranslucentButton) 
                ((TranslucentButton) buttons[i]).setID(i);
            mainP.add(buttons[i], gbc);
        }

        rechargerMenu();
	}

    /**
     * Permet d'ajouter un logo au dessus des boutons
     * @param logo le chemin vers une image quelconque
     * @param scale l'échelle à laquelle redimensionner l'image (en fonction de la taille de la fenêtre)
     */
    public void setLogo (String src, double scale) {
        // On récupère l'image 
        try {
            logo = loadImage(src);
        } catch (IOException e) {
            // L'image n'a pas pu être chargé, on ne fait rien
            System.err.println(e.getLocalizedMessage());
            return;
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // On calcule la nouvelle hauteur à l'aide de scale et de la taille de la fenêtre
                double newHeight = e.getComponent().getHeight() * scale;
                // Pour la largeur on utilise le même ratio utilisé pour la hauteur 
                double newWidth = logo.getWidth(null) * (newHeight / logo.getHeight(null));
                Image scaledLogo = logo.getScaledInstance((int) newWidth, (int) newHeight, Image.SCALE_DEFAULT);
                
                if (picLabel == null) {
                    picLabel = new JLabel(new ImageIcon(scaledLogo));
                    // On place l'image dans le layout, en première position
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(10, 10, 10, 10);
                    gbc.gridy = 0;
                    mainP.add(picLabel, gbc);
                } else picLabel.setIcon(new ImageIcon(scaledLogo));
                
                MenuSimple.this.rechargerMenu();
            }
        });
    }

    /**
     * @return Le logo préalablement choisit avec setLogo
     */
    public Image getLogo () {
        return logo;
    }

    /**
     * Charge et renvoie une image
     * @param src chemin vers une image quelconque
     * @return l'image chargée
     * @throws IOException
     */
    private BufferedImage loadImage (String src) throws IOException {
        BufferedImage img = null;
        InputStream in = MenuSimple.class.getResourceAsStream(src);
        if (in == null) throw new IOException();
        img = ImageIO.read(in);
        return img;
    }

    /**
     * Remplace le menu actuel par p
     * @param p un panneau quelconque
     */
    protected void setMenu (JPanel p) {
        if (p == this) {
            // On revient au menu courant
            mainP.setVisible(true);
            remove(secondaryP);
            secondaryP = null;
            rechargerMenu();
        } else {
            // On ajoute p par dessus le menu courant
            mainP.setVisible(false);
            add(p);
            if (secondaryP != null) remove(secondaryP);
            secondaryP = p;
        }
    }

    /**
     * Recalcule et réaffiche le menu s'il a déjà été affiché
     */
    private void rechargerMenu () {
        if (loadedOnce) {
            mainP.setBounds(0, 0, getWidth(), getHeight());
            mainP.revalidate();
            mainP.repaint();
        }
    }
}
