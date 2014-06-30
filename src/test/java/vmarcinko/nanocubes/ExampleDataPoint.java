package vmarcinko.nanocubes;

public class ExampleDataPoint {
    public enum DeviceType {
        ANDROID,
        IPHONE
    }

    private final int geoX;
    private final int geoY;
    private final DeviceType deviceType;
    private final long time;

    public ExampleDataPoint(int geoX, int geoY, DeviceType deviceType, long time) {
        this.geoX = geoX;
        this.geoY = geoY;
        this.deviceType = deviceType;
        this.time = time;
    }

    public int getGeoX() {
        return geoX;
    }

    public int getGeoY() {
        return geoY;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("geoX=").append(geoX);
        sb.append(", geoY=").append(geoY);
        sb.append(", deviceType=").append(deviceType);
        sb.append(", time=").append(time);
        sb.append('}');
        return sb.toString();
    }
}
