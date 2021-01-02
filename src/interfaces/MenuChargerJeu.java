package interfaces;

import java.awt.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Date;
import misc.GameSave;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Menu permettant d'afficher et de sélectionner les jeux enregistré
 */
public class MenuChargerJeu extends MenuSimple implements wargame.IConfig {
    private static final long serialVersionUID = 1L;
    private JPanel itemsPanel;
    private ModernScrollPane scrollPane;
    /* Derniere sauvegarde sélectionné */
    private SaveCard lastSelected = null;

    /**
     * Construit un menu affichant les jeux enregistré
     * @param parent le menu créant celui-ci, auquel revenir en appuyant sur le bouton "Retour"
     */
    public MenuChargerJeu(MenuSimple parent) {
        super();
        Dimension s = new Dimension(300, 75); // Taille des boutons
        ArrayList<GameSave> saveList = getSavedGames();
        int i, l = saveList.size();
        JButton btn;

        // On réutilise l'image chargé par le menu parent
        setBgImage(parent.getBgImage());

        // On récupère le panneau sur lequel on crée le menu
        JPanel mainP = getMainP();

        // On modifie le layout du panneau principal
        mainP.setLayout(new BorderLayout());

        // Panneau contenant la liste des sauvegardes
        itemsPanel = new JPanel(new GridBagLayout());
        itemsPanel.setOpaque(false);

        // On ajoute les SaveCard des sauvegardes
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        for (i = 0; i < l; i++) {
            if (saveList.get(i) == null) continue;
            gbc.gridy = i+1;
            itemsPanel.add(new SaveCard(saveList.get(i)), gbc);
        }

        // On ajoute une barre de défilement à la liste des sauvegardes
        scrollPane = new ModernScrollPane(itemsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        mainP.add(scrollPane);

        // Panneau contenant les boutons "Retour", "Charger" et "Supprimer"
        JPanel controls = new JPanel();
        controls.setOpaque(false);
        // Bouton de retour
        btn = new TranslucentButton("Retour", s, 500, false);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // On repasse au menu précédent
                parent.setMenu(parent);
            }
        });
        controls.add(btn);

        // Bouton de chargement
        btn = new TranslucentButton("Charger", s, 500, false);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (lastSelected != null) loadGame(lastSelected.getSave());
            }
        });
        controls.add(btn);

        // Bouton de suppression
        btn = new TranslucentButton("Supprimer", s, 500, false);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (lastSelected != null) {
                    removeGame(lastSelected);
                    lastSelected = null;
                }
            }
        });
        controls.add(btn);

        mainP.add(controls, BorderLayout.PAGE_END);
    }

    /**
     * Panneau permttant d'afficher les informations d'une GameSave et pouvant être utilisé dans une liste
     */
    public class SaveCard extends JPanel {
        private static final long serialVersionUID = 1L;
        private GameSave save;
        /* SCALE et RATIO permettent de déterminer la taille du panneau */
        private static final float SCALE = 0.4f;
        private static final float RATIO = 0.15f;
        /* Permet de déterminer s'il faut recalculer les dimensions du panneau et de ses composants */
        private Dimension lastDim = null;
        /* w/h: largeur/hauteur du panneau, r: arrondie aux coins */
        private int w, h, r = 10;
        /* Indiquent si le panneau est pressé et/ou la souris se se trouve au dessus, respectivement */
        private boolean isPressed = false, isRollover = false;
        private transient Image scaledImg;

        /**
         * Construit le panneau
         * @param save une GameSave quelconque
         */
        public SaveCard (GameSave save) {
            this.save = save;

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (lastSelected != null) lastSelected.deselect();
                    isPressed = !isPressed;
                    lastSelected = SaveCard.this;
                    repaint();
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    isRollover = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isRollover = false;
                    repaint();
                }
            });

            setOpaque(false);
        } 
        
        /**
         * Déselectionne cette SaveCard
         */
        protected void deselect() {
            isPressed = false;
            repaint();
		}

		/**
         * Affiche le panneau<p>
         *  - Largeur du panneau : {@code SCALE * <largeur de la fenêtre>}</p><p>
         *  - Hauteur du panneau : {@code <largeur du panneau> * RATIO}</p>
         */
        @Override
        public void paintComponent (Graphics g) {
            Color ssc, bgc;
            Graphics2D g2 = (Graphics2D) g.create();
            int x = 0, y = 0, fontSize = h/5;
            Font f;
            String text;
            FontMetrics fm;

            if (lastDim == null || !MenuChargerJeu.this.getSize().equals(lastDim)) 
                updateSizes();
    
    
            // Active l'antialiasage
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

                /* Dessine l'image (si elle existe) */
            if (scaledImg != null) {
                g2.drawImage(scaledImg, x, y, null);
                x += scaledImg.getWidth(null);
            } else x += r;

                /* Dessine le bouton */
            // Préparation de la forme du rectangle 
            Shape area = new RoundRectangle2D.Float(0, 0, w-1f, h-1f, r, r);
            // Préparation des couleurs
            if (isPressed) { // Clic 
                ssc = SB;
                bgc = ST;
            } else if(isRollover) { // Souris au dessus du bouton
                ssc = ST;
                bgc = SB;
            } else {
                ssc = TL;
                bgc = BR;
            }
            // Affichage
            g2.setPaint(new GradientPaint(x, y, ssc, x, (float) y+h, bgc, true));
            g2.fill(area);
            g2.setPaint(BR);
            g2.draw(area);
            
            // Éteind l'anti-aliasage
            g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_OFF);
    
                /* Dessine le texte */

                    /* Date de sauvegarde */
            // Active l'anti-aliasage 
            g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            // Police et couleur
            f = GenPolice.genFont(FONTNAME, 700, false, fontSize);
            g2.setFont(f);
            g2.setColor(Color.white);
            // Texte à afficher
            LocalDateTime localDateTime = save.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            DateTimeFormatter defaultTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'à' HH:mm:ss.SSS");
            text = defaultTimeFormatter.format(localDateTime);
            // Affichage à la bonne position
            fm = g2.getFontMetrics(f);
            g2.drawString(text, x + 5, 5 + fm.getAscent());

                    /* Temps joué */
            // Police et couleur
            f = GenPolice.genFont(FONTNAME, 400, false, fontSize);
            g2.setFont(f);
            // Texte à afficher
            if (save.getMinutesPlayed() == 1) text = "1 minute jouée";
            else text = save.getMinutesPlayed() + " minutes jouées";
            // Affichage à la bonne position
            fm = g2.getFontMetrics(f);
            g2.drawString(text, x + 5, 8 + fm.getAscent() + fm.getHeight());
            
                    /* Troupes restantes */
            if (save.getTroopCount() == 1) text = "1 troupe restante";
            else text = save.getTroopCount() + " troupes restantes";
            g2.drawString(text, x + 5, 8 + fm.getAscent() + fm.getHeight() * 2);

            // Éteind l'anti-aliasage
            g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, 
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            g2.dispose();
        }

        /**
         * Recalcule la taille du panneau et redimensionne l'image
         */
        private void updateSizes () {
            double width, height;
            BufferedImage img = save.getGameImg();

            /* Taille du panneau */
            width = MenuChargerJeu.this.getWidth() * SCALE;
            height = width * RATIO;
            w = (int) width;
            h = (int) height;

            /* Image (si elle existe) */
            if (img != null) {
                scaledImg = img.getScaledInstance(-1, h, Image.SCALE_FAST);
                scaledImg = makeRoundedCorner(scaledImg, r);
            }

            lastDim = MenuChargerJeu.this.getSize();
            setPreferredSize(new Dimension(w, h));
            scrollPane.setPreferredSize(new Dimension(w, MenuChargerJeu.this.getHeight()));
            // Indique à itemsPanel de redessiner ses composants
            itemsPanel.revalidate();
            itemsPanel.repaint();
        }

        /**
         * Renvoie image avec des coins arrondies et sans aliasage
         * @param image une image quelconque
         * @param cornerRadius le nombre de pixel à enlever aux coins
         * @return <b>image</b> avec des coins arrondies et sans aliasage
         * @see https://stackoverflow.com/a/7603815/5591299
         */
        public BufferedImage makeRoundedCorner(Image image, int cornerRadius) {
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
            Graphics2D g2 = output.createGraphics();
            
            // This is what we want, but it only does hard-clipping, i.e. aliasing
            // g2.setClip(new RoundRectangle2D ...)
        
            // so instead fake soft-clipping by first drawing the desired clip shape
            // in fully opaque white with antialiasing enabled...
            g2.setComposite(AlphaComposite.Src);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fill(new RoundRectangle2D.Float(0, 0, width, height, cornerRadius, cornerRadius));
            
            // ... then compositing the image on top,
            // using the white shape from above as alpha source
            g2.setComposite(AlphaComposite.SrcAtop);
            g2.drawImage(image, 0, 0, null);
            
            g2.dispose();
            
            return output;
        }

        /**
         * @return la sauvegarde associé au panneau
         */
        public GameSave getSave () {
            return save;
        }
    }

    /**
     * TODO
     * @return Une liste de GameSave
     */
    private ArrayList<GameSave> getSavedGames() {
        ArrayList<GameSave> l = new ArrayList<>();
        l.add(new GameSave(new Date(), 22, 6, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 234, 8, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 345, 2, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 345, 456, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 123, 4, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 123, 4, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 123, 4, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 123, 4, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 123, 4, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 123, 4, "data/img/gameSaveImgTMP.jpg"));
        l.add(new GameSave(new Date(), 123, 4, "data/img/gameSaveImgTMP.jpg"));

        return l;
    }

    /**
     * TODO
     * Charge le jeu passé en paramètre
     * @param save une sauvegarde de jeu
     */
    private void loadGame (GameSave save) {
        System.out.println("Chargement d'une save (ou pas)...");
    }

    /**
     * TODO
     * Supprime le jeu de la liste et du stockage
     * @param save une sauvegarde de jeu
     */
    private void removeGame (SaveCard save) {
        itemsPanel.remove(save);
        itemsPanel.revalidate();
        itemsPanel.repaint();
        // Supprimer du stockage...
    }
}
