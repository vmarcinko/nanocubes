package vmarcinko.nanocube;

import net.yadan.banana.memory.IMemAllocator;
import net.yadan.banana.memory.block.BigBlockAllocator;
import net.yadan.banana.memory.malloc.TreeAllocator;

public class Nanocube2 {
    private final IMemAllocator memory;
    private final int rootNodeMemLoc;

    public Nanocube2() {
//        new BigBlockAllocator(10, 3, 3.0);
        memory = new TreeAllocator(10, 3, 3.0d);
        rootNodeMemLoc = Node2Impl.createNode(memory, -1);
    }

    public Node2 getRoot() {
        return new Node2Impl(memory, rootNodeMemLoc);
    }

    @Override
    public String toString() {
        return getRoot().toString();
    }
}
