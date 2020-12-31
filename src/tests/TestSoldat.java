package tests;

import terrains.Carte;
import unites.*;
import wargame.ISoldat.TypesH;

public class TestSoldat {
    public static void main(String[] args) {
        Carte c = new Carte();
        Heros humain = new Heros(c, TypesH.HUMAIN, "Bernard", new Position(19, 7)); 
        Heros nain = new Heros(c, TypesH.NAIN, "Blblbl", new Position(18, 5));
        Heros hobbit = new Heros(c, TypesH.HOBBIT, "Frodon", new Position(20, 7));  
        
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
