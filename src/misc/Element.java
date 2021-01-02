package misc;

import java.awt.*;
import javax.imageio.ImageIO;
import unites.Soldat;
import java.io.File;
import java.io.IOException;

public abstract class Element {
    public enum TypeElement {
		PLAINE ("plaine", 0.3f, true, true, 0f, -65), DESERT ("desert", 0.2f, true, true, 0.5f, -65), 
		EAU ("eau", 0.1f, false, true, 0f, -65), MONTAGNE ("montagne", 0.1f, false, false, 0f, -156),
		FORET ("foret", 0.2f, false, false, 0f, -72);

		public final String NOM;
		public final float PROBA;
		public final boolean ESTACCESSIBLE;
		public final boolean PEUXTIRER;
		public final float PDVPERDUES; // Point de vies perdues par tour
		public final int DEPLACEMENTVERT; // Déplacement Y lors de l'affichage du sprite correspondant

		private TypeElement(String nom, float probaApparition, boolean estAccessible,
					boolean peuxTirer, float pdvPerdues, int deplacementVert) { 
			this.NOM = nom;
			this.PROBA = probaApparition;
			this.ESTACCESSIBLE = estAccessible;
			this.PEUXTIRER = peuxTirer;
			this.PDVPERDUES = pdvPerdues;
			this.DEPLACEMENTVERT = deplacementVert;
		}

		public static TypeElement getElementAlea() {
			return values()[(int)(Math.random()*values().length)];
		}
	}
	private final TypeElement type;
	private final Position pos;
	private Soldat soldat = null;

	public Element (Position pos) {
		type = TypeElement.getElementAlea();
		this.pos = pos;
	}

	public Element (TypeElement type, Position pos) {
		this.type = type;
		this.pos = pos;
	}

	public String getNom () {
		return type.NOM;
	}

	public Image getSprite () {
        try {
            return ImageIO.read(new File("data/img/elements/" + type.NOM));
        } catch (IOException e) {
            // Problème lors du chargement, on utilise rien
            System.out.println(e.getLocalizedMessage());
            return null;
        }
	}

	public boolean estAccessible () {
		return type.ESTACCESSIBLE;
	}

	public boolean peuxTirer () {
		return type.PEUXTIRER;
	}

	public float getPDVPerdues () {
		return type.PDVPERDUES;
	}

	public int getDeplacementVert () {
		return type.DEPLACEMENTVERT;
	}

	public Position getPos() {
		return this.pos;
	}

	public Soldat getSoldat() {
		return this.soldat;
	}

	/**
	 * Seul un soldat à la fois 
	 * @param soldat un soldat quelconque
	 */
	public void setSoldat(Soldat soldat) {
		if (this.soldat == null) {
			this.soldat = soldat;
		}
	}
}
