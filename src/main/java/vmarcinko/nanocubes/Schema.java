package vmarcinko.nanocubes;

import java.util.ArrayList;
import java.util.List;

public class Schema<DP> {
    private final List<List<LabellingFn<DP>>> chains = new ArrayList<>();

    public List<LabellingFn<DP>> getDimensionChain(int dimension) {
        return chains.get(dimension);
    }

    public int getDimension() {
        return chains.size();
    }

    public List<LabellingFn<DP>> addChain() {
        List<LabellingFn<DP>> chain = new ArrayList<>();
        chains.add(chain);
        return chain;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Schema{");
        sb.append("chains=").append(chains);
        sb.append('}');
        return sb.toString();
    }
}
