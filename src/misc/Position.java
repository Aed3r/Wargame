package misc;

import wargame.IConfig;

public class Position implements IConfig {
	private int x, y;
	public Position(int x, int y) { this.x = x; this.y = y; }
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
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
		int decalageX = 1;
		if(this.getY() % 2 == 0) decalageX = -1;

		/*Une case est voisine si les deux sont des positions valide et qu'elles sont adjacentes*/
		return (this.estValide() && pos.estValide()) &&
		(((pos.getY() == getY()-1 || pos.getY() == getY()+1) && (pos.getX() == getX() || pos.getX() == getX() + decalageX)) 
		|| (pos.getY() == getY() && (pos.getX() == getX() - 1 || pos.getX() == getX() + 1)));
	}
	/**
	 * @param pos Position
	 * @return int : distance vers la position pos
	 */
	public int distance(Position pos){ // 
		/*TODO*/
		return 0;
	}

	
}
