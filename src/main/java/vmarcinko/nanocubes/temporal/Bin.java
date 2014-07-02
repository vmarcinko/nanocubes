package vmarcinko.nanocubes.temporal;

import vmarcinko.nanocubes.Utils;

public class Bin implements Comparable<Bin> {
    private final long timestamp;
    private int encodedCount; // we use this as unsigned int by treating Integer.MIN_VALUE as zero

    public Bin(long timestamp, long count) {
        this.timestamp = timestamp;
        this.encodedCount = Utils.encodeUnsignedInt(count);
    }

    public Bin(Bin original) {
        this.timestamp = original.timestamp;
        this.encodedCount = original.encodedCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCount() {
        return Utils.decodeUnsignedInt(encodedCount);
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
        sb.append(", count=").append(getCount());
        sb.append('}');
        return sb.toString();
    }
}
