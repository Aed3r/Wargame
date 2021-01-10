package misc;

import wargame.IConfig;
import java.io.Serializable;
import terrains.Carte;
import terrains.Chemin;

public class Position implements IConfig, Serializable {
	private static final long serialVersionUID = -539589512429181144L;
	private int x, y;
	public Position(int x, int y) { this.x = x; this.y = y; }
	public Position() {  }
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	/**
	 * @return true si la position est bien dans les limites de la carte, false sinon
	 */
	public boolean estValide() {
		return !((x >= HAUTEUR_CARTE) || (y >= LARGEUR_CARTE) || (x < 0) || (y < 0));
	}
	public String toString() { return "("+x+","+y+")"; }
	/**
	 * Détermine si la position pos est voisine de cette position
	 * @param pos position a tester
	 * @return true si elle est voisine, false sinon
	 */
	public boolean estVoisine(Position pos) {
		/*Puisque l'on travaille avec des hexagones il y a un decalage a prende en compte selon l'indice de la ligne*/
		int decalageY = -1;
		if(this.getX() % 2 == 0) decalageY = 1;

		/*Une case est voisine si les deux sont des positions valide et qu'elles sont adjacentes*/
		return (pos.estValide() && this.estValide() && (((pos.getX() == getX()-1 || pos.getX() == getX()+1) && (pos.getY() == getY() || pos.getY() == getY() + decalageY)) 
		|| (pos.getX() == getX() && (pos.getY() == getY() - 1 || pos.getY() == getY() + 1))));
	}
	/**
	 * Calcul la distance entre deux positions en calculant un chemin entre celles-ci
	 * @param pos Position jusqu'à laquelle on veut déterminer la distance
	 * @param c Carte actuelle 
	 * @return int : distance vers la position pos
	 */
	public int distance(Position pos, Carte c){ 
		/* Appel de la fonction Chemin qui utilise des listes pour stocker celui-ci*/
		Chemin chem = new Chemin(x, y, pos.getX(), pos.getY(), c);

		/* On retourne la longueur du chemin */
		return chem.getListeF().size();
	}

	
}
