package tests;

import misc.*;
import terrains.Carte;
import terrains.Obstacle;

public class TestCarte {
    public static void main(String[] args) {
        int test = (int)Math.random();
        System.out.printf("%d \n", test);

        Obstacle O = new Obstacle(TypeObstacle.ROCHER, new Position(1, 2));
        Carte C = new Carte ();
    }
}
