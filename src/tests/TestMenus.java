package tests;

import javax.swing.*;

import java.awt.*;
import menus.Menus;

public class TestMenus {
    public static void main(String[] args) {
        JButton[] buttons = {
            new JButton("Test1"),
            new JButton("Test2"),
            new JButton("Test3"),
            new JButton("Test4")};

        JFrame frame = new JFrame("Menu Test");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); 
        frame.setSize(1000, 1000);

        Menus m = new Menus(Color.CYAN, buttons); 
        frame.add(m, BorderLayout.CENTER);
        frame.setVisible(true);
   }
}
