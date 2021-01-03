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

        int x = 13; int y = 23;
        Position P = new Position(x, y);
        Element E = C.getElement(P);
        System.out.printf("L'élement à la position %d:%d est de type : %s \n", x, y, E.getNom());
    
        Position A = new Position(3, 3);
        C.trouvePositionVide(A);
    }
}
