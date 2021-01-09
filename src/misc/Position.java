package misc;

import wargame.IConfig;
import terrains.Carte;
import terrains.Chemin;
import terrains.Noeud;
import java.util.ArrayList;

public class Position implements IConfig {
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
		return ((((pos.getX() == getX()-1 || pos.getX() == getX()+1) && (pos.getY() == getY() || pos.getY() == getY() + decalageY)) 
		|| (pos.getX() == getX() && (pos.getY() == getY() - 1 || pos.getY() == getY() + 1))));
	}
	/**
	 * @param pos Position
	 * @return int : distance vers la position pos
	 */
	public int distance(Position pos, Carte c){ // 
		Chemin chem = new Chemin(x, y, pos.getX(), pos.getY(), c);
		int cmp = 0;

		for (int i = 0; i < chem.getListeF().size(); i++) {
			cmp++;
		}

		return cmp;
	}

	
}
