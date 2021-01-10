package wargame;

import java.awt.Color;

import misc.Position;
public interface IConfig {
	int LARGEUR_CARTE = 20; int HAUTEUR_CARTE = 15; // en nombre de cases
	int LARGEUR_SPAWN = 5; int HAUTEUR_SPAWN = 5; //taille similaire
	Position POS_INIT_SPAWN_MONSTRE = new Position(1, 1);
	Position POS_INIT_SPAWN_GENTIL = new Position(HAUTEUR_CARTE-1-HAUTEUR_SPAWN, LARGEUR_CARTE-1-LARGEUR_SPAWN);
	int NB_HEROS = 25; int NB_MONSTRES = 25;
	/* ##############################  */
	/* Noms des paramètres dans le fichier de configuration. 
			- [0]: nom du paramètre à utiliser dans le fichier des configurations
			- [1]: nom à utiliser à l'affichage
			- [2]: valeur par défaut du paramètre
			- ...: reste des valeurs triées logiquement
	*/
	String[][] PARAMETRES = {
		{"tailleFenetre", "Taille de la fenètre", "grande", "plein écran", "petite", "moyenne"},
		{"difficulte", "Difficulté", "normale", "difficile", "simple"},
		{"deplacementVert", "Déplacement vertical", "éteind", "allumé"},
		{"modePerf", "Mode Performance", "On", "Off"}
	};
	// Chemin vers le fichier de configuration
	String CONFIGFILE = "/config.properties"; 
	// Police
	String FONTNAME = "Raleway";
	int FONTSIZE = 20;
	// Couleur utilisés pour l'interface et le jeu
	Color TL = new Color(1f, 1f, 1f, .2f); // Haut
    Color BR = new Color(0f, 0f, 0f, .4f); // Bas
    Color ST = new Color(1f, 1f, 1f, .2f); // Haut passage souris / Bas clic
    Color SB = new Color(1f, 1f, 1f, .1f); // Bas passage souris / Haut clic
	Color TC = new Color(1f, 1f, 1f, 1.f); // Couleur du texte
	Color BGCOLOR = new Color(12, 12, 12); // Couleur en fond de la carte
	float ALPHAELEMCACHE = 0.5f; // Transparence des éléments cachés du plateau
	Color COULEURPDV = new Color(255, 150, 150); // Couleur de la barre de PDV
	/* Plateau de jeu */
	int TAILLEX = 256, TAILLEY = 194; // Tailles des sprite élément
	int MARGX = TAILLEX*2, MARGY = TAILLEY*2; // Marges horizontales et verticales sur la carte
	int DEPLACEMENTCLAVIER = 30; // Nombre de pixels à déplacer par appui des flèches du clavier
	float MULTTAILLEPERF = 0.5f; // Multiplicateur des tailles pour le mode performance
}