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

        int x = 0; int y = 0;
        Position P = new Position(x, y);
        Element E = C.getElement(P);
        System.out.printf("L'élement à la position %d:%d est de type : %s \n", x, y, E.getNom());
    
        C.trouvePositionVide(P);
    }
}
