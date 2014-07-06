package vmarcinko.nanocube;

public class Nanocube2Test {
    public static void main(String[] args) {
        Nanocube2 nanocube2 = new Nanocube2();
        System.out.println("nanocube2 = " + nanocube2);
        Node2 rootNode = nanocube2.getRoot();

        Node2 child1 = rootNode.addChild(45);

        child1.addChild(12);
        child1.addChild(2);

        System.out.println("nanocube2 = " + nanocube2);
    }
}
