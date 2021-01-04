package unites;

import terrains.Carte;
import java.awt.Color;
import misc.*;
import wargame.ISoldat;

public abstract class Soldat implements ISoldat {
    private final int POINT_DE_VIE_MAX, PORTEE_VISUELLE, PUISSANCE, TIR; 
    private int pointsDeVie;
    private Carte carte;
    private Position pos;
    private boolean tour; /*Permet de savoir si ce soldat a déja joué son tour*/

    Soldat(Carte carte, int pts, int portee, int puiss, int tir, Position pos, Color couleur, String nom){
        this.POINT_DE_VIE_MAX = this.pointsDeVie = pts;
        this.PORTEE_VISUELLE = portee;
        this.PUISSANCE = puiss;
        this.TIR = tir;
        this.carte = carte;
        this.pos = pos;
    }

    /* Méthodes abstraites */
    public boolean getTour(){return tour;}
    public int joueTour(){
        if(tour == true){
            tour = false;
            return 0;
        }else{
            return -1;
        }
    }

    /* Méthodes définies */
    @Override
    public int getPoints(){ return this.pointsDeVie;} 
    
    public int getPortee() {return this.PORTEE_VISUELLE;}

    public void combat(Soldat soldat){ /*On considere que l'instance pour laquelle est appelée cette methode est la première a taper */
        /*Premièrement le cas d'un combat au corps-a-corps (adjacent)*/
        if(getPos().estVoisine(soldat.getPos())){
            /*On fait un tirage entre 0 et la puissance du soldat*/
            soldat.pointsDeVie = soldat.pointsDeVie - (int)(Math.random() * this.PUISSANCE);
            if(soldat.pointsDeVie <= 0){ /*Si le soldat adverse est mort sur le coup*/
                carte.mort(soldat);
            }else{ /* Si il n'est pas mort il peut attaquer a son tour*/
                this.pointsDeVie = this.pointsDeVie - (int)(Math.random() * soldat.PUISSANCE);
                if(this.pointsDeVie <= 0) carte.mort(this);
            }
        /*Ensuite le cas d'un combat a distance (on utilise le tir et non la puissance)*/ 
        }else if(getPos().distance(soldat.getPos()) <= this.getPortee()){ //Si le soldat adverse eset a portée de tir
            /*On fait un tirage entre 0 et la puissance du soldat*/
            soldat.pointsDeVie = soldat.pointsDeVie - (int)(Math.random() * TIR);
            if(soldat.pointsDeVie <= 0){ /*Si le soldat adverse est mort sur le coup*/
                carte.mort(soldat);
            }else if(getPos().distance(soldat.getPos()) <= soldat.getPortee()){ /* Si il n'est pas mort et que l'on est a sa portée il peut attaquer a son tour*/
                this.pointsDeVie = this.pointsDeVie - (int)(Math.random() * soldat.TIR);
                if(this.pointsDeVie <= 0) carte.mort(this);
            }
        }
    }

    public Position getPos(){ return this.pos;}

    public void seDeplace(Position newPos) { this.pos = newPos;}
    
    public String toString(){
        return " position : " + this.getPos().toString() + " Pdv : " + this.pointsDeVie + "/" + this.POINT_DE_VIE_MAX;
    }
}
