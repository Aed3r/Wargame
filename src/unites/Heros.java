package unites;

import terrains.Carte;

import java.awt.Color;

import misc.Position;

public class Heros extends Soldat {
    private TypesH TYPE;
    
    /**
     * Cree un heros d'un des types énuméré dans ISoldat selon son type 
     * @param carte La carte dont dépend le Heros
     * @param type Le type du Heros
     * @param nom Un nom pour le Heros
     * @param pos La position initiale du Heros
     * @param couleur Une couleur associée au Heros
     */
    public Heros(Carte carte, TypesH type, String nom, Position pos, Color couleur) {
        super(carte, type.getPoints(), type.getPortee(),
        type.getPuissance(), type.getTir(), pos, couleur, nom);
        TYPE = type;
    }

    public String toString(){
        return  TYPE + super.toString();
    }

    /**
     * @return Le nom associé au type de ce Héros
     */
    public String getNom(){return (String) TYPE.toString();};

    /**
     * Appelle la fonction récursive qui calcule les cases que le héros peut voir a partir de sa portée visuelle
     * */
    public void calculerVision(){
        calcVisRec(this.getPortee(), this.getPos(), false);
    }

    /**
     * Utilise récursivement Element.setVisible() sur les cases adjacentes, s'appelle également dessus avec une distance réduite de 1
     * @param distance Distance a laquelle on est censé voir depuis cette case
     * @param pos Position a partir de laquelle on applique cette distance
     * @param deplacement Si true on met les cases sur caché a la place
     */
    private void calcVisRec(int distance, Position pos, boolean deplacement){
        if(distance < 1) return;
        
        /*Puisque l'on travaille avec des hexagones il y a un decalage a prende en compte selon l'indice de la ligne*/
		int decalageX = 1, y;
        if(pos.getY() % 2 == 0) decalageX = -1;
        
        /*On rend tout d'abbord visible la case en position pos*/
        if(deplacement){
            getCarte().getElement(pos).setCache();
        }else getCarte().getElement(pos).setVisible();

        /*On parcours ensuite toutes les positions voisine pour appeller recursivement la fonction*/
        for(y=pos.getY()-1;y<pos.getY()+1;y++){
            if(y == pos.getY()){
                calcVisRec(distance - 1, new Position(y, pos.getX() - 1), deplacement);
                calcVisRec(distance - 1, new Position(y, pos.getX() + 1), deplacement);
            }else{
                calcVisRec(distance - 1, new Position(y, pos.getX()), deplacement);
                calcVisRec(distance - 1, new Position(y, pos.getX() + decalageX), deplacement);
            }
        }

    }
}
