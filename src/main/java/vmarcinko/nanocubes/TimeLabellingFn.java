package vmarcinko.nanocubes;

public interface TimeLabellingFn<DP> {
    long label(DP dataPoint);
}
