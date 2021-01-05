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

    public String toString(){
        return  TYPE + super.toString();
    }

    public void calculerVision(){} /*TODO Trouve les cases que le héros vois et les positionne sur visible*/
}
