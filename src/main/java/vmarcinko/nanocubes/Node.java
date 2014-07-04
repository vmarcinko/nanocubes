package vmarcinko.nanocubes;

import java.util.*;

public class Node implements Content {
    private static final int CONTENT_SHARED_BIT_INDEX = 999099;

    private final Long value;

    private final Map<Long, Link<Node>> childLinks = new HashMap<>();

    /*
        private final List<Node> children = new ArrayList<>();
    */
    private Content content;
//    private final BitSet sharedLinks = new BitSet();
    private boolean contentShared = false;

    public static void main(String[] args) {
        BitSet sharedLinks = new BitSet();
        sharedLinks.set(CONTENT_SHARED_BIT_INDEX, true);
    }

    public Node(Long value) {
        this.value = value;
    }

    /**
     * Creates a new child link to node keyed on value.
     */
    public Node newProperChild(Long value) {
        Node childNode = new Node(value);
        childLinks.put(value, new Link<>(false, childNode));
        return childNode;
    }

    /**
     * Creates a shared child link to node keyed on value
     */
    public void newSharedChild(Long value, Node node) {
        Node nodeChild = node.childLinks.get(value).getTarget();
        childLinks.put(value, new Link<>(true, nodeChild));
    }

    public Map<Long, Link<Node>> getChildLinks() {
        return childLinks;
    }

    public Node getChild(Long value) {
        Link<Node> childLink = childLinks.get(value);
        if (childLink == null) {
            return null;
        }
        return childLink.getTarget();
    }

    /**
     * Convenience method to create a shared content link to the content in given node.
     */
    public void setSharedContentWithNode(Node node) {
        setContent(true, node.getContent());
    }

    public void setContent(boolean shared, Content content) {
        this.content = content;
        this.contentShared = shared;
//        this.sharedLinks.set(CONTENT_SHARED_BIT_INDEX, shared);
    }

    public boolean isContentShared() {
        if (content == null) {
            throw new IllegalStateException("There is no content");
        }
        return contentShared;
//        return this.sharedLinks.get(CONTENT_SHARED_BIT_INDEX);
    }

    public Content getContent() {
        return content;
    }

    public <C extends Content> C getContent(Class<C> clazz) {
        return (C) getContent();
    }

    @Override
    public Content shallowCopy() {
        // Creates a new copy of the node with shared content and shared children
        Node copy = new Node(value);
        copy.setSharedContentWithNode(this);
        for (Long label : childLinks.keySet()) {
            copy.newSharedChild(label, this);
        }
        return copy;
    }

    @Override
    public void appendPrettyPrint(StringBuilder sb, int depth) {
        String valueDescription = value == null ? "--dimension-root--" : value.toString();
        sb.append("Node(").append(valueDescription).append(")");

        // display children
        if (!childLinks.isEmpty()) {
            appendTabbedNewLine(sb, depth + 1);
            sb.append("# children:");
            for (Link<Node> childLink : childLinks.values()) {
                appendTabbedNewLine(sb, depth + 2);
                childLink.appendPrettyPrint(sb, depth + 2);
            }
        }

        // display content
        if (getContent() != null) {
            appendTabbedNewLine(sb, depth + 1);
            sb.append("# content: ");
            appendLinkPrettyPrint(sb, depth + 2, isContentShared(), getContent());
        }
    }

    public void appendLinkPrettyPrint(StringBuilder sb, int depth, boolean shared, Content content) {
        String typeDescription = shared ? "SHARED" : "PROPER";
        sb.append("--").append(typeDescription).append("--> ");
        content.appendPrettyPrint(sb, depth);
    }

    private void appendTabbedNewLine(StringBuilder sb, int tabCount) {
        sb.append("\n");
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
    }

}
