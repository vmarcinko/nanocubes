package vmarcinko.nanocube;

import net.yadan.banana.memory.IMemAllocator;

import java.util.ArrayList;
import java.util.List;

public class Node2Impl implements Node2 {
    private static final int LABEL_OFFSET = 0;
    private static final int CONTENT_AND_CHILDREN_SHARED_BIT_SET_OFFSET = 2;
    private static final int CONTENT_POINTER_OFFSET = 4;

    private static final int CHILDREN_LIST_POINTER_OFFSET = 5;

    private final IMemAllocator memory;
    private final int pointer;

    public Node2Impl(IMemAllocator memory, int pointer) {
        this.memory = memory;
        this.pointer = pointer;
    }

    static int createNode(IMemAllocator memory, long label) {
        int nodePointer = memory.malloc(6);
        memory.setLong(nodePointer, LABEL_OFFSET, label);
        memory.setLong(nodePointer, CONTENT_AND_CHILDREN_SHARED_BIT_SET_OFFSET, 0L);

        memory.setInt(nodePointer, CONTENT_POINTER_OFFSET, -1);

        int childrenListPointer = MemArrayList.newList(memory);
        memory.setInt(nodePointer, CHILDREN_LIST_POINTER_OFFSET, childrenListPointer);

        return nodePointer;
    }

    @Override
    public long getLabel() {
        return memory.getLong(pointer, LABEL_OFFSET);
    }

    @Override
    public Node2 addChild(int label) {
        int childPointer = createNode(memory, label);

        int childrenListPointer = memory.getInt(pointer, CHILDREN_LIST_POINTER_OFFSET);
        MemArrayList.add(memory, childrenListPointer, childPointer);
        return new Node2Impl(memory, childPointer);
    }

    @Override
    public List<Node2> getChildren() {
        int childrenListPointer = memory.getInt(pointer, CHILDREN_LIST_POINTER_OFFSET);
        int[] childrenPointers = MemArrayList.getAll(memory, childrenListPointer);

        List<Node2> children = new ArrayList<>(childrenPointers.length);
        for (int childPointer : childrenPointers) {
            children.add(new Node2Impl(memory, childPointer));
        }
        return children;
    }

    @Override
    public int getChildrenSize() {
        int childrenListPointer = memory.getInt(pointer, CHILDREN_LIST_POINTER_OFFSET);
        return MemArrayList.getSize(memory, childrenListPointer);
    }

    @Override
    public int getMemoryPointer() {
        return pointer;
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
