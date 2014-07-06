package vmarcinko.nanocube;

import net.yadan.banana.memory.IMemAllocator;

import java.util.ArrayList;
import java.util.List;

public class Node2Impl implements Node2 {
    private static final int LABEL_OFFSET = 0;
    private static final int CHILDREN_SIZE_OFFSET = 2;

    private final IMemAllocator memory;
    private final int memLoc;

    public Node2Impl(IMemAllocator memory, int memLoc) {
        this.memory = memory;
        this.memLoc = memLoc;
    }

    public static int createNode(IMemAllocator memory, long label) {
        int nodeMemLoc = memory.malloc(10);
        memory.setLong(nodeMemLoc, LABEL_OFFSET, label);
        memory.setInt(nodeMemLoc, CHILDREN_SIZE_OFFSET, 0);
        return nodeMemLoc;
    }

    @Override
    public long getLabel() {
        return memory.getLong(memLoc, LABEL_OFFSET);
    }

    @Override
    public Node2 addChild(int label) {
        int childMemLoc = createNode(memory, label);

        int childrenSize = getChildrenSize();
        childrenSize++;
        memory.setInt(memLoc, CHILDREN_SIZE_OFFSET, childrenSize);
        memory.setInt(memLoc, CHILDREN_SIZE_OFFSET + childrenSize, childMemLoc);

        return new Node2Impl(memory, childMemLoc);
    }

    @Override
    public List<Node2> getChildren() {
        List<Node2> children = new ArrayList<>();
        for (int i = 0; i < getChildrenSize(); i++) {
            int childMemLoc = memory.getInt(memLoc, CHILDREN_SIZE_OFFSET + 1 + i);
            children.add(new Node2Impl(memory, childMemLoc));
        }
        return children;
    }

    @Override
    public int getChildrenSize() {
        return memory.getInt(memLoc, CHILDREN_SIZE_OFFSET);
    }

    @Override
    public int getMemoryLocation() {
        return memLoc;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("label=").append(getLabel());
        sb.append(", children=").append(getChildren());
        sb.append('}');
        return sb.toString();
    }
}
