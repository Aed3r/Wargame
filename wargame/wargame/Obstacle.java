package wargame;
import java.awt.Color;
import java.awt.Graphics;
public class Obstacle extends Element {
	Position pos;
	public enum TypeObstacle {
		ROCHER (Color.BLACK), FORET (Color.GREEN), EAU (Color.BLUE);
		private final Color COULEUR;
		TypeObstacle(Color couleur) { COULEUR = couleur; }
		public static TypeObstacle getObstacleAlea() {
			return values()[(int)(Math.random()*values().length)];
		}
	}
	private TypeObstacle TYPE;
	Obstacle(TypeObstacle type, Position pos) { TYPE = type; this.pos = pos; }
	public String toString() { return ""+TYPE; }
}