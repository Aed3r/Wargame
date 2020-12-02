package menus;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Menus extends JPanel {
    private static final long serialVersionUID = 1345696208043790031L;
    private static final int HORIZONTALPAD = 40; // %
    private static final int VERTICALPAD = 30; // %

    private Color bgColor;
    private transient Image bgImage;
    private boolean usesImage;
    private JButton[] buttons;
    private GridLayout layout = new GridLayout();

    public Menus (JButton[] buttons) {
        bgColor = Color.BLACK;
        usesImage = false;
        this.buttons = buttons;
        setButtons();
    }

    public Menus (Color bgColor, JButton[] buttons) {
        this(buttons);
        this.bgColor = bgColor;
    }

    public Menus (Image img, JButton[] buttons) {
        this(buttons);
        this.bgImage = img;
        usesImage = true;
    }

    private void setButtons () {
        int i, l = buttons.length;

        layout.setRows(buttons.length);
        setLayout(layout);

        for (i = 0; i < l; i++) {
            add(buttons[i]);
        }
    }

    /**
     * Calcule la taille de la bordure en fonction des pourcentages HORIZONTALPAD
     * et VERTICALPAD puis la définie.
     */
    private void setBorders () {
        int w = getWidth(), h = getHeight(),
            hPad = w * HORIZONTALPAD / 100, 
            vPad = h * VERTICALPAD / 100;

        setBorder(new EmptyBorder(vPad, hPad, hPad, vPad));
        setMinimumSize(new Dimension(w - hPad*2, h - vPad*2));
    }

    @Override
    public void paintComponent (Graphics g) {
        super.paintComponent(g);
        setBorders();
        if (usesImage) g.drawImage(bgImage, 
                                   0, 0, 
                                   this.getWidth(), this.getHeight(), 
                                   0, 0, 
                                   bgImage.getWidth(null), bgImage.getHeight(null),
                                   null);
        else setBackground(bgColor);
    }
}
