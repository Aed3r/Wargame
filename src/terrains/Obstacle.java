package terrains;

import java.awt.*;

import misc.Element;
import misc.Position;

public class Obstacle extends Element {
	public enum TypeObstacle {
		ROCHER (Color.BLACK), FORET (Color.GREEN), EAU (Color.BLUE);
		private final Color COULEUR;
		TypeObstacle(Color couleur) { COULEUR = couleur; }
		public static TypeObstacle getObstacleAlea() {
			return values()[(int)(Math.random()*values().length)];
		}
		public Color getColor () { return COULEUR; }
		public String getNom () {
			switch (this) {
			case ROCHER: return "Rocher";
			case FORET: return "Foret";
			case EAU: return "Eau";
			default: return "";
			}
		}
	}
	private TypeObstacle TYPE;
	Obstacle(TypeObstacle type, Position pos) { 
		super(pos, type.getColor(), type.getNom());
		TYPE = type; 
	}
	public String toString() { return ""+TYPE; }
	
	@Override
	public void afficher() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void supprimer() {
		// TODO Auto-generated method stub
		
	}
}
