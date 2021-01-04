package terrains;

import misc.Element;
import misc.Position;
import unites.*;
import java.awt.*;
import java.util.Random;
import misc.Parametres;

public class Carte implements wargame.IConfig {
    Element[][] grille = new Element[HAUTEUR_CARTE][LARGEUR_CARTE];

    public Carte () {

        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                if (i == 0 || i == HAUTEUR_CARTE-1 || j == 0 || j == LARGEUR_CARTE-1) {
                    grille[i][j] = new Element(misc.Element.TypeElement.FORET, new Position(i, j));
                }
                else grille[i][j] = new Element (new Position(i, j));
            }
        }      
    }

    public void mort(Soldat soldat){}

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
    }
}
