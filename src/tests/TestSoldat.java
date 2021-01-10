package tests;

import terrains.Carte;
import unites.*;

import java.awt.Color;
import wargame.ISoldat.*;
import misc.Position;

public class TestSoldat {
    public static void main(String[] args) {
        Carte c = new Carte();
        Position p1 = new Position(4,3);
        Position p2 = new Position(5,3); 
        Heros chevalier = new Heros(c, TypesH.CHEVALIER, "Bernard", p1, Color.BLACK); 
        Monstre troll = new Monstre(c, TypesM.TROLL, "bkbkbk", p2, Color.BLUE);
        Heros gnome = new Heros(c, TypesH.GNOME, "Frodon", new Position(6, 2), Color.BLACK);  

        c.getElement(p1).setSoldat(chevalier);
        c.getElement(p2).setSoldat(troll);

        System.out.println("Soldats sur le terrain :");
        System.out.println(chevalier.toString());
        System.out.println(troll.toString());
        System.out.println(gnome.toString());

        System.out.println("Le chevalier et le troll se battent" + c.actionHeros(chevalier.getPos(), troll.getPos()));
        System.out.println(chevalier.toString());
        System.out.println(troll.toString());

        chevalier.calculerVision(false);
    }
}
