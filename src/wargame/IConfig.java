package wargame;

import java.awt.Color;
public interface IConfig {
	int LARGEUR_CARTE = 30; int HAUTEUR_CARTE = 20; // en nombre de cases
	int NB_HEROS = 6; int NB_MONSTRES = 15;
	/* Noms des paramètres dans le fichier de configuration. 
			- [0]: nom du paramètre à utiliser dans le fichier des configurations
			- [1]: nom à utiliser à l'affichage
			- [2]: valeur par défaut du paramètre
			- ...: reste des valeurs triées logiquement
	*/
	String[][] PARAMETRES = {
		{"tailleFenetre", "Taille de la fenètre", "grande", "plein écran", "petite", "moyenne"},
		{"difficulte", "Difficulté", "normale", "difficile", "simple"},
		{"deplacementVert", "Déplacement vertical", "éteind", "allumé"}
	};
	// Chemin vers le fichier de configuration
	String CONFIGFILE = "data/config.properties"; 
	// Police
	String FONTNAME = "Raleway";
	int FONTSIZE = 20;
	// Couleur utilisés pour l'interface
	Color TL = new Color(1f, 1f, 1f, .2f); // Haut
    Color BR = new Color(0f, 0f, 0f, .4f); // Bas
    Color ST = new Color(1f, 1f, 1f, .2f); // Haut passage souris / Bas clic
    Color SB = new Color(1f, 1f, 1f, .1f); // Bas passage souris / Haut clic
	Color TC = new Color(1f, 1f, 1f, 1.f); // Couleur du texte
	int TAILLEX = 257, TAILLEY = 194; // Tailles des sprite élément
	int MARGX = TAILLEX*2, MARGY = TAILLEY*2; // Marges horizontales et verticales sur la carte
	Color BGCOLOR = new Color(12, 12, 12); // Couleur en fond de la carte
	float ALPHAELEMCACHE = 0.5f; // Transparence des éléments cachés du plateau
}