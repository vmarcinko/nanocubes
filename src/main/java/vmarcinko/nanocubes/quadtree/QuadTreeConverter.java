package vmarcinko.nanocubes.quadtree;

import vmarcinko.nanocubes.Utils;

public class QuadTreeConverter {

    private QuadTreeConverter() {
    }

    public static void main(String[] args) {
        System.out.println("args = " + convert(1, 2, 3, 3, 2));
        System.out.println("args = " + convert(1, 2, 3, 3, 2));
    }

    public static long convert(int x, int y, int maxX, int maxY, int depth) {
        if (depth < 1) {
            throw new IllegalArgumentException("Depth cannot be less than 1, but is " + depth);
        }
        int binaryX = convertSingleDimension(x, maxX, depth);
        int binaryY = convertSingleDimension(y, maxY, depth);

        return Utils.encodeTwoInts(binaryX, binaryY);
    }

    private static int convertSingleDimension(int a, int maxA, int depth) {
        int result = 0;

        int low = 0;
        int high = maxA;
        for (int i = 1; i <= depth; i++) {
            if (i > 1) {
                result = result << 1;
            }
            int middle = low + (high + 1 - low) / 2;
            byte digit = 0;
            if (a < middle) {
                high = middle - 1;
                digit = 0;
            } else {
                low = middle;
                digit = 1;
            }
            result = result | digit;
        }
        return result;
    }
}
