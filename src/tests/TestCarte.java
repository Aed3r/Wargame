package tests;

import misc.*;
import terrains.Carte;

public class TestCarte {
    public static void main(String[] args) {
        int test = (int)Math.random();
        System.out.printf("%d %n", test);

        Carte c = new Carte ();
        c.affiche_nul();

        int x = 13; int y = 23;
        Position p = new Position(x, y);
        Element e = c.getElement(p);
        System.out.printf("L'élement à la position %d:%d est de type : %s \n", x, y, e.getNom());
    
        Position a = new Position(5, 3);
        c.trouvePositionVide(a);
        c.trouveHeros(a);
        c.placementSoldatAlea();
    }
}
