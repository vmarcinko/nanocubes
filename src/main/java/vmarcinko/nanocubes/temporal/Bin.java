package vmarcinko.nanocubes.temporal;

public class Bin implements Comparable<Bin> {
    private final long timestamp;
    private long count = 0;

    public Bin(long timestamp, long count) {
        this.timestamp = timestamp;
        this.count = count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
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
        sb.append(", count=").append(count);
        sb.append('}');
        return sb.toString();
    }
}
