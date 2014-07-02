package vmarcinko.nanocubes;

public class Utils {

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
}
