package tests;

import terrains.Carte;
import unites.*;

import java.awt.Color;
import wargame.ISoldat.TypesH;
import misc.Position;

public class TestSoldat {
    public static void main(String[] args) {
        Carte c = new Carte();
        Heros humain = new Heros(c, TypesH.HUMAIN, "Bernard", new Position(19, 7), Color.BLACK); 
        Heros nain = new Heros(c, TypesH.NAIN, "Blblbl", new Position(18, 5), Color.BLACK);
        Heros hobbit = new Heros(c, TypesH.HOBBIT, "Frodon", new Position(20, 7), Color.BLACK);  
        
        System.out.println("Soldats sur le terrain :");
        System.out.println(humain.toString());
        System.out.println(nain.toString());
        System.out.println(hobbit.toString());

        humain.combat(hobbit);

        System.out.println("L'humain et le hobbit se battent");
        System.out.println(humain.toString());
        System.out.println(hobbit.toString());
    }
}
