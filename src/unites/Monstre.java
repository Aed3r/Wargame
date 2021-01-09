package unites;

import terrains.Carte;

import java.awt.Color;

import misc.Position;

public class Monstre extends Soldat {
    private final TypesM TYPE;
    /**
     * Cree un Monstre d'un des types énuméré dans ISoldat selon son type 
     * @param carte La carte dont dépend le Monstre
     * @param type Le type du Monstre
     * @param nom Un nom pour le Monstre
     * @param pos La position initiale du Monstre
     * @param couleur Une couleur associée au Monstre
     */
    public Monstre(Carte carte, TypesM type, String nom, Position pos, Color couleur) {
        super(carte, type.getPoints(), type.getPortee(),
        type.getPuissance(), type.getTir(), pos, couleur, nom);
        TYPE = type;
    }

    public String toString(){
        return  TYPE + super.toString();
    }

    /**
     * @return Le nom associé au type de ce Monstre
     */
    public String getNom(){
        if(this.getPointsMax() == 100){
            return "TROLL";
        }else if(this.getPointsMax() == 30){
            return "ZOMBIE";
        }else if(this.getPointsMax() == 60){
           return "MOMIE";
        }else return "SORCIER";
    };

    /**
     * Joue le monstre
     * @return true si le monstre a fait une action, false sinon
     */
    public boolean jouer(){
        Heros h = getCarte().trouveHeros(getPos());
        if(h == null){/*Il n'y a aucun Heros adjacents*/
            Position pos = getCarte().trouvePositionVide(getPos());
            if(pos == null) return false;
            getCarte().deplaceSoldat(pos, this);
        }else{
            this.combat(h);
        }
        return true;
    }
}