package unites;

import terrains.Carte;

import java.awt.Color;

import misc.Position;


/**
 * Classe représentants les Héros, l'armée du joueur
 */
public class Heros extends Soldat {
    private static final long serialVersionUID = -7847862757650619304L;
    
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
        calculerVision(false);
    }

    public String toString(){
        return  TYPE + super.toString();
    }

    /**
     * @return Le nom associé au type de ce Héros
     */
    public String getNom(){
        if(this.getPointsMax() == 80){
            return "CHEVALIER";
        }else if(this.getPointsMax() == 30){
            return "GNOME";
        }else if(this.getPointsMax() == 60){
            return "BARBARE";
        }else return "ARCHER";
    };

    /**
     * Appelle la fonction récursive qui calcule les cases que le héros peut voir a partir de sa portée visuelle
     * @param cache si true, cache les éléments dans le champs de vision
     * */
    public void calculerVision(boolean cache){
        calcVisRec(this.getPortee(), this.getPos(), cache);
    }

    /**
     * Utilise récursivement Element.setVisible() sur les cases adjacentes, s'appelle également dessus avec une distance réduite de 1
     * @param distance Distance a laquelle on est censé voir depuis cette case
     * @param pos Position a partir de laquelle on applique cette distance
     * @param cache Si true on met les cases sur caché a la place
     */
    private void calcVisRec(int distance, Position pos, boolean cache){
        if(distance < 0 || !pos.estValide()) return;
        
        /*On rend tout d'abord visible la case en position pos*/
        if(cache){
            getCarte().getElement(pos).setCache();
        }else getCarte().getElement(pos).setVisible();

       /*On parcours ensuite toutes les positions voisine pour appeller recursivement la fonction*/
        Position test = new Position();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                test.set(pos.getX()+x, pos.getY()+y);
                if (pos.estVoisine(test))
                    calcVisRec(distance-1, test, cache);
            }
        }
    }

    /**
     * Déplace le soldat dans une case sans aucune vérification
     * @param newPos La position on le soldat se déplace
     */
    @Override
    public void seDeplace(Position newPos) { 
        // On cache les éléments autout de soit
        calculerVision(true);
        super.seDeplace(newPos);
        // On affiche les éléments autour de la nouvelle position
        calculerVision(false);
    }
}
