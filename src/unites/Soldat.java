package unites;

import terrains.Carte;

import java.util.Random;

import misc.Element;
import wargame.ISoldat;

public abstract class Soldat extends Element implements ISoldat{
    private final int POINT_DE_VIE_MAX, PORTEE_VISUELLE, PUISSANCE, TIR; 
    private int pointsDeVie;
    private Carte carte;
    private Position pos;

    Soldat(Carte carte, int pts, int portee, int puiss, int tir, Position pos){
        this.POINT_DE_VIE_MAX = this.pointsDeVie = pts;
        this.PORTEE_VISUELLE = portee;
        this.PUISSANCE = puiss;
        this.TIR = tir;
        this.carte = carte; this.pos = pos;
    }

    @Override
    public int getPoints(){ return this.pointsDeVie;} 
    public abstract int getTour();
    public int getPortee() {return this.PORTEE_VISUELLE;}
    public Position getPosition() {return this.pos;}
    public abstract void joueTour(int tour);
    public void combat(Soldat soldat){ /*On considere que l'instance pour laquelle est appelée cette methode est la première a taper */
        /*Premièrement le cas d'un combat au corps-a-corps (adjacent)*/
        if(this.pos.estVoisine(soldat.pos)){
            /*On fait un tirage entre 0 et la puissance du soldat*/
            soldat.pointsDeVie = soldat.pointsDeVie - (int)(Math.random() * PUISSANCE);
            if(soldat.pointsDeVie <= 0){ /*Si le soldat adverse est mort sur le coup*/
                carte.mort(soldat);
            }else{ /* Si il n'est pas mort il peut attaquer a son tour*/
                this.pointsDeVie = this.pointsDeVie - (int)(Math.random() * soldat.PUISSANCE);
                if(this.pointsDeVie <= 0) carte.mort(this);
            }
        /*Ensuite on s'occupe du cas ou les soldats ne sont pas au corps a corp*/
        }
        
    }
    public abstract void seDeplace(Position newPos);
}
