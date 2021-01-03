package tests;

import misc.*;
import java.awt.*;
import terrains.Carte;

public class TestCarte {
    public static void main(String[] args) {
        int test = (int)Math.random();
        System.out.printf("%d \n", test);

        Carte C = new Carte ();
        C.affiche_nul();
    }
}
