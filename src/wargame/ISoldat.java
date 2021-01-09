package wargame;

import misc.Position;
import unites.Soldat;

/**
 * Interface contenant les enumerations pour les types de Soldats
 */
public interface ISoldat {
   enum TypesH {
      CHEVALIER (80,3,10,2), GNOME (30,1,8,9), BARBARE (60,3,15,0), ARCHER (20,5,5,10);
      
      private final int POINTS_DE_VIE, PORTEE_VISUELLE, PUISSANCE, TIR;

      TypesH(int points, int portee, int puissance, int tir) {
      POINTS_DE_VIE = points; PORTEE_VISUELLE = portee;
      PUISSANCE = puissance; TIR = tir;
      }
      public int getPoints() { return POINTS_DE_VIE; }
      public int getPortee() { return PORTEE_VISUELLE; }
      public int getPuissance() { return PUISSANCE; }
      public int getTir() { return TIR; }
      public static TypesH getTypeHAlea() {
         return values()[(int)(Math.random()*values().length)];
      }
   }
   public enum TypesM {
      TROLL (100,1,25,0), ZOMBIE (40,2,10,0), MOMIE (30,2,5,2), SORCIER (20,2,5,10 );
      
      private final int POINTS_DE_VIE, PORTEE_VISUELLE, PUISSANCE, TIR;
      
      TypesM(int points, int portee, int puissance, int tir) {
      POINTS_DE_VIE = points; PORTEE_VISUELLE = portee;
      PUISSANCE = puissance; TIR = tir;
      }
      
      public int getPoints() { return POINTS_DE_VIE; }
      public int getPortee() { return PORTEE_VISUELLE; }
      public int getPuissance() { return PUISSANCE; }
      public int getTir() { return TIR; } 
      
      public static TypesM getTypeMAlea() {
         return values()[(int)(Math.random()*values().length)];
      }
   }

   int getPoints(); boolean getTour(); int getPortee();
   boolean joueTour();
   void combat(Soldat soldat);
   void seDeplace(Position newPos);
}