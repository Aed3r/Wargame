package terrains;

import misc.Element;
import misc.Position;
import unites.Soldat;
import java.awt.*;

public class Carte implements wargame.IConfig {
    Element[][] grille = new Element[HAUTEUR_CARTE][LARGEUR_CARTE];

    public Carte () {

        for (int i = 0; i < HAUTEUR_CARTE; i++) {
            for (int j = 0; j < HAUTEUR_CARTE; j++) {
                Position P = new Position (i, j);
                Element E = new Element(P);
                grille[i][j] = E;
                System.out.printf("test \n");
                grille[i][j] = new Element (P);
                
            }
        }      
    }

    public void mort(Soldat soldat){}

    public void affiche_nul () {
        for (int i = 0; i < LARGEUR_CARTE; i++) {
            for (int j = 0; j < HAUTEUR_CARTE; j++) {
                System.out.printf("%d ", grille[i][j]);
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
                y = MARGY + i*TAILLEY;

                if (i%2 == 0)
                    grille[i][j].afficher(g, x+TAILLEX/2, y);
                else
                    grille[i][j].afficher(g, x, y);   
            }
        }
    }
}
