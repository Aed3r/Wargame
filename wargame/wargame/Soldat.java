package wargame;

public abstract class Soldat extends Element implements ISoldat{
    private final int POINT_DE_VIE_MAX, PORTEE_VISUELE, PUISSANCE, TIR; 
    private int pointsDeVie;
    private Carte carte;
    private Position pos;

    Soldat(Carte carte, int pts, int portee, int puiss, int tir, position pos){
        this.POINT_DE_VIE_MAX = this.pointsDeVie = pts;
        this.PORTEE_VISUELE = portee;
        this.PUISSANCE = puiss;
        this.TIR = tir;
        this.carte = carte; this.pos = pos;
    }

    @Override
    public abstract int getPoints(); 
    public abstract int getTour(); 
    public abstract int getPortee();
    public abstract void joueTour(int tour);
    public abstract void combat(Soldat soldat);
    public abstract void seDeplace(Position newPos);
}
