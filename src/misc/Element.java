package misc;

import java.awt.*;

public abstract class Element {
    private Position pos;
    private Image sprite;
    private Color couleur;
    private String nom;
    private boolean usesImage;

    protected Element(Position pos, Image sprite, String nom) {
    	this (pos, Color.white, nom);
        setSprite(sprite);
    }

    protected Element(Position pos, Color couleur, String nom) {
    	setPos(pos);
    	setCouleur(couleur);
    	setNom(nom);
    }

    /* Get Set */

	/**
	 * @return the pos
	 */
	public Position getPos() {
		return pos;
	}

	/**
	 * @param pos the pos to set
	 */
	public void setPos(Position pos) {
		this.pos = pos;
	}

	/**
	 * @return the sprite
	 */
	public Image getSprite() {
		return sprite;
	}

	/**
	 * @param sprite the sprite to set
	 */
	public void setSprite(Image sprite) {
        this.sprite = sprite;
        usesImage = false;
	}

	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
    }
    
    /**
	 * @return the couleur
	 */
	public Color getColor() {
		return couleur;
	}

	/**
	 * @param couleur the couleur to set
	 */
	public void setCouleur(Color couleur) {
        this.couleur = couleur;
        usesImage = false;
    }

    /**
     * @return true si l'élément à une image, false sinon
     */
    public boolean usesImage () {
        return usesImage;
    }

    /* Methods */
	public abstract void afficher();
	public abstract void supprimer();
}
