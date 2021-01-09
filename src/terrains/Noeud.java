package terrains;

import misc.Position; 

public class Noeud {
    private float coutG;
    private float coutH;
    private float coutF;
    private Position pere = new Position (0, 0);
    private Position pos = new Position(0, 0);
    private int indice_pere;


    public Noeud () {}

    /* public Noeud (int coutG, int coutH, int coutF, Position pere, Position pos) {
        this.coutG = coutG;
        this.coutH = coutH;
        this.coutF = coutF;
        this.pere.setX(pere.getX());
        this.pere.setY(pere.getY());
        this.pos.setX(pos.getX());
        this.pos.setY(pos.getY());
    } */

    public void setPos (int x, int y) {
        pos.setX(x);
        pos.setY(y);
    }

    public int getXpos () {
        return pos.getX();
    }

    public int getYpos () {
        return pos.getY();
    }

    public int getXposPere () {
        return pere.getX();
    }

    public int getYposPere () {
        return pere.getY();
    }

    public void setCoutG (float coutG) {
        this.coutG = coutG;
    }

    public void setCoutH (float coutH) {
        this.coutH = coutH;
    }

    public void setCoutF (float coutF) {
        this.coutF = coutF;
    }

    public void setPere (int i) {
        this.indice_pere = i;
    }

    public void setPosPere (int x, int y) {
        pere.setX(x);
        pere.setY(y);
    }

    public float getcoutG () {
        return coutG;
    }

    public float getcoutH () {
        return coutH;
    }

    public float getcoutF () {
        return coutF;
    }

    public int getPere () {
        return indice_pere;
    }

    public String toString () {
        return "Noeud "+getXpos()+";"+getYpos()+" CoutG : "+getcoutG()+" CoutH : "+getcoutH()+" CoutF : "+getcoutF();
    }
}
