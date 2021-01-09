package terrains;

import java.util.ArrayList;
import terrains.Noeud;
import misc.Position; 


public class Chemin {
    ArrayList<Noeud> liste_o  = new ArrayList<Noeud>();
    ArrayList<Noeud> liste_f  = new ArrayList<Noeud>();
    private Position arrivee = new Position (0, 0);

    public Chemin (int xDepart, int yDepart, int xArrivee, int yArrivee, Carte c) {
        //System.out.println("entrée constructeur \n");
        arrivee.setX(xArrivee);
        arrivee.setY(yArrivee);
        int best;

        //System.out.println("initialisations \n");
        Noeud courant = new Noeud();
        Noeud depart = new Noeud();
        System.out.printf("depart : %d %d \n", xDepart, yDepart);
        courant.setPos(xDepart, yDepart);
        courant.setCoutG(0);
        courant.setCoutH(distance(xDepart, yDepart, xArrivee, yArrivee));
        courant.setCoutF(courant.getcoutG()+courant.getcoutH());

        copieNoeud(courant, depart);

        System.out.printf("%s \n", courant.toString());
        System.out.printf("%s \n", depart.toString());

        
        liste_o.add(depart);
        liste_f.add(depart);
        //ajout_voisin(courant, c);
    
        //System.out.println("fin initialisations \n");
        int courantX = courant.getXpos(); int courantY = courant.getYpos();
        boolean a = true;
        while (a) {
            Noeud test = new Noeud();
            ajout_voisin(courant, c);
            best = meilleur_noeud(liste_o);
            
            copieNoeud(liste_o.get(best), courant); 
            copieNoeud(liste_o.get(best), test);

            liste_f.add(test);
            liste_o.remove(best);

            courantX = courant.getXpos();
            courantY = courant.getYpos();

            if (courant.getXpos() == arrivee.getX() && courant.getYpos() == arrivee.getY()) a = false;
            if (liste_o.isEmpty()) a =false;
        }
        
        System.out.println("fin boucle while \n");
        
        if (liste_o.isEmpty()) System.out.println("vide \n");
        if (courant.getXpos() == arrivee.getX() && courant.getYpos() == arrivee.getY()) {
            //chemin trouvé
            //liste_f.set(0, depart);
            afficher_liste(liste_f);
        }

        else {
            System.out.println("Pas de chemin \n");
        }

        System.out.println("Fin du constructeur \n");
    }

    public ArrayList<Noeud> getListeF () {
        return liste_f;
    }

    /* calcule la distance entre les points (x1,y1) et (x2,y2) */
    public float distance(int x1, int y1, int x2, int y2){
        /* distance euclidienne */
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public boolean est_dans_liste (Noeud n, ArrayList<Noeud> liste) {
        for (int i = 0; i < liste.size(); i++) {
            System.out.printf("xL : %d yL : %d || xN: %d yN: %d\n", liste.get(i).getXpos(), liste.get(i).getYpos(), n.getXpos(), n.getYpos());
            if (liste.get(i).getXpos() == n.getXpos() && liste.get(i).getYpos() == n.getYpos()) return true;
        }
        return false;
    }

    public int retourne_indice (Position p, ArrayList<Noeud> liste) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getXpos() == p.getX() && liste.get(i).getYpos() == p.getY()) return i;
        }
        //System.exit(-1);
        return -1;
    }

    public void remplacement (Noeud n, int indice, ArrayList<Noeud> liste) {
        liste.get(indice).setCoutF(n.getcoutF());
        liste.get(indice).setCoutH(n.getcoutH());
        liste.get(indice).setCoutG(n.getcoutG());
        liste.get(indice).setPere(n.getPere());
        liste.get(indice).setPosPere(n.getXposPere(), n.getYposPere());
        liste.get(indice).setPos(n.getXpos(), n.getYpos());
    }

    public void copieNoeud (Noeud n, Noeud i) {
        i.setCoutF(n.getcoutF());
        i.setCoutH(n.getcoutH());
        i.setCoutG(n.getcoutG());
        i.setPere(n.getPere());
        i.setPosPere(n.getXposPere(), n.getYposPere());
        i.setPos(n.getXpos(), n.getYpos());
    }

    public int meilleur_noeud (ArrayList<Noeud> liste) {
        int min = 1;
        for (int i = 1; i < liste.size(); i++) {
            if (liste.get(i).getcoutF() < liste.get(min).getcoutF()) min = i;
        }

        return min;
    }

    public void ajout_lf (int indice, Noeud n) {
        liste_f.add(n);
        liste_o.remove(indice);
    }

    public void afficher_liste (ArrayList<Noeud> liste) {
        for (int i = 0; i < liste.size(); i++) {
            System.out.printf("Noeud (%d;%d)", liste.get(i).getXpos(), liste.get(i).getYpos());
        }
        System.out.printf("\n");
    }

    public void ajout_voisin (Noeud n, Carte c) {
        //System.out.println("Entrée dans ajout voisin \n");
        int posI = n.getXpos();
        int posJ = n.getYpos();
        int indice_last_good = 0;
        //Noeud tmp = new Noeud ();


        //System.out.printf(": %d %d \n", posI, posJ);

        /*Tous les noeuds adjacents */
        for (int i = posI-1; i < posI+2; i++) {
            for (int j = posJ-1; j < posJ+2; j++) {
                if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                    //System.out.printf("Cas non valable : %d %d \n", i, j);
                }
                else {
                    //System.out.printf("Cas valable : %d %d \n", i, j);
                    if (c.grille[i][j].estAccessible()) {
                        //System.out.printf("Accessible (%d, %d) \n", i, j);
                        Noeud tmp = new Noeud();
                        tmp.setPos(i, j);

                        System.out.printf("Nouveau noeud : %d %d \n", tmp.getXpos(), tmp.getYpos());
                        System.out.println("liste fermée");
                        if (!est_dans_liste(tmp, liste_f)) {
                            //System.out.println("Pas dans liste f \n");
                            indice_last_good = liste_f.size()-1;
                            tmp.setPos(i, j);
                            tmp.setCoutG(distance(liste_f.get(indice_last_good).getXpos(), liste_f.get(indice_last_good).getYpos(), i, j));
                            //System.out.printf("liste f : %s \n", liste_f.get(0).toString());
                            //System.out.printf("Distance tmp -> arrivée : %f \n", distance(i, j, arrivee.getX(), arrivee.getY()));
                            //System.out.printf("Cout G de temp : %f \n", tmp.getcoutG());
                            tmp.setCoutH(distance(i, j, arrivee.getX(), arrivee.getY()));
                            tmp.setCoutF(tmp.getcoutG()+tmp.getcoutH());
                            tmp.setPere(n.getPere()+1); //pas sur du tt
                            tmp.setPosPere(n.getXpos(), n.getYpos());
                            //System.out.printf("tmp : %s \n", tmp.toString());
                            //afficher_liste(liste_o);
                            //System.out.println("FIN O : \n");

                            System.out.println("liste ouverte");
                            if (est_dans_liste(tmp, liste_o)) {
                                //System.out.printf("Je suis dans la liste (%d;%d) \n", tmp.getXpos(), tmp.getYpos());
                                //afficher_liste(liste_o);
                                int indice_new_node = retourne_indice(new Position(tmp.getXpos(), tmp.getYpos()), liste_o);
                                //System.out.printf("indice  : %d \n", indice_new_node);
                                if (tmp.getcoutF() < (liste_o.get(indice_new_node).getcoutF())) {
                                    //remplacement(tmp, indice_new_node, liste_o);
                                    liste_o.set(indice_new_node, tmp);
                                }
                            }

                            else {
                                //System.out.println("Ajout \n");
                                liste_o.add(tmp);
                                //afficher_liste(liste_o);
                            }
                        }
                    }
                }
            }
        }
        //System.exit(0);
        //System.out.println("Sortie dans ajout voisin \n");
    }

    public void trouver_chemin () {
        /* Noeud tmp = new Noeud();
        copieNoeud(n, i); */
    }
}
