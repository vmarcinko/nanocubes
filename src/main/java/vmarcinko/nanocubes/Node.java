package vmarcinko.nanocubes;

import java.util.ArrayList;
import java.util.List;

public class Node implements Content {
    private static final int CONTENT_SHARED_BIT_INDEX = 0;

    private final long label;

    private final List<Node> children = new ArrayList<>(2);
    private Content content;
    private long sharedLinkBitSet = 0L;

    public Node() {
        this(-1L);
    }

    public Node(long label) {
        this.label = label;
    }

    /**
     * Creates a new child link to node keyed on label.
     */
    public Node newProperChild(long label) {
        Node childNode = new Node(label);
        addChildNode(childNode, false);
        return childNode;
    }

    private void addChildNode(Node childNode, boolean shared) {
        children.add(childNode);
        setLinkShared(children.size(), shared);
    }

    public int getChildrenSize() {
        return children.size();
    }

    public boolean isChildShared(long label) {
        int childIndex = getChildIndex(label);
        return isLinkShared(childIndex + 1);
    }

    private int getChildIndex(long label) {
        for (int i = 0; i < children.size(); i++) {
            Node node = children.get(i);
            if (node.label == label) {
                return i;
            }
        }
        throw new IllegalStateException("Cannot find child not with label: " + label);
    }

    public Node getChild(long label) {
        for (Node child : children) {
            if (child.label == label) {
                return child;
            }
        }
        return null;
    }

    public void replaceChild(Node newChild) {
        int childIndex = getChildIndex(newChild.label);
        children.set(childIndex, newChild);
        setLinkShared(childIndex + 1, false);
    }

    /**
     * Convenience method to create a shared content link to the content in given node.
     */
    public void setSharedContentWithNode(Node node) {
        Content nodeContent = node.getContent();
        setContent(true, nodeContent);
    }

    public void setContent(boolean shared, Content content) {
        this.content = content;
        setLinkShared(CONTENT_SHARED_BIT_INDEX, shared);
    }

    public boolean isContentShared() {
        if (content == null) {
            throw new IllegalStateException("There is no content");
        }
        return isLinkShared(CONTENT_SHARED_BIT_INDEX);
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
        Node copy = new Node(label);
        copy.setSharedContentWithNode(this);

        for (Node child : children) {
            copy.addChildNode(child, true);
        }

        return copy;
    }

    @Override
    public void appendPrettyPrint(StringBuilder sb, int depth) {
        String valueDescription = label == -1 ? "--dimension-root--" : String.valueOf(label);
        sb.append("Node(").append(valueDescription).append(")");

        // display children
        if (!children.isEmpty()) {
            appendTabbedNewLine(sb, depth + 1);
            sb.append("# children:");
            for (Node child : children) {
                appendTabbedNewLine(sb, depth + 2);
                appendLinkPrettyPrint(sb, depth + 2, isChildShared(child.label), child);
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

    private boolean isLinkShared(int linkIndex) {
        validateLinkIndex(linkIndex);
        return (sharedLinkBitSet & (1L << linkIndex)) != 0;
    }

    private void setLinkShared(int linkIndex, boolean shared) {
        validateLinkIndex(linkIndex);
        if (shared) {
            sharedLinkBitSet |= (1L << linkIndex);
        } else {
            sharedLinkBitSet &= ~(1L << linkIndex);
        }
    }

    private void validateLinkIndex(int linkIndex) {
        if (linkIndex > 32) {
            throw new IllegalArgumentException("Link index cannot be larger than 32");
        }
    }
}
