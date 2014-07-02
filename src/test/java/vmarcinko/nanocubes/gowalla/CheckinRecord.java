package vmarcinko.nanocubes.gowalla;

import java.util.Date;

public class CheckinRecord {
    private final Date time;
    private final double latitude;
    private final double longitude;

    public CheckinRecord(Date time, double latitude, double longitude) {
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Date getTime() {
        return time;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("time=").append(time);
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}
