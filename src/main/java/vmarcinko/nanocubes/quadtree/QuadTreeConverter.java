package vmarcinko.nanocubes.quadtree;

public class QuadTreeConverter {
    private QuadTreeConverter() {
    }

    public static void main(String[] args) {
        System.out.println("args = " + convert(1, 2, 3, 3, 2));
    }

    public static QuadTreeAddress convert(int x, int y, int maxX, int maxY, int depth) {
        if (depth < 1) {
            throw new IllegalArgumentException("Depth cannot be less than 1, but is " + depth);
        }
        String binaryX = convertSingleDimension(x, maxX, depth);
        String binaryY = convertSingleDimension(y, maxY, depth);
        return new QuadTreeAddress(binaryX, binaryY);
    }

    private static String convertSingleDimension(int a, int maxA, int depth) {
        StringBuilder sb = new StringBuilder();

        int low = 0;
        int high = maxA;
        for (int i = 1; i <= depth; i++) {
            int middle = low + (high + 1 - low) / 2;
            Character digit = null;
            if (a < middle) {
                high = middle - 1;
                digit = '0';
            } else {
                low = middle;
                digit = '1';
            }
            sb.append(digit);
        }
        return sb.toString();
    }
}
