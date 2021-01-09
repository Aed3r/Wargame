package tests;

import java.util.ArrayList;
import terrains.Noeud;
import terrains.Carte;
import misc.Position;

public class TestChemin {
    public static void main(String[] args) {
        ArrayList<Noeud> liste = new ArrayList<Noeud>();

        Noeud n = new Noeud();
        Noeud na = new Noeud();
        Noeud nb = new Noeud();
        Noeud test = new Noeud();

        test.setCoutF(1);
        test.setCoutG(1);
        test.setCoutH(1);
        test.setPere(1);
        test.setPos(1, 1);
        test.setPosPere(1, 1);

        n.setCoutF(1);
        n.setCoutG(2);
        n.setCoutH(3);
        n.setPere(0);
        n.setPos(0, 0);
        n.setPosPere(1, 1);

        na.setCoutF(1);
        na.setCoutG(2);
        na.setCoutH(3);
        na.setPere(0);
        na.setPos(0, 0);
        na.setPosPere(1, 1);

        nb.setCoutF(1);
        nb.setCoutG(1);
        nb.setCoutH(1);
        nb.setPere(1);
        nb.setPos(1, 1);
        nb.setPosPere(1, 1);

        liste.add(n);
        liste.add(na);
        liste.add(test);

        System.out.printf("nb à l'indice: %d \n", liste.indexOf(nb));
        
        Carte c = new Carte ();
        c.affiche_nul();

        //Chemin chem = new Chemin(5, 2, 5, 18, c);
        Position p = new Position(5, 2);
        Position p2 = new Position(5, 18);
        int longu = p.distance(p2, c);
        System.out.printf("La longueur du chemin est de %d \n", longu);
    }
}
