package unites;

import wargame.IConfig;

public class Position implements IConfig {
	private int x, y;
	public Position(int x, int y) { this.x = x; this.y = y; }
	public int getX() { return x; }
	public int getY() { return y; }
	public void setX(int x) { this.x = x; }
	public void setY(int y) { this.y = y; }
	public boolean estValide() {
		return !(x<0 || x>=LARGEUR_CARTE || y<0 || y>=HAUTEUR_CARTE);
	}
	public String toString() { return "("+x+","+y+")"; }
	public boolean estVoisine(Position pos) {
		/*Une case est voisine si les deux sont des positions valide et qu'elles sont adjacentes*/
		return (this.estValide() && pos.estValide()) && ((y == pos.y && (x == pos.x - 1 || x == pos.x - 2 || x == pos.x + 1 || x == pos.x + 2)) || (y == pos.y -1 && (x == pos.x - 1 || x == pos.x + 1)));
	}
}
