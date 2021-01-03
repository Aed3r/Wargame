package wargame;

import java.awt.Graphics;

import misc.Element;
import unites.Heros;
import misc.Position;
import unites.Soldat;
public interface ICarte {
	Element getElement(Position pos);
	//Position trouvePositionVide(); // Trouve al�atoirement une position vide sur la carte
	Position trouvePositionVide(Position pos); // Trouve une position vide choisie
								// al�atoirement parmi les 8 positions adjacentes de pos
	Heros trouveHeros(); // Trouve al�atoirement un h�ros sur la carte
	Heros trouveHeros(Position pos); // Trouve un h�ros choisi al�atoirement
									 // parmi les 8 positions adjacentes de pos
	boolean deplaceSoldat(Position pos, Soldat soldat);
	void mort(Soldat perso);
	boolean actionHeros(Position pos, Position pos2);
	void jouerSoldats();
	void toutDessiner(Graphics g);
}