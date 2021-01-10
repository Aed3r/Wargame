package terrains;

import misc.Position; 

public class Noeud {
    private float coutG; //Coût du point de départ au noeud
    private float coutH; //Coût du point d'arrivée au noeud
    private float coutF; //Coût total du noeud
    private Position pere = new Position (0, 0); //Position du père du noeud
    private Position pos = new Position(0, 0); //Position du noeud
    private int indice_pere; //Indice de position du père du noeud

    /**
     * Création d'un noeud pour lequel on devra initaliser toutes les valeurs
     */
    public Noeud () {}

    /**
     * Modifier la position du noeud
     * @param x 
     * @param y
     */
    public void setPos (int x, int y) {
        pos.setX(x);
        pos.setY(y);
    }

    /**
     * Obtenir la position X du noeud
     * @return Retourne l'ordonée du noeud
     */
    public int getXpos () {
        return pos.getX();
    }

    /**
     * Obtenir la position Y du noeud
     * @return Retourne l'abscisse du noeud
     */
    public int getYpos () {
        return pos.getY();
    }

    /**
     * Obtenir la position X du père du noeud
     * @return Retourne l'ordonée du père noeud
     */
    public int getXposPere () {
        return pere.getX();
    }

    /**
     * Obtenir la position Y du père du noeud
     * @return Retourne l'abscisse du père noeud
     */
    public int getYposPere () {
        return pere.getY();
    }

    /**
     * Modifier le coût au point de départ du noeud
     * @param coutG Flottant du coût
     */
    public void setCoutG (float coutG) {
        this.coutG = coutG;
    }

    /**
     * Modifier le coût au point d'arrivée du noeud
     * @param coutH Flottant du coût
     */
    public void setCoutH (float coutH) {
        this.coutH = coutH;
    }

    /**
     * Modifier le coût total du noeud
     * @param coutF Flottant du coût
     */
    public void setCoutF (float coutF) {
        this.coutF = coutF;
    }

    /**
     * Modifier l'indice de placement du père
     * @param i Entier indice
     */
    public void setPere (int i) {
        this.indice_pere = i;
    }

    /**
     * Modifier la Position du père
     * @param x Ordonée du père
     * @param y Abscisse du père
     */
    public void setPosPere (int x, int y) {
        pere.setX(x);
        pere.setY(y);
    }

    /**
     * Obtenir le coût de départ du noeud
     * @return Float du coût
     */
    public float getcoutG () {
        return coutG;
    }

    /**
     * Obtenir le coût de l'arrivée du noeud
     * @return Float du coût
     */
    public float getcoutH () {
        return coutH;
    }

    /**
     * Obtenir le coût total du noeud
     * @return Float du coût
     */
    public float getcoutF () {
        return coutF;
    }

    /**
     * Obtenir l'indice de position du père
     * @return Entier indice
     */
    public int getPere () {
        return indice_pere;
    }

    /**
     * Fonction toString affichant la position d'un noeud et son coût
     */
    public String toString () {
        return "Noeud "+getXpos()+";"+getYpos()+" CoutG : "+getcoutG()+" CoutH : "+getcoutH()+" CoutF : "+getcoutF();
    }
}
