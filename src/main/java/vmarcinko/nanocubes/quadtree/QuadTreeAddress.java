package vmarcinko.nanocubes.quadtree;

public class QuadTreeAddress {
    private final String binaryX;
    private final String binaryY;

    public QuadTreeAddress(String binaryX, String binaryY) {
        this.binaryX = binaryX;
        this.binaryY = binaryY;
    }

    public String getBinaryX() {
        return binaryX;
    }

    public String getBinaryY() {
        return binaryY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        QuadTreeAddress that = (QuadTreeAddress) o;

        if (!binaryX.equals(that.binaryX)) {
            return false;
        }
        if (!binaryY.equals(that.binaryY)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = binaryX.hashCode();
        result = 31 * result + binaryY.hashCode();
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("(").append(binaryX).append(",").append(binaryY).append(")");
        return sb.toString();
    }
}
