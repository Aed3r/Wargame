package unites;

import terrains.Carte;

import java.awt.Color;

import misc.Position;

public class Monstre extends Soldat {
    private final TypesH TYPE;
    public Monstre(Carte carte, TypesH type, String nom, Position pos, Color couleur) {
        super(carte, type.getPoints(), type.getPortee(),
        type.getPuissance(), type.getTir(), pos, couleur, nom);
        TYPE = type;
    }

    public String toString(){
        return  TYPE + super.toString();
    }
}