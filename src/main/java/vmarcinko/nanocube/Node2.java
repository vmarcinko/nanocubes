package vmarcinko.nanocube;

import java.util.List;

public interface Node2 {
    long getLabel();

    Node2 addChild(int label);

    List<Node2> getChildren();

    int getChildrenSize();

    int getMemoryLocation();
}
