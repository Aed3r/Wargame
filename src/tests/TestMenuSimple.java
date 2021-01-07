package tests;

import javax.swing.*;

import ui.MenuSimple;

import java.awt.*;

/**
 * Test rapide pour la classe MenuSimple
 */
public class TestMenuSimple {
    public static void main(String[] args) {
        JButton[] buttons = {
            new JButton("Test1"),
            new JButton("Test2"),
            new JButton("Test3"),
            new JButton("Test4")};

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setPreferredSize(new Dimension(100, 50));
        }

        JFrame frame = new JFrame("Menu Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 

        // Taille de la fenêtre = taille de l'écran / 1.33333
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize((int) (screenSize.getWidth()/(1+1/3.)), 
                      (int) (screenSize.getHeight()/(1+1/3.)));
        // Evite le redimensionnement 
        frame.setResizable(false);
        // Centre la fenêtre
        frame.setLocationRelativeTo(null);

        MenuSimple m = new MenuSimple(Color.CYAN, buttons); 
        frame.add(m, BorderLayout.CENTER);
        frame.setVisible(true);
   }
}