package unites;

import terrains.Carte;
import java.awt.Color;
import misc.*;
import wargame.ISoldat;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public abstract class Soldat implements ISoldat {
    private final int POINT_DE_VIE_MAX, PORTEE_VISUELLE, PUISSANCE, TIR; 
    private int pointsDeVie;
    private Carte carte;
    private Position pos;
    private boolean tour; /*Permet de savoir si ce soldat a déja joué son tour*/
    private BufferedImage sprite;

    /**
     * Crée un soldat selon les caracteristiques en paramètre (CLASSE ABSTRAITE NE DOIT PAS ÊTRE APELLÉ DIRECTEMENT)
     * @param carte La carte dont le soldat dépend
     * @param pts Ses points de vie max
     * @param portee Sa portee
     * @param puiss Sa puissance d'attaque au corps a corps
     * @param tir Sa puissance de tir (0 si incapable de tirer)
     * @param pos Sa position initiale
     * @param couleur La couleur associée a ce soldat
     * @param nom Un nom
     */
    Soldat(Carte carte, int pts, int portee, int puiss, int tir, Position pos, Color couleur, String nom){
        this.POINT_DE_VIE_MAX = this.pointsDeVie = pts;
        this.PORTEE_VISUELLE = portee;
        this.PUISSANCE = puiss;
        this.TIR = tir;
        this.carte = carte;
        this.pos = pos;
    }

    /* Méthodes définies */
    
    /**
     * @return true si le joueur peut jouer son tour false sinon
     */
    public boolean getTour(){return tour;}
    
    /**
     * Joue le tour du soldat, retourne 0 si il a pu jouer son tour, -1 si son tour est déjà joué
     */
    public int joueTour(){
        if(tour == true){
            tour = false;
            return 0;
        }else{
            return -1;
        }
    }

    /**
     * @return les points de vie actuels du soldat
     */
    public int getPoints(){ return this.pointsDeVie;} 
    
    /**
     * @return la portee du soldat
     */
    public int getPortee() {return this.PORTEE_VISUELLE;}

    /**
     * Engage un combat avec le soldat en paramètre, détermine tout seul ce qui dois se passer.
     * @param soldat Le soldat a combatre
     */
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
        }else if(getPos().distance(soldat.getPos()) <= this.getPortee()){ //Si le soldat adverse est a portée de tir
            /*TODO On vérifie qu'il n'y aie pas d'obstacle entre les deux soldats*/
            
            /*On fait un tirage entre 0 et la puissance de tir du soldat*/
            soldat.pointsDeVie = soldat.pointsDeVie - (int)(Math.random() * TIR);
            if(soldat.pointsDeVie <= 0){ /*Si le soldat adverse est mort sur le coup*/
                carte.mort(soldat);
            }else if(getPos().distance(soldat.getPos()) <= soldat.getPortee()){ /* Si il n'est pas mort et que l'on est a sa portée il peut attaquer a son tour*/
                this.pointsDeVie = this.pointsDeVie - (int)(Math.random() * soldat.TIR);
                if(this.pointsDeVie <= 0) carte.mort(this);
            }
        }
    }

    /**
     * @return la position du soldat
     */
    public Position getPos(){ return this.pos;}

    /**
     * @return la carte associée au soldat
     */
    public Carte getCarte(){ return carte;}

    /**
     * Déplace le soldat dans une case sans aucune vérification
     * @param newPos La position on le soldat se déplace
     */
    public void seDeplace(Position newPos) { this.pos = newPos;}

    public String toString(){
        return " position : " + this.getPos().toString() + " Pdv : " + this.pointsDeVie + "/" + this.POINT_DE_VIE_MAX;
    }

    public abstract String getNom(); 

    public BufferedImage getSprite(){
        if (sprite == null) {
            // Chargement de l'image associée
            try {
                sprite = ImageIO.read(new File("data/img/soldats/" + getNom() + ".png"));
            } catch (IOException e) {
                // Problème lors du chargement, on utilise rien
                System.out.println(e.getLocalizedMessage());
            }
        }
        return sprite;
    }
}
