package vmarcinko.nanocubes;

public class Utils {
    private final static String binaryMask = "1111111111111111111111111111111";
    private final static int intMask = Integer.parseInt(binaryMask, 2);

    private Utils() {
    }

    public static long decodeUnsignedInt(int encodedValue) {
        return (long) encodedValue - (long) Integer.MIN_VALUE;
    }

    public static int encodeUnsignedInt(long value) {
        long maxValue = 2 * (long) Integer.MAX_VALUE;
        if (value > maxValue) {
            throw new IllegalArgumentException("Value cannot be larger than " + maxValue + ", but is " + value);
        }
        return (int) (value + Integer.MIN_VALUE);
    }

    public static long encodeTwoInts(int a, int b) {
        long encoded = 0;
        encoded |= a;
        encoded <<= binaryMask.length();
        encoded |= b;
        return encoded;
    }

    public static int[] decodeTwoInts(long encodedInts) {
        int b = (int) (encodedInts & intMask);
        int a = (int) ((encodedInts >> binaryMask.length()) & intMask);
        return new int[]{a, b};
    }
}
