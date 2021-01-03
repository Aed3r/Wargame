package misc;

import java.awt.*;
import javax.imageio.ImageIO;
import unites.Soldat;
import java.io.File;
import java.io.IOException;

/**
 * Représente une des tuile du plateau de jeu
 */
public class Element {
	/**
	 * Représente un des différents types d'éléments prédéfinies
	 */
    public enum TypeElement {
		PLAINE ("plaine", 0.6f, true, true, 0f, -65), DESERT ("desert", 0.2f, true, true, 0.5f, -67), 
		EAU ("eau", 0.025f, false, true, 0f, -65), MONTAGNE ("montagne", 0.025f, false, false, 0f, -156),
		FORET ("foret", 0.05f, false, false, 0f, -72);

		final String NOM;
		final float PROBA;
		final boolean ESTACCESSIBLE;
		final boolean PEUXTIRER;
		final float PDVPERDUES;
		final int DEPLACEMENTVERT;

		/**
		 * Crée les éléments définies au dessus
		 * @param nom le nom de l'élément (également utilisé pour récupérer l'image)
		 * @param probaApparition la probabilité d'apparition de l'élément
		 * @param estAccessible si une unité peuvent être déplacé sur l'élément ou non
		 * @param peuxTirer si les unités peuvent tirer des projectiles au travers de l'élément
		 * @param pdvPerdues le nombre de point de vies perdus à chaque tour par une unité se trouvant sur cet élément
		 * @param deplacementVert déplacement vertical à effectuer pour afficher l'image de l'élément
		 */
		private TypeElement(String nom, float probaApparition, boolean estAccessible,
					boolean peuxTirer, float pdvPerdues, int deplacementVert) { 
			this.NOM = nom;
			this.PROBA = probaApparition;
			this.ESTACCESSIBLE = estAccessible;
			this.PEUXTIRER = peuxTirer;
			this.PDVPERDUES = pdvPerdues;
			this.DEPLACEMENTVERT = deplacementVert;
		}

		/**
		 * @return un élément aléatoire, choisit selon leurs probabilités prédéfinies
		 */
		public static TypeElement getElementAlea() {
			double valRand = Math.random(), valElem = 0;
			int i;

			for (i = 0; i < values().length && valElem < valRand; i++)
				valElem += values()[i].PROBA;

			return values()[i-1];
		}
	}

	private final TypeElement type;
	private final Position pos;
	private Soldat soldat = null;

	/**
	 * Crée un élément aléatoire
	 * @param pos la position de l'élément sur la carte
	 * @see TypeElement#getElementAlea
	 */
	public Element (Position pos) {
		type = TypeElement.getElementAlea();
		this.pos = pos;
	}

	/**
	 * Crée l'élément indiqué par type
	 * @param type un des types définies dans TypeElement
	 * @param pos la position de l'élément sur la carte
	 * @see TypeElement
	 */
	public Element (TypeElement type, Position pos) {
		this.type = type;
		this.pos = pos;
	}

	/**
	 * @return le nom de l'élément (également utilisé pour récupérer l'image)
	 */
	public String getNom () {
		return type.NOM;
	}

	/**
	 * @return l'image de l'élément
	 */
	public Image getSprite () {
        try {
            return ImageIO.read(new File("data/img/elements/" + getNom() + ".png"));
        } catch (IOException e) {
            // Problème lors du chargement, on utilise rien
            System.out.println(e.getLocalizedMessage());
            return null;
        }
	}

	/**
	 * @return true si une unité peuvent être déplacé sur l'élément ou non, false sinon
	 */
	public boolean estAccessible () {
		return type.ESTACCESSIBLE;
	}

	/**
	 * @return true si les unités peuvent tirer des projectiles au travers de l'élément, false sinon
	 */
	public boolean peuxTirer () {
		return type.PEUXTIRER;
	}

	/**
	 * @return le nombre de point de vies perdus à chaque tour par une unité se trouvant sur cet élément
	 */
	public float getPDVPerdues () {
		return type.PDVPERDUES;
	}

	/**
	 * @return la position de l'élément sur la carte
	 */
	public Position getPos() {
		return this.pos;
	}

	/**
	 * @return le soldat stationné sur l'élément (s'il y en a un), null sinon
	 */
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

	/**
	 * Affiche cet élément sur l'objet graphique g à la position (x, y)
	 * @param g un objet graphique
	 * @param x l'abscisse
	 * @param y l'ordonnée
	 */
	public void afficher (Graphics g, int x, int y) {
		g.drawImage(getSprite(), x, y+type.DEPLACEMENTVERT, null);
	}
}
