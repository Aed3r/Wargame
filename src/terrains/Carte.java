package terrains;

import misc.Element;
import misc.Position;
import unites.*;
import wargame.ISoldat.TypesH;
import wargame.ISoldat.TypesM;

import java.awt.*;

public class Carte implements wargame.IConfig, wargame.ICarte {
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

    public void placementSoldatAlea () {
        int nb_heros = NB_HEROS;
        int nb_monstres = NB_MONSTRES;
        System.out.printf("nb heros : %d nb monstres : %d \n", nb_heros, nb_monstres);
        System.out.printf("init Monstre x : %d y : %d \n", POS_INIT_SPAWN_MONSTRE.getX(), POS_INIT_SPAWN_MONSTRE.getY());
        System.out.printf("init Heros x : %d y : %d \n", POS_INIT_SPAWN_GENTIL.getX(), POS_INIT_SPAWN_GENTIL.getY());

        if (HAUTEUR_SPAWN*LARGEUR_SPAWN < nb_heros || HAUTEUR_SPAWN*LARGEUR_SPAWN < nb_monstres) {
            System.out.println("Erreur placementHerosAlea Trop de soldats dans la zone de Spawn \n");
            System.exit(-1);
        }
        int i; int j; int iMonstre; int jMonstre;
        int max = LARGEUR_SPAWN-1; int min = 0; 
        int range = max - min + 1; 
        int randI; int randJ;

        while (nb_heros > 0 || nb_monstres > 0) {
            randI = (int)(Math.random() * range) + min;
            randJ = (int)(Math.random() * range) + min;
            i = randI + POS_INIT_SPAWN_GENTIL.getX();
            j = randJ + POS_INIT_SPAWN_GENTIL.getY();
            iMonstre = randI + POS_INIT_SPAWN_MONSTRE.getX();
            jMonstre = randJ + POS_INIT_SPAWN_MONSTRE.getY();

            if (grille[i][j].getSoldat() == null && nb_heros != 0) {
                System.out.printf("héros %d %d  \n", i, j);
                grille[i][j].setSoldat(new Heros(this, TypesH.getTypeHAlea(), "Adolf", new Position(i, j), Color.black));
                nb_heros--;
            }
            
            if (grille[iMonstre][jMonstre].getSoldat() == null && nb_monstres != 0) {
                System.out.printf("monstre %d %d  \n", iMonstre, jMonstre);
                grille[iMonstre][jMonstre].setSoldat(new Monstre(this, TypesM.getTypeMAlea(), "Gustav", new Position(iMonstre, jMonstre), Color.blue));
                nb_monstres--;
            }
        }
        System.out.println("test test \n");
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
        if(!pos2.estValide() || getElement(pos).getSoldat() == null || !pos.estVoisine(pos2)){
            System.out.println("1");
            return false; 
        }
        /*Si le solda n'est pas un heros ou si un héros se trouve déjà en pos2 ou si la variable tour du soldat vaux false*/
        if(!(getElement(pos).getSoldat() instanceof Heros) || getElement(pos2).getSoldat() instanceof Heros || !getElement(pos).getSoldat().getTour()){
            System.out.println("2");    
            return false;
        }
        
        /*On va maintenant déterminer l'action a effectuer :*/
        /*On essaye de deplacer le soldat dans la case pos2, si on ne peut pas c'est qu'il y a un monstre*/
        if(!deplaceSoldat(pos2, getElement(pos).getSoldat())){
            if(getElement(pos2).getSoldat() != null){
                getElement(pos).getSoldat().combat(getElement(pos2).getSoldat());
            }else{
                System.out.println("3");    
                return false;
            }
        }
        return true;
    }

    public boolean deplaceSoldat(Position pos, Soldat soldat){
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

    /* Retourne l'élement à la position (x, y) dans la grille */
    public Element getElement(int x, int y) {
        return getElement(new Position(x, y));
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
        System.out.printf("indice : %d %d \n", posI, posJ);
        return (Heros)(grille[posI][posJ].getSoldat());
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

    /* Affichage basique du nom des élements de la grille */
    public void affiche_perso () {
        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                if (grille[i][j].getSoldat() != null) System.out.printf("o ");
                else System.out.printf("- ");
            }
            System.out.printf("\n");
        }
    }


    /**
     * Affiche sur g tous les éléments constituant la carte courante
     * @param g un object graphique quelconque
     * @param tabHitbox le tableau des hitbox
     * @param reafficher s'il suffit de réafficher les éléments marqués par setReafficher(true)
     */
    public void afficher (Graphics g, byte[][][] tabHitbox, boolean reafficher) {
        int i, j, x, y;
        
        // lignes
        for (i = 0; i < HAUTEUR_CARTE; i++) {
            // colonnes
            for (j = 0; j < LARGEUR_CARTE; j++) {
                x = MARGX + j*TAILLEX;
                y = MARGY + i*TAILLEY;   
                if (i%2 == 0) x += TAILLEX/2;
                
                if (reafficher) {
                    if (grille[i][j].getReafficher())
                        grille[i][j].reafficher(g, tabHitbox, this);
                } else grille[i][j].afficher(g, x, y, tabHitbox);  
            }
        }
    }
}
