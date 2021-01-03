package terrains;

import misc.Element;
import misc.Position;
import unites.Soldat;
import java.awt.*;
import java.util.Random;
import misc.Parametres;

public class Carte implements wargame.IConfig {
    Element[][] grille = new Element[HAUTEUR_CARTE][LARGEUR_CARTE];

    public Carte () {

        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < LARGEUR_CARTE; j++) {
                grille[i][j] = new Element (new Position(i, j));
            }
        }      
    }

    public void mort(Soldat soldat){}

    public void affiche_nul () {
        for (int i = 0; i < LARGEUR_CARTE; i++) {
            for (int j = 0; j < HAUTEUR_CARTE; j++) {
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

        Random rand = new Random();

        // lignes
        for (i = 0; i < HAUTEUR_CARTE; i++) {
            // colonnes
            for (j = 0; j < LARGEUR_CARTE; j++) {
                x = MARGX + j*TAILLEX;
                y = (MARGY + i*TAILLEY);
                
                // Déplacement vertical aléatoire des éléments 
                if (Parametres.getParametre("deplacementVert").equals("allumé"))
                    y += rand.nextInt(50)-25;             

                if (i%2 == 0)
                    grille[i][j].afficher(g, x+TAILLEX/2, y);
                else
                    grille[i][j].afficher(g, x, y);   
            }
        }
    }
}
