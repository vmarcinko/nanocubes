package vmarcinko.nanocubes;

public class Link<C extends Content> {
    private boolean shared;
    private C target;

    public Link(boolean shared, C target) {
        this.shared = shared;
        this.target = target;
    }

    public boolean isShared() {
        return shared;
    }

    public C getTarget() {
        return target;
    }

    public void appendPrettyPrint(StringBuilder sb, int depth) {
        String typeDescription = shared ? "SHARED" : "PROPER";
        sb.append("--").append(typeDescription).append("--> ");
        target.appendPrettyPrint(sb, depth);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Link{");
        sb.append("shared=").append(shared);
        sb.append(", target=").append(target);
        sb.append('}');
        return sb.toString();
    }
}
