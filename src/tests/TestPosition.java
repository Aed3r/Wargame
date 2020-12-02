package tests;

import unites.*;

public class TestPosition {
    public static void main(String[] args) {
        Position pos = new Position(19, 7);
        System.out.println(pos);
        System.out.println(pos.estVoisine(new Position(20, 7)));
    }
}
