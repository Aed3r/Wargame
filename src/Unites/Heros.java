package Unites;

public class Heros extends Soldat{
    private final String NOM;
    private final TypesH TYPE;
    public Heros(Carte carte, TypesH type, String nom, Position pos) {
        super(carte, type.getPoints(), type.getPortee(),
        type.getPuissance(), type.getTir(), pos);
        NOM = nom; TYPE = type;
    }

    public void joueTour(int tour){

    }
    public void seDeplace(Position newPos){
    }
    public int getTour(){
        return 0;
    }
}
