package vmarcinko.nanocubes.temporal;

public class Bin implements Comparable<Bin> {
    private final long timestamp;
    private int encodedCount; // we use this as unsigned int by treating Integer.MIN_VALUE as zero

    public Bin(long timestamp, long count) {
        this.timestamp = timestamp;
        encodeCount(count);
    }

    private void encodeCount(long count) {
        this.encodedCount = (int) (count + Integer.MIN_VALUE);
    }

    private long decodeCount() {
        return this.encodedCount - Integer.MIN_VALUE;
    }

    public Bin(Bin original) {
        this.timestamp = original.timestamp;
        this.encodedCount = original.encodedCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCount() {
        return decodeCount();
    }

    public void incrementCount() {
        this.encodedCount++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Bin bin = (Bin) o;

        if (timestamp != bin.timestamp) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (timestamp ^ (timestamp >>> 32));
    }

    @Override
    public int compareTo(Bin o) {
        return new Long(timestamp).compareTo(new Long(o.timestamp));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", count=").append(decodeCount());
        sb.append('}');
        return sb.toString();
    }
}
