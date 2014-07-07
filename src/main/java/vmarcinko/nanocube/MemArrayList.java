package vmarcinko.nanocube;

import net.yadan.banana.memory.IMemAllocator;

public class MemArrayList {
    private static final int RESERVED_FIELDS_SIZE = 2;
    private static final int INITIAL_CAPACITY = 1;
    private static final int GROWTH_FACTOR = 2;

    private static final int SIZE_OFFSET = 0; // todo: size & capacity as shorts?
    private static final int CAPACITY_OFFSET = 1;
    private static final int FIRST_ELEMENT_POINTER_OFFSET = 2;

    private MemArrayList() {
        // utility
    }

    public static int newList(IMemAllocator memory) {
        int pointer = memory.malloc(RESERVED_FIELDS_SIZE + INITIAL_CAPACITY);
        setSize(memory, pointer, 0);
        setCapacity(memory, pointer, INITIAL_CAPACITY);
        return pointer;
    }

    public static int add(IMemAllocator memory, int pointer, int elementPointer) {
        int newPointer = pointer;

        int listSize = getSize(memory, newPointer);
        listSize++;

        int capacity = getCapacity(memory, newPointer);
        if (listSize > capacity) {
            capacity = capacity * GROWTH_FACTOR;
            newPointer = memory.realloc(pointer, RESERVED_FIELDS_SIZE + capacity);
            setCapacity(memory, newPointer, capacity);
        }

        setSize(memory, newPointer, listSize);
        set(memory, newPointer, listSize - 1, elementPointer);

        return newPointer;
    }

    public static int getSize(IMemAllocator memory, int pointer) {
        return memory.getInt(pointer, SIZE_OFFSET);
    }

    public static int getCapacity(IMemAllocator memory, int pointer) {
        return memory.getInt(pointer, CAPACITY_OFFSET);
    }

    public static int[] getAll(IMemAllocator memory, int pointer) {
        int size = getSize(memory, pointer);
        int[] elementPointers = new int[size];
        if (size > 0) {
            memory.getInts(pointer, FIRST_ELEMENT_POINTER_OFFSET, elementPointers, 0, size);
        }
        return elementPointers;
    }

    private static void setSize(IMemAllocator memory, int pointer, int size) {
        memory.setInt(pointer, SIZE_OFFSET, size);
    }

    private static void setCapacity(IMemAllocator memory, int pointer, int capacity) {
        memory.setInt(pointer, CAPACITY_OFFSET, capacity);
    }

    private static void set(IMemAllocator memory, int pointer, int index, int elementPointer) {
        memory.setInt(pointer, FIRST_ELEMENT_POINTER_OFFSET + index, elementPointer);
    }

    public static int get(IMemAllocator memory, int pointer, int index) {
        return memory.getInt(pointer, FIRST_ELEMENT_POINTER_OFFSET + index);
    }
}
