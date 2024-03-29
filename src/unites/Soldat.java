package unites;

import terrains.Carte;
import java.awt.Color;
import misc.*;
import wargame.IConfig;
import wargame.ISoldat;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.Serializable;

/**
 * Classe abstraite décrivant les méthodes et variables communes aux deux factions
 */
public abstract class Soldat implements ISoldat, IConfig, Serializable {
    private static final long serialVersionUID = -3905888721768484824L;
    private final int POINT_DE_VIE_MAX, PORTEE_VISUELLE, PUISSANCE, TIR;
    private int pointsDeVie;
    private Carte carte;
    private Position pos;
    private boolean tour = true; /*Permet de savoir si ce soldat a déja joué son tour*/
    private transient BufferedImage sprite, spriteDegat;
    private boolean afficherSpriteDegat = false;

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
     * Joue le tour du soldat, retourne true si il a pu jouer son tour, false si son tour est déjà joué
     */
    public boolean joueTour(){
        if(tour == true){
            tour = false;
            return true;
        }else{
            return false;
        }
    }

    /**
     * Termine le tour du soldat, si il n'as pas bougé ce tour ci il recupère des pv.
     * Si il se trouve dans un désert il perd des pv
     * @return true s'il faut afficher une animation de dégats, false sinon
     */
    public boolean termineTour(){
        int pdvAvant = getPoints();
        /*Si le soldat n'as pas joué ce tour ci et qu'il ne se trouve pas sur une case qui cause des dégats il recupère 10% de ses pv max*/
        if(tour == true && carte.getElement(this.pos).getPDVPerdues() == 0){
            this.pointsDeVie = this.pointsDeVie + (int)(this.POINT_DE_VIE_MAX *0.1);
        }else{
            this.pointsDeVie = this.pointsDeVie - (int) carte.getElement(this.pos).getPDVPerdues();
        }  
        tour = true;
        /*On vérifie que le soldat n'est pas mort, ou que son nombre de points de vie n'est pas au dessus du maximum */
        if(this.pointsDeVie <= 0)
            carte.mort(this);
        else {
            if (this.pointsDeVie > POINT_DE_VIE_MAX) this.pointsDeVie = POINT_DE_VIE_MAX;
            if (pdvAvant < getPoints()) carte.getElement(this.pos).setReafficher(); // réaffichage normal
            else if (pdvAvant > getPoints()) return true; // afficher animation dégat
        }
        return false;
    }

    /**
     * @return les points de vie actuels du soldat
     */
    public int getPoints(){ return this.pointsDeVie;} 
    
    /**
     * @return les points de vie maximum du soldat
     */
    public int getPointsMax(){ return this.POINT_DE_VIE_MAX;}

    /**
     * @return la portee du soldat
     */
    public int getPortee() {return this.PORTEE_VISUELLE;}

    /**
     * Engage un combat avec le soldat en paramètre, détermine tout seul ce qui dois se passer.
     * @param soldat Le soldat a combatre
     * @return true si les soldat on pu combatre, false sinon
     */
    public boolean combat(Soldat soldat){ /*On considere que l'instance pour laquelle est appelée cette methode est la première a taper */
        if(soldat == null) return false;
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
        }else if(getPos().distance(soldat.getPos(), carte) <= this.getPortee()){ //Si le soldat adverse est a portée de tir
            /*On fait un tirage entre 0 et la puissance de tir du soldat*/
            soldat.pointsDeVie = soldat.pointsDeVie - (int)(Math.random() * TIR);
            if(soldat.pointsDeVie <= 0){ /*Si le soldat adverse est mort sur le coup*/
                carte.mort(soldat);
            }else if(getPos().distance(soldat.getPos(), carte) <= soldat.getPortee()){ /* Si il n'est pas mort et que l'on est a sa portée il peut attaquer a son tour*/
                this.pointsDeVie = this.pointsDeVie - (int)(Math.random() * soldat.TIR);
                if(this.pointsDeVie <= 0) carte.mort(this);
            }
        }else return false;
        return true;
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
    public void seDeplace(Position newPos) { this.pos = newPos; }

    public String toString(){
        return " position : " + this.getPos().toString() + " Pdv : " + this.pointsDeVie + "/" + this.POINT_DE_VIE_MAX;
    }

    public abstract String getNom(); 

    public BufferedImage getSprite(){
        if (sprite == null) {
            // Chargement de l'image associée
            try {
                String val = Parametres.getParametre("modePerf");
                if (val == null || val.equals(PARAMETRES[3][2])) 
                    sprite = ImageIO.read(getClass().getResourceAsStream("/img/soldats/" + getNom() + "Perf.png"));
                else    
                    sprite = ImageIO.read(getClass().getResourceAsStream("/img/soldats/" + getNom() + ".png"));
            } catch (IOException e) {
                // Problème lors du chargement, on utilise rien
                System.out.println(e.getLocalizedMessage());
                return null;
            }

            // Sprite dégat
            spriteDegat = tint(sprite, Color.RED);
        }
        if (afficherSpriteDegat) return spriteDegat;
        else return sprite;
    }

    public BufferedImage getSpriteDegat () {
        return spriteDegat;
    }

    /**
     * Applique la teinture color à image et laisse le channel alpha intact
     * @param image une image à teinter
     * @param color la couleur de la teinte
     * @return une nouvelle image correspondant à image teinté
     * @author Oleg Mikhailov
     * @see https://stackoverflow.com/a/36744345/5591299
     */
    private static BufferedImage tint(BufferedImage image, Color color) {
        BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				Color pixelColor = new Color(image.getRGB(x, y), true);
				int r = (pixelColor.getRed() + color.getRed()) / 2;
				int g = (pixelColor.getGreen() + color.getGreen()) / 2;
				int b = (pixelColor.getBlue() + color.getBlue()) / 2;
				int a = pixelColor.getAlpha();
				int rgba = (a << 24) | (r << 16) | (g << 8) | b;
				tmp.setRGB(x, y, rgba);
			}
        }
        return tmp;
    }
    

    public boolean getAfficherSpriteDegat() {
        return this.afficherSpriteDegat;
    }

    public void setAfficherSpriteDegat(boolean afficherSpriteDegat) {
        this.afficherSpriteDegat = afficherSpriteDegat;
    }


    public boolean estHeros () {
		return this instanceof Heros;
    }

    public int getPUISSANCE() {
        return this.PUISSANCE;
    }

    public int getTIR() {
        return this.TIR;
    }    
}
