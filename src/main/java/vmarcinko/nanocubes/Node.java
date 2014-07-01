package vmarcinko.nanocubes;

import java.util.HashMap;
import java.util.Map;

public class Node implements Content {
    private static long global_id = 0;

    private final long id;

    private final Map<Object, Link<Node>> childLinks = new HashMap<>();
    private Link<? extends Content> contentLink = null;
    private Object value = null;

    public Node() {
        global_id++;
        this.id = global_id;
    }

    public Node(Object value) {
        this();
        this.value = value;
    }

    public long getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }

    /**
     * Creates a new child link to node keyed on value.
     */
    public Node newProperChild(Object value) {
        Node childNode = new Node(value);
        childLinks.put(value, new Link<>(false, childNode));
        return childNode;
    }

    /**
     * Creates a shared child link to node keyed on value
     */
    public void newSharedChild(Object value, Node node) {
        Node nodeChild = node.childLinks.get(value).getTarget();
        childLinks.put(value, new Link<>(true, nodeChild));
    }

    /**
     * Creates a shared content link to the content in given node.
     */
    public void setSharedContent(Node node) {
        this.contentLink = new Link<>(true, node.contentLink.getTarget());
    }

    public Map<Object, Link<Node>> getChildLinks() {
        return childLinks;
    }

    public Node getChild(Object value) {
        Link<Node> childLink = childLinks.get(value);
        if (childLink == null) {
            return null;
        }
        return childLink.getTarget();
    }

    public Link<? extends Content> getContentLink() {
        return contentLink;
    }

    public <C extends Content> C getContent(Class<C> clazz) {
        if (contentLink == null) {
            return null;
        }
        return (C) contentLink.getTarget();
    }

    public void setContentLink(Link<? extends Content> contentLink) {
        this.contentLink = contentLink;
    }

    @Override
    public Content shallowCopy() {
        // Creates a new copy of the node with shared content and shared children
        Node copy = new Node(value);
        copy.setSharedContent(this);
        for (Object label : childLinks.keySet()) {
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
        if (contentLink != null) {
            appendTabbedNewLine(sb, depth + 1);
            sb.append("# content: ");
            contentLink.appendPrettyPrint(sb, depth + 2);
        }
    }

    private void appendTabbedNewLine(StringBuilder sb, int tabCount) {
        sb.append("\n");
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Node node = (Node) o;

        if (id != node.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
