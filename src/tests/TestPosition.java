package tests;

import misc.*;

public class TestPosition {
    public static void main(String[] args) {
        Position pos = new Position(9, 14);
        Position pos2 = new Position(8,13);
        System.out.println(pos);
        if(((pos2.getX() == pos.getX()-1 || pos2.getX() == pos.getX()+1) && (pos2.getY() == pos.getY() || pos2.getY() == pos.getY() -1))) System.out.println("aaaa");
        System.out.println(pos.estVoisine(pos2));
    }
}
