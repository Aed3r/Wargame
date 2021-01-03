package unites;

import terrains.Carte;

import java.awt.Color;

import misc.Position;

public class Heros extends Soldat {
    private final TypesH TYPE;
    public Heros(Carte carte, TypesH type, String nom, Position pos, Color couleur) {
        super(carte, type.getPoints(), type.getPortee(),
        type.getPuissance(), type.getTir(), pos, couleur, nom);
        TYPE = type;
    }

    public void joueTour(int tour){

    }
    public int getTour(){
        return 0;
    }
    public String toString(){
        return  TYPE + super.toString();
    }
}
