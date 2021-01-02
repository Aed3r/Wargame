package ui;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 * Crée un bouton translucide
 * @author https://ateraimemo.com/Swing/TranslucentButton.html (légèrement modifié)
 */
public class TranslucentButton extends JButton implements wargame.IConfig {
    private static final long serialVersionUID = 1L;
    private int r = 16; // Arrondie 
    private int fontWeight;
    private Boolean italic;
    private int id = -1;

    /**
     * Crée un bouton translucide sans texte
     * @param size la taille du bouton
     * @param fontWeight le poids de la police ( 0 <= fontWeight <= 900)
     * @param italic si le texte est italique ou non
     */
    public TranslucentButton(Dimension size, int fontWeight, Boolean italic) {
        super();
        setPreferredSize(size);
        this.fontWeight = fontWeight;
        this.italic = italic;
        init();
    }

    /**
     * Crée un bouton translucide avec le texte text
     * @param text le texte figurant sur le bouton
     * @param size la taille du bouton
     * @param fontWeight le poids de la police ( 0 <= fontWeight <= 900)
     * @param italic si le texte est italique ou non
     */
    public TranslucentButton(String text, Dimension size, int fontWeight, Boolean italic) {
        super(text);
        setPreferredSize(size);
        this.fontWeight = fontWeight;
        this.italic = italic;
        init();
    }

    /**
     * Crée un bouton translucide avec le texte text et l'icône icon
     * @param text le texte figurant sur le bouton
     * @param icon l'icône figurant sur le bouton, avant le texte
     * @param size la taille du bouton
     * @param fontWeight le poids de la police ( 0 <= fontWeight <= 900)
     * @param italic si le texte est italique ou non
     */
    public TranslucentButton(String text, Icon icon, Dimension size, int fontWeight, Boolean italic) {
        super(text, icon);
        setPreferredSize(size);
        this.fontWeight = fontWeight;
        this.italic = italic;
        init();
    }

    /**
     * @return L'identifiant du bouton donné lors de sa mise en place
     */
    public int getID () {
        return id;
    }

    /**
     * @param id un identifiant arbitraire permettant de retrouver ce bouton
     */
    public void setID (int id) {
        this.id = id;
    }

    /**
     * Initialise l'apparence du bouton et du texte
     */
    private void init () {
        // Apparence du bouton
        setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        setMargin(new Insets(2, 8, 2, 8));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);

        // Préparation de la bonne police
        setFont(GenPolice.genFont(FONTNAME, fontWeight, italic));
        
        // Couleur du texte
        setForeground(TC);
    }

    /**
     * Réinitialise l'affichage du bouton à une valeur de l'apparence actuelle.
     */
    @Override 
    public void updateUI() {
        super.updateUI();
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);
    }

    /**
     * Affiche le bouton
     */
    @Override 
    protected void paintComponent(Graphics g) {
        Color ssc, bgc;
        int x = 0, y = 0, w = getWidth(), h = getHeight();
        Graphics2D g2 = (Graphics2D)g.create();
        String text = getText();

                    /* Dessine le bouton */

        // Active l'antialiasage
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
        // Préparation de la forme du rectangle 
        Shape area = new RoundRectangle2D.Float(x, y, w-1, h-1, r, r);
        // Préparation des couleurs
        ButtonModel m = getModel();
        if (m.isPressed()) { // Clic 
            ssc = SB;
            bgc = ST;
        } else if(m.isRollover()) { // Souris au dessus du bouton
            ssc = ST;
            bgc = SB;
        } else {
            ssc = TL;
            bgc = BR;
        }
        // Affichage
        g2.setPaint(new GradientPaint(x, y, ssc, x, y+h, bgc, true));
        g2.fill(area);
        g2.setPaint(BR);
        g2.draw(area);
        // Éteind l'anti-aliasage
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_OFF);

                    /* Dessine le texte */

        // Active l'anti-aliasage 
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING,
            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // Calcule l'emplacement du texte centré, puis le dessine
        FontMetrics fm = g2.getFontMetrics(g2.getFont());
        Rectangle2D stringBounds = fm.getStringBounds(text, g);
        double textX = (w - stringBounds.getWidth()) / 2d;
        double textY = (h - stringBounds.getHeight()) / 2d;
        // Affichage
        g2.setPaint(g.getColor());
        g2.drawString(getText(), (int) textX, (int) (textY + fm.getAscent()));
        // Éteind l'anti-aliasage
        g2.setRenderingHint(
            RenderingHints.KEY_TEXT_ANTIALIASING, 
            RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        
        g2.dispose();
    }
}