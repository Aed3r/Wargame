package terrains;

import misc.Element;
import misc.Parametres;
import misc.Position;
import unites.*;
import wargame.ISoldat.TypesH;
import wargame.ISoldat.TypesM;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class Carte implements wargame.IConfig, wargame.ICarte, Serializable {
    private static final long serialVersionUID = -1115730673450347942L;
    Element[][] grille = new Element[HAUTEUR_CARTE][LARGEUR_CARTE];
    private int minutesJouees = 0; // Utilisé pour la sauvegarde

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

    public int getMinutesJouees() {
        return this.minutesJouees;
    }

    public void addMinutesJouees(int minutesJouees) {
        this.minutesJouees += minutesJouees;
    }

    public void placementSoldatAlea () {
        int nb_heros = NB_HEROS;
        int nb_monstres = NB_MONSTRES;
        System.out.printf("nb heros : %d nb monstres : %d %n", nb_heros, nb_monstres);
        System.out.printf("init Monstre x : %d y : %d %n", POS_INIT_SPAWN_MONSTRE.getX(), POS_INIT_SPAWN_MONSTRE.getY());
        System.out.printf("init Heros x : %d y : %d %n", POS_INIT_SPAWN_GENTIL.getX(), POS_INIT_SPAWN_GENTIL.getY());

        if (HAUTEUR_SPAWN*LARGEUR_SPAWN < nb_heros || HAUTEUR_SPAWN*LARGEUR_SPAWN < nb_monstres) {
            System.out.println("Erreur placementHerosAlea Trop de soldats dans la zone de Spawn %n");
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
                System.out.printf("héros %d %d  %n", i, j);
                grille[i][j].setSoldat(new Heros(this, TypesH.getTypeHAlea(), "Adolf", new Position(i, j), Color.black));
                nb_heros--;
            }
            
            if (grille[iMonstre][jMonstre].getSoldat() == null && nb_monstres != 0) {
                System.out.printf("monstre %d %d  %n", iMonstre, jMonstre);
                grille[iMonstre][jMonstre].setSoldat(new Monstre(this, TypesM.getTypeMAlea(), "Gustav", new Position(iMonstre, jMonstre), Color.blue));
                nb_monstres--;
            }
        }
        System.out.println("test test %n");
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
        if(getElement(pos).getSoldat() == null)
            return false; 
        
        /*Si le soldat n'est pas un heros ou si un héros se trouve déjà en pos2*/
        if(!(getElement(pos).getSoldat() instanceof Heros) || getElement(pos2).getSoldat() instanceof Heros)
            return false;
        
        
        /*On essaie de jouer le tour du soldat*/
        if(!getElement(pos).getSoldat().joueTour()) return false;
        

        /*On va maintenant déterminer l'action a effectuer :*/
        /*Si la case n'est pas voisine on tente une attaque a distance */
        if(!pos.estVoisine(pos2)){
            /*Si le combat ne se fait pas on retourne false */
            if(!getElement(pos).getSoldat().combat(getElement(pos2).getSoldat()))
                return false;
        }

        /*On essaye de deplacer le soldat dans la case pos2, si on ne peut pas c'est qu'il y a un monstre*/
        if(!deplaceSoldat(pos2, getElement(pos).getSoldat())){
            if(getElement(pos2).getSoldat() != null){
                getElement(pos).getSoldat().combat(getElement(pos2).getSoldat());
            }else return false;
        }
        return true;
    }

    public boolean deplaceSoldat(Position pos, Soldat soldat){
        /*Si la position ou l'on veut se deplacer est vide et adjacente au soldat*/
        if(pos != null && getElement(pos) != null && getElement(pos).estAccessible() && 
           getElement(pos).getSoldat() == null && pos.estVoisine(soldat.getPos())){
            getElement(soldat.getPos()).setSoldat(null); /*On libère la case ou se trouvais le soldat*/
            soldat.seDeplace(pos);
            getElement(pos).setSoldat(soldat); /*On sauvegarde le soldat dans l'element de pos*/
            return true;
        }else return false;
    }

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
        System.out.println("%nAyou %n");
        if (!pos.estValide()) {
            System.out.println("Erreur trouvePositionVide la position est hors de la grille FIN");
            return null;
        }
        
        for (int i = posI-1; i < posI+2; i++) {
            for (int j = posJ-1; j < posJ+2; j++) {
                if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                    
                }
                else {
                    if (grille[i][j].estAccessible()) cmp++;
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
        int i; int j;

        while (test == 0) {
            randI = (int)(Math.random() * range) + min;
            randJ = (int)(Math.random() * range) + min;
            i = randI+posI;
            j = randJ+posJ;

            if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                //System.out.printf("Cas non valable : %d %d \n", i, j);
            }
            else {
                P.setX(i);
                P.setY(j);
                if (grille[i][j].estAccessible()) test = 1;
            }
        }
        return P;
    }

    /* Trouve un heros choisi aleatoirement parmi les 6 positions adjacentes de pos */
    public Heros trouveHeros(Position pos) {
        int posI = pos.getX();
        int posJ = pos.getY();
        int cmp = 0;
        System.out.println("%nAyou %n");
        if (!pos.estValide()) {
            System.out.println("Erreur trouvePositionVide la position est hors de la grille FIN");
            return null;
        }
        
        for (int i = posI-1; i < posI+2; i++) {
            for (int j = posJ-1; j < posJ+2; j++) {
                if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                    //System.out.printf("Cas non valable : %d %d \n", i, j);
                }
                else {
                    if (grille[i][j].getSoldat() != null && grille[i][j].getSoldat().estHeros()) cmp++;
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
        int i = 0; int j = 0;

        while (test == 0) {
            randI = (int)(Math.random() * range) + min;
            randJ = (int)(Math.random() * range) + min;
            i = randI+posI;
            j = randJ+posJ;

            if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                //System.out.printf("Cas non valable : %d %d \n", i, j);
            }
            else {
                System.out.printf("i j %d %d \n", i, j);
                System.out.printf("posI posJ %d %d \n", posI, posJ);
                System.out.printf("posI-1 posJ+1 %d %d \n", posI-1, posJ+1);
                System.out.printf("posI+1 posJ+1 %d %d \n", posI+1, posJ+1);

                P.setX(i);
                P.setY(j);
                if (grille[i][j].getSoldat() != null && grille[i][j].getSoldat().estHeros()) test = 1;
            }
        }
        System.out.printf("indice : %d %d %n", i, j);
        return (Heros)(grille[i][j].getSoldat());
    }

    /* Affichage basique du nom des élements de la grille */
    public void affiche_nul () {
        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                System.out.printf("%s ", grille[i][j].getNom());
            }
            System.out.printf("%n");
        }
    }

    /* Affichage basique du nom des élements de la grille */
    public void affiche_perso () {
        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                if (grille[i][j].getSoldat() != null) System.out.printf("o ");
                else System.out.printf("- ");
            }
            System.out.printf("%n");
        }
    }

    /**
     * Fini le tour des soldats joueur
     * @see Soldat#termineTour()
     * @return la liste de soldats ayant perdues de la vie 
     */
    public ArrayList<Soldat>  terminerTour () {
        ArrayList<Soldat> tmp = new ArrayList<>();

        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                Soldat s = grille[i][j].getSoldat();
                if (s != null && s.termineTour()) tmp.add(s);
            }
        }

        return tmp;
    }

    /**
     * Joue le tour des ennemis de la carte
     * @see Monstre#joueTour()
     * @return la liste de héros attaqué
     */
    public ArrayList<Heros> jouerEnnemis () {
        ArrayList<Heros> herosAttaque = new ArrayList<>();

        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                Soldat s = grille[i][j].getSoldat();
                if (s != null && !s.estHeros()) {
                    Heros tmp = ((Monstre) s).jouer();
                    if (tmp != null) herosAttaque.add(tmp);
                }
            }
        }

        return herosAttaque;
    }


    /**
     * Affiche sur g tous les éléments constituant la carte courante
     * @param g un object graphique quelconque
     * @param tabHitbox le tableau des hitbox
     * @see Element#setReafficher(boolean)
     */
    public void afficher (Graphics g, byte[][][] tabHitbox) {
        int i, j, x, y;
        float multTaille = 1;

        String val = Parametres.getParametre("modePerf");
        if (val == null || val.equals(PARAMETRES[3][2]))
            multTaille = MULTTAILLEPERF;
        
        // lignes
        for (i = 0; i < HAUTEUR_CARTE; i++) {
            // colonnes
            for (j = 0; j < LARGEUR_CARTE; j++) {
                x = (int) ((MARGX + j * TAILLEX) * multTaille);
                y = (int) ((MARGY + i * TAILLEY) * multTaille);
                if (i%2 == 0) x += (TAILLEX*multTaille)/2;
                
                grille[i][j].afficher(g, x, y, tabHitbox);  
            }
        }
    }
}
