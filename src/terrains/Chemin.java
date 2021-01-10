package terrains;

import java.util.ArrayList;
import misc.Position; 


public class Chemin {
    ArrayList<Noeud> liste_o  = new ArrayList<Noeud>(); //Liste ouverte pour connaître les noeuds que l'on a déjà rencontré
    ArrayList<Noeud> liste_f  = new ArrayList<Noeud>(); //Liste fermée pour composer le chemin
    private Position arrivee = new Position (0, 0); //Position d'arrivée

    /**
     * Trouve un chemin entre deux points sur la carte
     * @return Retourne un chemin allant de Depart à Arrivée (passés en paramètres) sur la Carte
     * @param xDepart Ordonée de départ (Hauteur)
     * @param yDepart Abscisse de départ (Largeur)
     * @param xArrivee Ordonée d'arrivée (Hauteur)
     * @param yArrivee Abscisse d'arrivée (Largeur)
     * @param c Carte de jeu actuelle
     */
    public Chemin (int xDepart, int yDepart, int xArrivee, int yArrivee, Carte c) {
        /* Initialisations */
        arrivee.setX(xArrivee);
        arrivee.setY(yArrivee);
        int best;
        boolean a = true;
        Noeud courant = new Noeud();
        Noeud depart = new Noeud();
        courant.setPos(xDepart, yDepart);
        courant.setCoutG(0);
        courant.setCoutH(distance(xDepart, yDepart, xArrivee, yArrivee));
        courant.setCoutF(courant.getcoutG()+courant.getcoutH());

        copieNoeud(courant, depart);
 
        liste_o.add(depart);
        liste_f.add(depart);
    
        int courantX = courant.getXpos(); int courantY = courant.getYpos();
        
        /* Parcours de tous les voisins du noeud courant et sélection du meilleur */
        while (a) {
            Noeud test = new Noeud();
            ajout_voisin(courant, c); // on ajoute les voisins dans la liste ouverte
            best = meilleur_noeud(liste_o); // on sélectionne le meilleur noeud de la liste ouverte
            
            copieNoeud(liste_o.get(best), courant); // le meilleur noeud est copié dans courant
            copieNoeud(liste_o.get(best), test); // le meilleur noeud est copié dans test

            liste_f.add(test); // on ajoute le meilleur noeud dans la liste fermée
            liste_o.remove(best); // on enlève le meilleur noeud de la liste ouverte

            /* Récupération des nouvelles coordonées du noeud courant */
            courantX = courant.getXpos();
            courantY = courant.getYpos();

            /* Si courant correspond au noeud d'arrivée ou si on a parcouru toute la carte on peut sortir */
            if (courant.getXpos() == arrivee.getX() && courant.getYpos() == arrivee.getY()) a = false;
            if (liste_o.isEmpty()) a =false;
        }
        
        /* On affiche les informations  */
        if (liste_o.isEmpty()) System.out.println("vide \n");
        if (courant.getXpos() == arrivee.getX() && courant.getYpos() == arrivee.getY()) {
            //afficher_liste(liste_f);
        }

        else {
            liste_f.clear();
        }
    }

    /**
     * Fonction pour obtenir la liste fermée
     * @return Liste de noeud
     */
    public ArrayList<Noeud> getListeF () {
        return liste_f;
    }

    /**
     * calcule la distance euclidienne entre les points (x1,y1) et (x2,y2)
     * @return La distance entre les points en paramètres
     * @param x1
     * @param y2
     * @param x2
     * @param y2
     */
    public float distance(int x1, int y1, int x2, int y2){
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Fonction qui regarde si un noeud est dans une liste
     * @param n Noeud 
     * @param liste Liste de noeuds
     * @return True si le noeud est dans liste, false sinon
     */
    public boolean est_dans_liste (Noeud n, ArrayList<Noeud> liste) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getXpos() == n.getXpos() && liste.get(i).getYpos() == n.getYpos()) return true;
        }
        return false;
    }

    /**
     * Renvoie l'indice dans la liste auquel correspond la position
     * @param p Position
     * @param liste Liste de noeuds
     * @return l'indice que l'on recherche
     */
    public int retourne_indice (Position p, ArrayList<Noeud> liste) {
        for (int i = 0; i < liste.size(); i++) {
            if (liste.get(i).getXpos() == p.getX() && liste.get(i).getYpos() == p.getY()) return i;
        }
        return -1;
    }

    /**
     * Remplace un noeud situé à un indice dans une liste de noeuds par un noeud passé en paramètres
     * @param n Nouveau noeud
     * @param indice Indice dans la liste qui va être remplacé
     * @param liste Liste de noeuds
     */
    public void remplacement (Noeud n, int indice, ArrayList<Noeud> liste) {
        liste.get(indice).setCoutF(n.getcoutF());
        liste.get(indice).setCoutH(n.getcoutH());
        liste.get(indice).setCoutG(n.getcoutG());
        liste.get(indice).setPere(n.getPere());
        liste.get(indice).setPosPere(n.getXposPere(), n.getYposPere());
        liste.get(indice).setPos(n.getXpos(), n.getYpos());
    }

    /**
     * Copie un noeud dans un autre noeud
     * @param n Noeud à copier
     * @param i Noeud dans lequel on copie
     */
    public void copieNoeud (Noeud n, Noeud i) {
        i.setCoutF(n.getcoutF());
        i.setCoutH(n.getcoutH());
        i.setCoutG(n.getcoutG());
        i.setPere(n.getPere());
        i.setPosPere(n.getXposPere(), n.getYposPere());
        i.setPos(n.getXpos(), n.getYpos());
    }

    /**
     * Renvoie l'indice du meilleur noeud de la liste
     * @param liste Liste de noeuds
     * @return Indice du meilleur noeud
     */
    public int meilleur_noeud (ArrayList<Noeud> liste) {
        int min = 1;
        for (int i = 1; i < liste.size(); i++) {
            if (liste.get(i).getcoutF() < liste.get(min).getcoutF()) min = i;
        }

        return min;
    }

    /**
     * Affichage basique de la position de chaque noeud d'une liste
     * @param liste Liste de noeuds
     */
    public void afficher_liste (ArrayList<Noeud> liste) {
        for (int i = 0; i < liste.size(); i++) {
            System.out.printf("Noeud (%d;%d)", liste.get(i).getXpos(), liste.get(i).getYpos());
        }
        System.out.printf("\n");
    }

    /**
     * Permet de visiter tous les noeuds voisins dans le graphe et de les ajouter dans les listes
     * @param n Noeud de départ
     * @param c Carte actuelle du jeu
     */
    public void ajout_voisin (Noeud n, Carte c) {
        /* Initialisations */
        int posI = n.getXpos();
        int posJ = n.getYpos();
        int indice_last_good = 0;


        /*Tous les noeuds adjacents */
        for (int i = posI-1; i < posI+2; i++) {
            for (int j = posJ-1; j < posJ+2; j++) {
                if ((i == posI && j == posJ) || (i == posI-1 && j == posJ+1) || (i == posI+1 && j == posJ+1)) {
                    /* On vérifie ici que l'on ne prend pas en compte la positions de départ elle même
                       ou les positions qui ne sont pas accessibles dans le cas d'une grille hexagonale */
                }
                else {
                    /* On est dans le cas d'un noeud adjacent */
                    if (c.grille[i][j].estAccessible()) {
                        /* Le noeud que l'on visite est accessible */
                        Noeud tmp = new Noeud();
                        tmp.setPos(i, j);

                        /* Si il n'est pas dans la liste fermé */
                        if (!est_dans_liste(tmp, liste_f)) {

                            indice_last_good = liste_f.size()-1;
                            /* On donne les valeurs du noeud en cours de visite au noeud tmp */
                            tmp.setPos(i, j);
                            tmp.setCoutG(distance(liste_f.get(indice_last_good).getXpos(), liste_f.get(indice_last_good).getYpos(), i, j));
                            tmp.setCoutH(distance(i, j, arrivee.getX(), arrivee.getY()));
                            tmp.setCoutF(tmp.getcoutG()+tmp.getcoutH());
                            tmp.setPere(n.getPere()+1); 
                            tmp.setPosPere(n.getXpos(), n.getYpos());

                            /* Si il est dans la liste ouverte */
                            if (est_dans_liste(tmp, liste_o)) {
                                /* Récupération de son indice dans la liste ouverte */
                                int indice_new_node = retourne_indice(new Position(tmp.getXpos(), tmp.getYpos()), liste_o);
                                /* Si son coût est plus faible qu'auparavant on le remplace */
                                if (tmp.getcoutF() < (liste_o.get(indice_new_node).getcoutF())) {
                                    liste_o.set(indice_new_node, tmp);
                                }
                            }

                            /* Sinon on l'ajoute dans la liste ouverte */
                            else {
                                liste_o.add(tmp);
                            }
                        }
                    }
                }
            }
        }
    }
}
