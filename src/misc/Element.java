package misc;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import unites.Soldat;
import java.io.IOException;
import java.util.LinkedList;
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
	private boolean reafficherDessus = false; // Indique s'il faut réafficher les éléments au dessus de l'élément courant
	private int drawX, drawY; // Coordonnées d'affichage de l'élément
	private int deplacementYSoldat = 0; // Depassement du soldat au dessus d'un élément
	private int nbSoldatVoient = 0; // Le nombre de soldats qui voient l'élément
	private static LinkedList<Element> fileElem = new LinkedList<>(); // File des éléments à raffraichir
	private boolean jamaisAffiche = true; 
	private final boolean perf; // Si le mode performance est allumé ou non
	private final float multTaille; // Le multiplicateur des tailles d'images
	private final int tailleX, tailleY; // La taille des sprite

	/**
	 * Représente un des différents types d'éléments prédéfinies
	 */
    public enum TypeElement {
		PLAINE ("plaine", 0.6f, true, true, 0, -65), DESERT ("desert", 0.2f, true, true, 5, -95), 
		EAU ("eau", 0.025f, false, true, 0, -60), MONTAGNE ("montagne", 0.025f, false, false, 0, -195),
		FORET ("foret", 0.05f, false, false, 0, -106);

		final String NOM;
		final float PROBA;
		final boolean ESTACCESSIBLE;
		final boolean PEUXTIRER;
		final int PDVPERDUES;
		final float DEPLACEMENTVERT;
		int deplacementY;

		/**
		 * Crée les éléments définies au dessus
		 * @param nom le nom de l'élément (également utilisé pour récupérer l'image)
		 * @param probaApparition la probabilité d'apparition de l'élément
		 * @param estAccessible si une unité peuvent être déplacé sur l'élément ou non
		 * @param peuxTirer si les unités peuvent tirer des projectiles au travers de l'élément
		 * @param pdvPerdues le nombre de point de vies perdus à chaque tour par une unité se trouvant sur cet élément
		 * @param deplacementY déplacement vertical à effectuer pour afficher l'image de l'élément
		 */
		private TypeElement(String nom, float probaApparition, boolean estAccessible,
					boolean peuxTirer, int pdvPerdues, int deplacementVert) 
		{ 
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

		/**
		 * Charge le deplacement des types en fonction du mode performance<br>
		 * Utile lorsque le mode est éteind/allumé d'une partie à l'autre
		 */
		public static void setDeplacement(float multTaille) {
			for (TypeElement te : values()) {
				String param = Parametres.getParametre("deplacementVert");
				if (param != null && param.equals("allumé"))
					te.deplacementY = (int) ((te.DEPLACEMENTVERT + (int) (Math.random()*50-25)) * multTaille);  	  
				else
					te.deplacementY = (int) (te.DEPLACEMENTVERT * multTaille);
			}
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
		int w, h;

		this.type = type;
		this.pos = pos;

		// Mode Performance
		String val = Parametres.getParametre("modePerf");
        if (val != null) perf = val.equals(PARAMETRES[3][2]);
        else perf = true;

		if (perf) {
			multTaille = MULTTAILLEPERF;
			tailleX = (int) (TAILLEX * MULTTAILLEPERF);
			tailleY = (int) (TAILLEY * MULTTAILLEPERF);
		} else {
			multTaille = 1;
			tailleX = TAILLEX;
			tailleY = TAILLEY;
		}

		// Chargement de l'image normal
		try {
			if (perf) 
				sprite = ImageIO.read(getClass().getResourceAsStream("/img/elements/" + getNom() + "Perf.png"));
			else
				sprite = ImageIO.read(getClass().getResourceAsStream("/img/elements/" + getNom() + ".png"));
        } catch (IOException e) {
            // Problème lors du chargement, on utilise rien
			System.out.println(e.getLocalizedMessage());
		}

		w = sprite.getWidth(null);
		h = sprite.getHeight(null);
		
		// Création de l'image sombre
		BufferedImage sSombre = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics gr = sSombre.getGraphics();
		gr.drawImage(sprite, 0, 0, null);
		RescaleOp op = new RescaleOp(ALPHAELEMCACHE, 0, null);
		sSombre = op.filter(sSombre, null);
		spriteSombre = sSombre;
		gr.dispose();
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
	public int getPDVPerdues () {
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
		this.soldat = soldat;
		setReafficher();
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
		nbSoldatVoient++;
		if (!estVisible) {
			estVisible = true;
			setReafficher();
		}
	}

	/**
	 * Indique que cet élément et son contenu ne sont pas visible par le joueur (case grisée)
	 */
	public void setCache () {
		nbSoldatVoient--;
		if (nbSoldatVoient <= 0) {
			nbSoldatVoient = 0;
			estVisible = false;
			setReafficher();
		}
	}

	/**
	 * Indique s'il faut réafficher l'élément
	 */
	public void setReafficher () {
		if(!jamaisAffiche) fileElem.add(this);
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
		
		if (estVisible) g.drawImage(getSprite(), x, y+type.deplacementY, null);
		else g.drawImage(spriteSombre, x, y+type.deplacementY, null);

		afficherSoldat(g, x, y);

		// Mise à jour du tableau des hitbox et buffer
		RunnableAfficher r = new RunnableAfficher(tabHitbox);
		Thread t = new Thread(r);
		t.start();

		jamaisAffiche = false;
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
			int w = getSprite().getWidth(null), h = (int) (getSprite().getHeight(null)-(127*multTaille)), alpha;

			// Mise à jour du buffer
			if (getSoldat() != null)
				deplacementYSoldat = (getSoldat().getSprite().getHeight()+15) - (tailleY/2-type.deplacementY);
			else deplacementYSoldat = 0;
			
			if (deplacementYSoldat < 0) deplacementYSoldat = 0;
			else h += deplacementYSoldat;

			if (buffer != null) buffer.flush();

			buffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = buffer.getGraphics();

			if (estVisible) g.drawImage(getSprite(), 0, deplacementYSoldat, null);
			else g.drawImage(spriteSombre, 0, deplacementYSoldat, null);

			afficherSoldat(buffer.getGraphics(), 0, -type.deplacementY+deplacementYSoldat);

			// Calcul des hitbox
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < w; j++) {
					alpha = (buffer.getRGB(j, i)>>24)&0xff;

					if (alpha == 255) {
						tabHitbox[i+drawY+type.deplacementY-deplacementYSoldat][j+drawX][0] = (byte) getPos().getX();
						tabHitbox[i+drawY+type.deplacementY-deplacementYSoldat][j+drawX][1] = (byte) getPos().getY();
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
			x = getPos().getX(), y = getPos().getY(), dY = type.deplacementY-deplacementYSoldat;

		switch (cote) {
			case 0: // Nord-ouest
				g.drawImage(buffer, drawX, drawY+dY, 
							drawX+halfW+1, drawY,
							0, 0, halfW+1, -dY, null);

				// On recalcule la hitbox
				SwingUtilities.invokeLater(() -> {
					for (int i = drawY+dY; i < drawY; i++) {
						for (int j = drawX; j < drawX+halfW+1; j++) {
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
	private void reafficher (Graphics g, byte[][][] tabHitbox, Carte carte) {
		int deplacementOuest = 0, deplacementEst = 0, w = getSprite().getWidth(),
			h = getSprite().getHeight();
		Element tmp;
		Position posTmp = new Position();
		BufferedImage img;

		// La position des éléments au dessus et en dessous dépend de la position de l'élément courant
		if (getPos().getX() % 2 == 0) deplacementEst = 1;
		else deplacementOuest = -1;

		// On réaffiche les parties basses des éléments au dessus de l'élément courant
		if (reafficherDessus) {
			// Element nord-ouest
			posTmp.set(getPos().getX()-1, getPos().getY()+deplacementOuest);
			if (posTmp.estValide()) {
				tmp = carte.getElement(posTmp);
				if (tmp != null) tmp.dessinerQuart(g, tabHitbox, 2); 
			}
			// Element nord-est
			posTmp.set(getPos().getX()-1, getPos().getY()+deplacementEst);
			if (posTmp.estValide()) {
				tmp = carte.getElement(posTmp); 
				if (tmp != null) tmp.dessinerQuart(g, tabHitbox, 3); 
			}
			reafficherDessus = false;
		}

		// On réaffiche l'élément courant
		if (estVisible) img = getSprite();
		else img = spriteSombre;

		g.drawImage(img, drawX, drawY+type.deplacementY, 
					drawX+w, (int) (drawY+190*multTaille), 
					0, 0, w, (int) (-type.deplacementY+190*multTaille), null);

		afficherSoldat(g, drawX, drawY);

		// On recalcule la hitbox
		RunnableAfficher r = new RunnableAfficher(tabHitbox);
		Thread t = new Thread(r);
		t.start();

		// On réaffiche les parties hautes des élément au dessous de l'élément courant

		// Element sud-ouest
		posTmp.set(getPos().getX()+1, getPos().getY()+deplacementOuest);
		if (posTmp.estValide()) {
			tmp = carte.getElement(posTmp); 
			if (tmp != null) tmp.dessinerQuart(g, tabHitbox, 1); 
		} else {
			// On réaffiche la partie "souterraine" gauche de l'élément
			g.drawImage(img, drawX, (int) (drawY+130*multTaille), 
						drawX+w/2, drawY+type.deplacementY+h, 
						0, (int) (-type.deplacementY+130*multTaille), 
						w/2, h, null);
		}

		// Element sud-est
		posTmp.set(getPos().getX()+1, getPos().getY()+deplacementEst);
		if (posTmp.estValide()) {
			tmp = carte.getElement(posTmp); 
			if (tmp != null) tmp.dessinerQuart(g, tabHitbox, 0); 
		} else {
			// On réaffiche la partie "souterraine" droite de l'élément
			g.drawImage(img, drawX+w/2, (int) (drawY+130*multTaille), 
						drawX+w, drawY+type.deplacementY+h, 
						w/2, (int) (-type.deplacementY+130*multTaille), 
						w, h, null);
		}
	}

	public static void reafficherFile (Graphics g, byte[][][] tabHitbox, Carte carte) {
		while (!fileElem.isEmpty()) {
			fileElem.removeFirst().reafficher(g, tabHitbox, carte);
		}
	}

	/**
	 * Affiche le soldat et ses points de vie positionné sur l'élément (s'il y en a)
	 * @param g l'objet graphique sur le lequel dessiné
	 * @param dX le déplacement horizontal pour atteindre l'élément
	 * @param dY le déplacement vertical pour atteindre l'élément
	 */
	private void afficherSoldat (Graphics g, int dX, int dY) {
		Soldat s = getSoldat();

		if (s == null) return;

		BufferedImage img = s.getSprite();
		// Coordonnées d'affichage du soldat
		int x = dX+(tailleX-img.getWidth())/2,
			y = dY+tailleY/2-img.getHeight();

		g.drawImage(img, x, y, null);
		reafficherDessus = true;

		double ratioVie = (double) s.getPoints() / s.getPointsMax();

		// Points de vie
		Shape clip = new RoundRectangle2D.Float(dX+tailleX/4, y-15.f, tailleX/2, 10, 10, 10),
			  oldClip = g.getClip();
		g.setClip(clip);
		g.setColor(COULEURPDV);
		g.fillRect(dX+tailleX/4, y-15, (int) ((tailleX/2.)*ratioVie), 10);
		g.setColor(COULEURPDV.brighter().brighter().brighter().brighter());
		g.fillRect((int) (dX+tailleX/4.+(tailleX/2.)*ratioVie), y-15, 
				   (int) ((tailleX/2.)*(1-ratioVie)), 10);
		g.setColor(Color.white);
		g.fillRect(dX+tailleX/4+10, y-13, tailleX/3, 1);
		g.setClip(oldClip);
	}
}
