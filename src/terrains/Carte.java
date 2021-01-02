package terrains;

import misc.Element;
import terrains.Obstacle.TypeObstacle;
import misc.Position;
import unites.Soldat;
import java.awt.*;

public class Carte implements wargame.IConfig {
    Element[][] grille;
    final int i;
    final int j;
    final int nb_obstacle_max;

    public Carte () {
        // utilise LARGEUR_CARTE et HAUTEUR_CARTE
        i = 15;
        j = 30;
        nb_obstacle_max = 12;
        int cmp_obstacle = 0;

        for (int i = 0; i < LARGEUR_CARTE; i++) {
            for (int j = 0; j < HAUTEUR_CARTE; j++) {
                if (cmp_obstacle == nb_obstacle_max) {}
                else {
                    if (alea(0, i*j) <= nb_obstacle_max) {
                        
                        grille[k][l] = new Obstacle(TypeObstacle.ROCHER, new Position(k, l));
                        System.out.println("blablza\n");
                        cmp_obstacle++;
                    }
                }
            }
        }      
    }

    public void mort(Soldat soldat){}

    // plus besoin
    public int alea (int min, int max) {
        int range = max - min + 1; 
        int alea = (int)(Math.random() * range) + min; 
  
        return alea;
    }

    /**
     * Affiche sur g tous les éléments constituant la carte courante
     * @param g un object graphique quelconque
     */
    public void afficher (Graphics g) {
        int i, j, x, y;

        // lignes
        for (i = 0; i < LARGEUR_CARTE; i++) {
            // colonnes
            for (j = 0; j < HAUTEUR_CARTE; j++) {
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
