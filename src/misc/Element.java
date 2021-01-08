package misc;

import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import unites.Heros;
import unites.Soldat;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import terrains.*;

/**
 * Représente une des tuile du plateau de jeu
 */
public class Element implements wargame.IConfig {
	private final TypeElement type;
	private final Position pos;
	private Soldat soldat = null; // le soldat se trouvant sur la case
	private boolean estVisible = false; // si un héros peux voir cette case
	private transient BufferedImage sprite = null, spriteSombre = null, buffer = null;
	private boolean reafficher = false; // Indique s'il faut redessiner l'élément
	private boolean reafficherDessus = false; // Indique s'il faut réafficher les éléments au dessus de l'élément courant
	private int drawX, drawY; // Coordonnées d'affichage de l'élément
	private int deplacementYSoldat = 0; // Depassement du soldat au dessus d'un élément

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
			
			// Déplacement vertical aléatoire des éléments 
			String param = Parametres.getParametre("deplacementVert");
			if (param != null && param.equals("allumé"))
				this.DEPLACEMENTVERT = deplacementVert + (int) (Math.random()*50-25);  	  
			else
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

	/**
	 * Crée un élément aléatoire
	 * @param pos la position de l'élément sur la carte
	 * @see TypeElement#getElementAlea
	 */
	public Element (Position pos) {
		this(TypeElement.getElementAlea(), pos);
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

		// Chargement de l'image normal
		try {
            sprite = ImageIO.read(getClass().getResourceAsStream("/img/elements/" + getNom() + ".png"));
        } catch (IOException e) {
            // Problème lors du chargement, on utilise rien
			System.out.println(e.getLocalizedMessage());
		}
		
		// Création de l'image sombre
		int w = sprite.getWidth(null), h = sprite.getHeight(null);
		BufferedImage sSombre = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = sSombre.getGraphics();
		g.drawImage(sprite, 0, 0, null);
		RescaleOp op = new RescaleOp(ALPHAELEMCACHE, 0, null);
		sSombre = op.filter(sSombre, null);
		spriteSombre = sSombre;
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
	public BufferedImage getSprite () {
        return sprite;
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


	public boolean estHeros () {
		if (getSoldat() == null) return false;
		return getSoldat() instanceof Heros;
	}

	/**
	 * Seul un soldat à la fois 
	 * @param soldat un soldat quelconque
	 */
	public void setSoldat(Soldat soldat) {
		if (this.soldat == null) {
			this.soldat = soldat;
			setReafficher(true);
		}
	}

	/**
	 * @return true si l'élément est visible, false sinon
	 */
	public boolean getVisible () {
		return estVisible;
	}

	/**
	 * Indique que cet élément et son contenu est visible par le joueur
	 */
	public void setVisible () {
		estVisible = true;
		setReafficher(true);
	}

	/**
	 * Indique que cet élément et son contenu ne sont pas visible par le joueur (case grisée)
	 */
	public void setCache () {
		estVisible = false;
		setReafficher(true);
	}

	/**
	 * Indique s'il faut réafficher l'élément ou non
	 * @param val true s'il faut réafficher, false sinon
	 */
	public void setReafficher (boolean val) {
		reafficher = val;
	}

	/**
	 * @return true s'il faut réafficher l'élément, false sinon
	 */
	public boolean getReafficher () {
		return reafficher;
	}

	/**
	 * Affiche cet élément sur l'objet graphique g à la position (x, y)
	 * @param g un objet graphique
	 * @param x l'abscisse
	 * @param y l'ordonnée
	 * @param tabHitbox le tableau des hitbox de la carte
	 */
	public void afficher (Graphics g, int x, int y, byte[][][] tabHitbox) {
		drawX = x;
		drawY = y;
		
		if (estVisible) g.drawImage(getSprite(), x, y+type.DEPLACEMENTVERT, null);
		else g.drawImage(spriteSombre, x, y+type.DEPLACEMENTVERT, null);

		afficherSoldat(g, x, y);

		// Mise à jour du tableau des hitbox et buffer
		RunnableAfficher r = new RunnableAfficher(tabHitbox);
		Thread t = new Thread(r);
		t.start();

		reafficher = false;
	}

	/**
	 * Permet d'effectuer des opérations de dessin longue et pas immédiatement nécessaires.
	 * Calcule les nouvelles hitbox et met à jour le buffer de l'élément.
	 */
	public class RunnableAfficher implements Runnable {
		private byte[][][] tabHitbox;
		
		/**
		 * Initialise le runnable avec les paramètres voulu
		 * @param tabHitbox le tableau des hitbox
		 */
		public RunnableAfficher(byte[][][] tabHitbox) {
			this.tabHitbox = tabHitbox;
		}
	
		public void run() {
			int w = getSprite().getWidth(null), h = getSprite().getHeight(null)-127, alpha;

			// Mise à jour du buffer
			if (getSoldat() != null)
				deplacementYSoldat = getSoldat().getSprite().getHeight() - (TAILLEY/2-type.DEPLACEMENTVERT);
			else deplacementYSoldat = 0;
			
			if (deplacementYSoldat < 0) deplacementYSoldat = 0;
			else h += deplacementYSoldat;

			if (buffer != null) buffer.flush();

			buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = buffer.getGraphics();

			if (estVisible) g.drawImage(getSprite(), 0, deplacementYSoldat, null);
			else g.drawImage(spriteSombre, 0, deplacementYSoldat, null);

			afficherSoldat(buffer.getGraphics(), 0, -type.DEPLACEMENTVERT+deplacementYSoldat);

			// Calcul des hitbox
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					alpha = (buffer.getRGB(j, i)>>24)&0xff;

					if (alpha == 255) {
						tabHitbox[i+drawY+type.DEPLACEMENTVERT-deplacementYSoldat][j+drawX][0] = (byte) getPos().getX();
						tabHitbox[i+drawY+type.DEPLACEMENTVERT-deplacementYSoldat][j+drawX][1] = (byte) getPos().getY();
					}
				}
			}

			
		}
	}

	/**
	 * Redessine le quart de l'élément indiqué en utilisant le buffer.
	 * Met également à jour les hitbox
	 * @param g l'objet graphique sur lequel dessiner
	 * @param tabHitbox le tableau des hitbox
	 * @param cote le côté à dessiné <p>
	 * 		- 0: Nord-ouest </p><p>
	 * 		- 1: Nord-est </p><p>
	 * 		- 2: Sud-est </p><p>
	 * 		- 3: Sud-ouest </p>
	 */
	protected void dessinerQuart (Graphics g, byte[][][] tabHitbox, int cote) {
		int w = buffer.getWidth(), h = buffer.getHeight(), halfW = w/2,
			x = getPos().getX(), y = getPos().getY(), dY = type.DEPLACEMENTVERT-deplacementYSoldat;

		switch (cote) {
			case 0: // Nord-ouest
				g.drawImage(buffer, drawX, drawY+dY, 
							drawX+halfW+1, drawY,
							0, 0, halfW+1, -dY, null);

				// On recalcule la hitbox
				SwingUtilities.invokeLater(() -> {
					for (int i = drawY + dY; i < drawY; i++) {
						for (int j = drawX; j < drawX + halfW + 1; j++) {
							if (((buffer.getRGB(j - drawX, i - (drawY + dY)) >> 24) & 0xff) == 255) {
								tabHitbox[i][j][0] = (byte) x;
								tabHitbox[i][j][1] = (byte) y;
							}
						}
					}
				});
				break;
			case 1: // Nord-Est
				g.drawImage(buffer, drawX+halfW, drawY+dY, 
							drawX+w, drawY,
							halfW, 0, w, -dY, null);
				
				// On recalcule la hitbox
				SwingUtilities.invokeLater(() -> {
					for (int i = drawY+dY; i < drawY; i++) {
						for (int j = drawX+halfW; j < drawX+w; j++) {
							if (((buffer.getRGB(j-drawX, i-(drawY+dY))>>24)&0xff) == 255) {
								tabHitbox[i][j][0] = (byte) x;
								tabHitbox[i][j][1] = (byte) y;
							}
						}
					}
				});
				break;
			case 2: // Sud-est
				g.drawImage(buffer, drawX+halfW, drawY, 
							drawX+w, drawY+dY+h,
							halfW, -dY, w, h, null);
				// On recalcule la hitbox
				for (int i = drawY; i < drawY+dY+h; i++) {
					for (int j = drawX+halfW; j < drawX+w; j++) {
						tabHitbox[i][j][0] = (byte) x;
						tabHitbox[i][j][1] = (byte) y;
					}
				}
				break;
			case 3: // Sud-ouest
				g.drawImage(buffer, drawX, drawY, 
							drawX+halfW, drawY+dY+h,
							0, -dY, halfW, h, null);
				// On recalcule la hitbox
				for (int i = drawY; i < drawY+dY+h; i++) {
					for (int j = drawX; j < drawX+halfW; j++) {
						tabHitbox[i][j][0] = (byte) x;
						tabHitbox[i][j][1] = (byte) y;
					}
				}
				break;
			default:
				System.err.println("Cote invalide!");
				return;
		}
	}

	/**
	 * Réaffiche cet élément sur l'objet graphique g
	 * @param g un objet graphique
	 * @param tabHitbox le tableau des hitbox de la carte
	 * @param carte la carte sur laquelle se trouve l'élément
	 */
	public void reafficher (Graphics g, byte[][][] tabHitbox, Carte carte) {
		int deplacementOuest = 0, deplacementEst = 0;
		Element tmp;

		// La position des éléments au dessus et en dessous dépend de la position de l'élément courant
		if (getPos().getX() % 2 == 0) deplacementEst = 1;
		else deplacementOuest = -1;

		// On réaffiche les parties basses des éléments au dessus de l'élément courant
		if (reafficherDessus) {
			tmp = carte.getElement(getPos().getX()-1, getPos().getY()+deplacementOuest); // Element nord-ouest
			if (tmp != null) tmp.dessinerQuart(g, tabHitbox, 2); 
			tmp = carte.getElement(getPos().getX()-1, getPos().getY()+deplacementEst); // Element nord-est
			if (tmp != null) tmp.dessinerQuart(g, tabHitbox, 3); 
			reafficherDessus = false;
		}

		// On réaffiche l'élément courant
		if (estVisible) 
			g.drawImage(getSprite(), drawX, drawY+type.DEPLACEMENTVERT, drawX+getSprite().getWidth(), drawY+190, 0, 0, getSprite().getWidth(), -type.DEPLACEMENTVERT+190, null);
		else g.drawImage(spriteSombre, drawX, drawY+type.DEPLACEMENTVERT, drawX+spriteSombre.getWidth(), drawY+190, 0, 0, spriteSombre.getWidth(), -type.DEPLACEMENTVERT+190, null);

		afficherSoldat(g, drawX, drawY);

		// On recalcule la hitbox
		RunnableAfficher r = new RunnableAfficher(tabHitbox);
		Thread t = new Thread(r);
		t.start();

		// On réaffiche les parties hautes des élément au dessous de l'élément courant
		tmp = carte.getElement(getPos().getX()+1, getPos().getY()+deplacementOuest); // Element sud-ouest
		if (tmp != null) tmp.dessinerQuart(g, tabHitbox, 1); 
		tmp = carte.getElement(getPos().getX()+1, getPos().getY()+deplacementEst); // Element sud-est
		if (tmp != null) tmp.dessinerQuart(g, tabHitbox, 0); 

		setReafficher(false);
	}

	private void afficherSoldat (Graphics g, int dX, int dY) {
		Soldat s = getSoldat();

		if (s != null) {
			BufferedImage img = s.getSprite();
			g.drawImage(img, dX+(TAILLEX-img.getWidth())/2, dY+TAILLEY/2-img.getHeight(), null);
			reafficherDessus = true;
		}
	}
}
