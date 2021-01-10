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

import javax.swing.text.WrappedPlainView;

public class Carte implements wargame.IConfig, wargame.ICarte, Serializable {
    private static final long serialVersionUID = -1115730673450347942L;
    Element[][] grille = new Element[HAUTEUR_CARTE][LARGEUR_CARTE];
    private int minutesJouees = 0; // Utilisé pour la sauvegarde
    private int difficulte = 1;

    /**
     * Construit une carte d'éléments aléatoires avec les informations de IConfig
     * @see Element
     */
    public Carte () {;
        /* Gestion de la difficulté */
        if (Parametres.getParametre("difficulte").equals("simple")) difficulte = 3;
        if (Parametres.getParametre("difficulte").equals("normale")) difficulte = 2;
        if (Parametres.getParametre("difficulte").equals("difficile")) difficulte = 1;

        /* Construction de la carte avec élements aléatoires et un contour de forêt */
        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                if (i == 0 || i == HAUTEUR_CARTE-1 || j == 0 || j == LARGEUR_CARTE-1) {
                    /* Création d'un contours inacessible du plateau, ici de type Forêt*/
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

        /* Zone de Spawn des héros */
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


    /**
     * Fonction de placement aléatoire des Soldats sur la carte de jeu
     */
    public void placementSoldatAlea () {
        /* Récupération des configurations de IConfig */
        int nb_heros = NB_HEROS;
        int nb_monstres = NB_MONSTRES/difficulte;

        /* Test que le nombre de soldats ne dépassent pas les zones de spawn */
        if (HAUTEUR_SPAWN*LARGEUR_SPAWN < nb_heros || HAUTEUR_SPAWN*LARGEUR_SPAWN < nb_monstres) {
            System.out.println("Erreur placementHerosAlea Trop de soldats dans la zone de Spawn %n");
            System.exit(-1);
        }

        int i; int j; int iMonstre; int jMonstre;
        int max = LARGEUR_SPAWN-1; int min = 0; 
        int range = max - min + 1; 
        int randI; int randJ;

        /* Placement aléatoires dans chaque zone de spawn des Héros et des Monstres */
        while (nb_heros > 0 || nb_monstres > 0) {
            /* Calcul des valeurs des variables aléatoires à chaque tour de boucle */
            randI = (int)(Math.random() * range) + min;
            randJ = (int)(Math.random() * range) + min;
            i = randI + POS_INIT_SPAWN_GENTIL.getX();
            j = randJ + POS_INIT_SPAWN_GENTIL.getY();
            iMonstre = randI + POS_INIT_SPAWN_MONSTRE.getX();
            jMonstre = randJ + POS_INIT_SPAWN_MONSTRE.getY();

            /* Si il n'y a pas déjà de Héros à cet emplacement on peut placer le nouveau Héros */
            if (grille[i][j].getSoldat() == null && nb_heros != 0) {
                grille[i][j].setSoldat(new Heros(this, TypesH.getTypeHAlea(), "gentil", new Position(i, j), Color.black));
                nb_heros--;
            }
            
            /* Si il n'y a pas déjà de Monstres à cet emplacement on peut placer le nouveau Monstre */
            if (grille[iMonstre][jMonstre].getSoldat() == null && nb_monstres != 0) {
                grille[iMonstre][jMonstre].setSoldat(new Monstre(this, TypesM.getTypeMAlea(), "mechant", new Position(iMonstre, jMonstre), Color.blue));
                nb_monstres--;
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
        if(getElement(pos).getSoldat() == null)
            return false; 
        
        /*Si le soldat n'est pas un heros ou si un héros se trouve déjà en pos2*/
        if(!(getElement(pos).getSoldat() instanceof Heros) || getElement(pos2).getSoldat() instanceof Heros)
            return false;
        

        /*On va maintenant déterminer l'action a effectuer :*/
        /*Si la case n'est pas voisine on tente une attaque a distance */
        if(!pos.estVoisine(pos2)){
            /*Si le combat ne se fait pas on retourne false */
            if(!getElement(pos).getSoldat().joueTour()) return false;
            if(!getElement(pos).getSoldat().combat(getElement(pos2).getSoldat())){
                return false;
            }
            
        }

        /*On essaye de deplacer le soldat dans la case pos2, si on ne peut pas c'est qu'il y a un monstre*/
        if(!getElement(pos).getSoldat().getTour()) return false;
        if(!deplaceSoldat(pos2, getElement(pos).getSoldat())){
            if(getElement(pos2).getSoldat() != null){
                getElement(pos).getSoldat().combat(getElement(pos2).getSoldat());
                getElement(pos).getSoldat().joueTour();
            }else return false;
        }else getElement(pos2).getSoldat().joueTour();
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

    /**
     * Retourne l'élement à la position pos dans la grille
     * @return L'élément trouvé 
     * @param pos La position dans la grille à laquelle on veut obtenir l'élément
     */
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

    /**
     * Trouve une position vide (où l'on peut se déplacer) choisie aleatoirement parmi les 6 positions adjacentes de pos
     * @return null si l'on ne trouve pas de positions vides et la position trouvée sinon
     * @param pos La position autour de laquelle on cherche une position vide
     */
    public Position trouvePositionVide(Position pos) {
        /* Récupération des coordonées de la position passée en paramètres */
        int posI = pos.getX();
        int posJ = pos.getY();
        int cmp = 0;

        /* On vérifie que la position de laquelle on part est dans le plateau de jeu */
        if (!pos.estValide()) {
            System.out.println("Erreur trouvePositionVide la position est hors de la grille FIN");
            return null;
        }
        
         /* On regarde déjà si il existe bien une PositionVide dans les cases adjacentes */
        for (int i = posI-1; i < posI+2; i++) {
            for (int j = posJ-1; j < posJ+2; j++) {
                if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                    /* On vérifie ici que l'on ne prend pas en compte la positions de départ elle même
                       ou les positions qui ne sont pas accessibles dans le cas d'une grille hexagonale */
                }
                else {
                    /* On vérifie si il y'a un soldat sur la case accesible */
                    if (grille[i][j].estAccessible() && grille[i][j].getSoldat() == null) cmp++; 
                }
            }
        }

        /* On renvoie null si on ne trouve pas de positions vides */
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

        /* On tire aléatoirement une case dans les cases adjacentes tant que l'on ne trouve pas la case vide repérée auparavant */
        while (test == 0) {
            randI = (int)(Math.random() * range) + min;
            randJ = (int)(Math.random() * range) + min;
            i = randI+posI;
            j = randJ+posJ;

            if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                /* On vérifie ici que l'on ne prend pas en compte la positions de départ elle même
                    ou les positions qui ne sont pas accessibles dans le cas d'une grille hexagonale */
            }
            else {
                P.setX(i);
                P.setY(j);
                if (grille[i][j].estAccessible() && grille[i][j].getSoldat() == null) test = 1;
            }
        }
        /* On renvoie la position vide trouvée */
        return P;
    }

    /**
     * Trouve un heros choisi aleatoirement parmi les 6 positions adjacentes de pos
     * @return null si l'on ne trouve pas de héros et le Héros trouvé sinon
     * @param pos La position autour de laquelle on cherche un Héros
     */
    public Heros trouveHeros(Position pos) {
        /* Récupération des coordonées de la position passée en paramètres */
        int posI = pos.getX();
        int posJ = pos.getY();
        int cmp = 0;

        /* On vérifie que la position de laquelle on part est dans le plateau de jeu */
        if (!pos.estValide()) {
            System.out.println("Erreur trouvePositionVide la position est hors de la grille FIN");
            return null;
        }
        
        /* On regarde déjà si il existe bien un Héros dans les cases adjacentes */
        for (int i = posI-1; i < posI+2; i++) {
            for (int j = posJ-1; j < posJ+2; j++) {
                if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                    /* On vérifie ici que l'on ne prend pas en compte la positions de départ elle même
                       ou les positions qui ne sont pas accessibles dans le cas d'une grille hexagonale */
                }
                else {
                    if (grille[i][j].getSoldat() != null && grille[i][j].getSoldat().estHeros()) cmp++;
                }
            }
        }

        /* On renvoie null si on ne trouve pas de héros */
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

        /* On tire aléatoirement une case dans les cases adjacentes tant que l'on ne trouve pas le héros repéré auparavant */
        while (test == 0) {
            randI = (int)(Math.random() * range) + min;
            randJ = (int)(Math.random() * range) + min;
            i = randI+posI;
            j = randJ+posJ;

            if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                /* On vérifie ici que l'on ne prend pas en compte la positions de départ elle même
                    ou les positions qui ne sont pas accessibles dans le cas d'une grille hexagonale */
            }
            else {
                P.setX(i);
                P.setY(j);
                if (grille[i][j].getSoldat() != null && grille[i][j].getSoldat().estHeros()) test = 1;
            }
        }
        /* On renvoie le héros à la position trouvée */
        return (Heros)(grille[i][j].getSoldat());
    }

    /**
     * Affichage basique du nom des élements de la grille
     */
    public void affiche_nul () {
        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                System.out.printf("%s ", grille[i][j].getNom());
            }
            System.out.printf("%n");
        }
    }

    /**
     * Affichage basique des soldats de la grille
     */
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
     * Fini le tour des soldats indiqué en paramètre
     * @param tourJoueur si true: finir le tour du joueur, sinon finir le tour des ennemis
     * @see Soldat#termineTour()
     * @return la liste de soldats ayant perdues de la vie 
     */
    public ArrayList<Soldat>  terminerTour (boolean tourJoueur) {
        ArrayList<Soldat> tmp = new ArrayList<>();

        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                Soldat s = grille[i][j].getSoldat();
                if (s != null && s.estHeros() == tourJoueur && s.termineTour()) 
                    tmp.add(s);
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
