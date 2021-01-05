package terrains;

import misc.Element;
import misc.Position;
import unites.*;
import java.awt.*;
import misc.Parametres;

public class Carte implements wargame.IConfig {
    Element[][] grille = new Element[HAUTEUR_CARTE][LARGEUR_CARTE];

    public Carte () {
        /* Construction de la carte avec élements aléatoires et un contour de forêt */
        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                if (i == 0 || i == HAUTEUR_CARTE-1 || j == 0 || j == LARGEUR_CARTE-1) {
                    grille[i][j] = new Element(misc.Element.TypeElement.FORET, new Position(i, j));
                }
                else grille[i][j] = new Element (new Position(i, j));
            }
        }

        /* Placement des zones de spawn pour l'instant des plaines */
        for (int i = POS_INIT_SPAWN_MONSTRE.getX(); i <= HAUTEUR_SPAWN; i++) {
            for (int j = POS_INIT_SPAWN_MONSTRE.getY(); j <= LARGEUR_SPAWN; j++) {
                grille[i][j] = new Element(misc.Element.TypeElement.PLAINE, new Position(i, j));
            }
        } 

        for (int i = POS_INIT_SPAWN_GENTIL.getX(); i <= POS_INIT_SPAWN_GENTIL.getX()+HAUTEUR_SPAWN-1; i++) {
            for (int j = POS_INIT_SPAWN_GENTIL.getY(); j <= POS_INIT_SPAWN_GENTIL.getY()+LARGEUR_SPAWN-1; j++) {
                grille[i][j] = new Element(misc.Element.TypeElement.PLAINE, new Position(i, j));
            }
        }
    }

    public void mort(Soldat soldat){
        getElement(soldat.getPos()).setSoldat(null);/*On libère la case ou se trouvais le soldat*/
        soldat.seDeplace(null); /*On met ensuite le soldat dans une position nulle pour signifier qu'il n'est plus de ce monde*/
    }

    /*Appélée quand le joueur donne l'ordre au heros en position pos de faire une action en pos2
    si la case est accessible il se déplace, si il y a un enemis il attaque*/
    public boolean actionHeros(Position pos, Position pos2){
        
        /*Vérifications préalables :*/
        /*Si pos2 n'est pas valide ou qu'il n'y a pas de soldat en pos ou que les cases ne sont pas adjacente*/
        if(!pos2.estValide() || getElement(pos).getSoldat() == null || !pos.estVoisine(pos2)) 
            return false; 
        
        /*Si le solda n'est pas un heros ou si un héros se trouve déjà en pos2 ou si la variable tour du soldat vaux false*/
        if(!(getElement(pos).getSoldat() instanceof Heros) || getElement(pos2).getSoldat() instanceof Heros || !getElement(pos).getSoldat().getTour()) 
            return false;
        
        /*Si la case ou le héros essaye de se déplacer est un obstacle*/
        if(!getElement(pos2).estAccessible())
            return false;
        
        /*On va maintenant déterminer l'action a effectuer :*/
        /*On essaye de deplacer le soldat dans la case pos2, si on ne peut pas c'est qu'il y a un monstre*/
        if(!deplacerSoldat(pos2, getElement(pos).getSoldat())){
             getElement(pos).getSoldat().combat(getElement(pos2).getSoldat());
        }
        return true;
    }

    public boolean deplacerSoldat(Position pos, Soldat soldat){
        /*Si la position ou l'on veut se deplacer est vide et adjacente au soldat*/
        if(getElement(pos).estAccessible() && getElement(pos).getSoldat() == null && pos.estVoisine(soldat.getPos())){
            getElement(soldat.getPos()).setSoldat(null); /*On libère la case ou se trouvais le soldat*/
            soldat.seDeplace(pos);
            getElement(pos).setSoldat(soldat); /*On sauvegarde le soldat dans l'element de pos*/
            return true;
        }else return false;
    }

    public void placerSoldat (int nb_soldat) {}

    /* Retourne l'élement à la position pos dans la grille */
    public Element getElement(Position pos) {
        int i = pos.getX();
        int j = pos.getY();

        if (!pos.estValide()) {
            System.out.println("Erreur getElement la position est hors de la grille FIN");
            return null;
        }
        return grille[i][j];     
    }

    /* Trouve une position vide choisie aleatoirement parmi les 8 positions adjacentes de pos */
    public Position trouvePositionVide(Position pos) {
        int posI = pos.getX();
        int posJ = pos.getY();
        int cmp = 0;
        System.out.println("\nAyou \n");
        if (!pos.estValide()) {
            System.out.println("Erreur trouvePositionVide la position est hors de la grille FIN");
            return null;
        }
        
        for (int i = posI-1; i < posI+2; i++) {
            for (int j = posJ-1; j < posJ+2; j++) {
                if ((i != posI && j != posJ) || (i != posI-1 && j != posJ+1) || (i != posI+1 && j != posJ+1)) {
                    if (grille[posI][posJ].estAccessible()) cmp++;
                }
            }
        }

        if (cmp == 0) {
            System.out.println("Erreur trouvePositionVide pas de cases vides adjacentes FIN");
            return null;
        }

        Position P = new Position(0, 0);
        int test = 0;

        int max = 1; int min = -1; 
        int range = max - min + 1; 
        int randI; int randJ;

        while (test == 0) {
            randI = (int)(Math.random() * range) + min;
            randJ = (int)(Math.random() * range) + min;
            if ((randI != posI && randJ != posJ) || (randI != posI-1 && randJ != posJ+1) || (randI != posI+1 && randJ != posJ+1)) {
                P.setX(randI);
                P.setY(randJ);
                if (grille[posI][posJ].estAccessible()) test = 1;
            }
        }
        return P;
    }

    /* Trouve un heros choisi aleatoirement parmi les 8 positions adjacentes de pos */
    public Heros trouveHeros(Position pos) {
        int posI = pos.getX();
        int posJ = pos.getY();
        int cmp = 0;
        System.out.println("\nAyou \n");
        if (!pos.estValide()) {
            System.out.println("Erreur trouvePositionVide la position est hors de la grille FIN");
            return null;
        }
        
        for (int i = posI-1; i < posI+2; i++) {
            for (int j = posJ-1; j < posJ+2; j++) {
                if ((i != posI && j != posJ) || (i != posI-1 && j != posJ+1) || (i != posI+1 && j != posJ+1)) {
                    if (grille[posI][posJ].estHeros()) cmp++;
                }
            }
        }

        if (cmp == 0) {
            System.out.println("Erreur trouveHeros pas de héros dans les cases adjacentes FIN");
            return null;
        }

        Position P = new Position(0, 0);
        int test = 0;

        int max = 1; int min = -1; 
        int range = max - min + 1; 
        int randI; int randJ;

        while (test == 0) {
            randI = (int)(Math.random() * range) + min;
            randJ = (int)(Math.random() * range) + min;
            if ((randI != posI && randJ != posJ) || (randI != posI-1 && randJ != posJ+1) || (randI != posI+1 && randJ != posJ+1)) {
                P.setX(randI);
                P.setY(randJ);
                if (grille[posI][posJ].estHeros()) test = 1;
            }
        }
        return (Heros)grille[posI][posJ].getSoldat();
    }

    /* Affichage basique du nom des élements de la grille */
    public void affiche_nul () {
        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                System.out.printf("%s ", grille[i][j].getNom());
            }
            System.out.printf("\n");
        }
    }


    /**
     * Affiche sur g tous les éléments constituant la carte courante
     * @param g un object graphique quelconque
     */
    public void afficher (Graphics g) {
        int i, j, x, y;

        if (!Element.getReafficher()) return;

        // lignes
        for (i = 0; i < HAUTEUR_CARTE; i++) {
            // colonnes
            for (j = 0; j < LARGEUR_CARTE; j++) {
                x = MARGX + j*TAILLEX;
                y = (MARGY + i*TAILLEY);
                
                // Déplacement vertical aléatoire des éléments 
                if (Parametres.getParametre("deplacementVert").equals("allumé"))
                    y += Math.random()*50-25;             

                if (i%2 == 0)
                    grille[i][j].afficher(g, x+TAILLEX/2, y);
                else
                    grille[i][j].afficher(g, x, y);   
            }
        }

        Element.setReafficher(false);
    }
}
