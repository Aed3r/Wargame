package terrains;

import misc.Element;
import terrains.Obstacle.TypeObstacle;
import unites.Position;
import unites.Soldat;

public class Carte {
    Element[][] grille;
    final int i;
    final int j;
    final int nb_obstacle_max;

    public Carte () {
        i = 15;
        j = 30;
        nb_obstacle_max = 12;
        int cmp_obstacle = 0;

        for (int k = 0; k < i; k++) {
            for (int l = 0; l < j; l++) {
                if (cmp_obstacle == nb_obstacle_max) {}
                else {
                    if (alea(0, i*j) <= nb_obstacle_max) {
                        grille[k][l] = new Obstacle(TypeObstacle.ROCHER, new Position(k, l));
                        cmp_obstacle++;
                    }
                }
            }
        }      
    }

    public void mort(Soldat soldat){}

    public int alea (int min, int max) {
        int range = max - min + 1; 
        int alea = (int)(Math.random() * range) + min; 
  
        return alea;
    }

}
