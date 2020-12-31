package wargame;

import java.awt.Color;
public interface IConfig {
	int LARGEUR_CARTE = 25; int HAUTEUR_CARTE = 15; // en nombre de cases
	int NB_PIX_CASE = 20;
	int POSITION_X = 100; int POSITION_Y = 50; // Position de la fen�tre
	int NB_HEROS = 6; int NB_MONSTRES = 15; int NB_OBSTACLES = 20;
	Color COULEUR_VIDE = Color.white, COULEUR_INCONNU = Color.lightGray;
	Color COULEUR_TEXTE = Color.black, COULEUR_MONSTRES = Color.black;
	Color COULEUR_HEROS = Color.red, COULEUR_HEROS_DEJA_JOUE = Color.pink;
	Color COULEUR_EAU = Color.blue, COULEUR_FORET = Color.green, COULEUR_ROCHER = Color.gray;
	/* Noms des paramètres dans le fichier de configuration. 
			- [0]: nom du paramètre à utiliser dans le fichier des configurations
			- [1]: nom à utiliser à l'affichage
			- [2]: valeur par défaut du paramètre
			- ...: reste des valeurs triées logiquement
	*/
	String[][] PARAMETRES = {
		{"tailleFenetre", "Taille de la fenètre", "grande", "plein écran", "petite", "moyenne"},
		{"difficulte", "Difficulté", "normale", "difficile", "simple"}
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
}