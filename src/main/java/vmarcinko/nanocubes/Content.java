package vmarcinko.nanocubes;

public interface Content {
    Content shallowCopy();

    void appendPrettyPrint(StringBuilder sb, int depth);
}
