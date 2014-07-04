package vmarcinko.nanocubes;

import vmarcinko.nanocubes.temporal.SummedTimeCountsTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Nanocube<DP> {
    private final Node root;
    private final Schema<DP> schema;
    private final TimeLabellingFn<DP> timeLabellingFn;

    public Nanocube(Schema<DP> schema, TimeLabellingFn<DP> timeLabellingFn) {
        // root node doesn't have a value
        this.root = new Node();
        this.schema = schema;
        this.timeLabellingFn = timeLabellingFn;
    }

    public Node getRoot() {
        return root;
    }

    public void insert(DP dataPoint) {
        Set<Content> updatedNodes = new HashSet<>();
        add(root, dataPoint, 0, updatedNodes);
    }

    /**
     * add nodes depth-first to a given label.  As you recurse to lower levels, construct shared links or split off to
     */
    private void add(Node root, DP dataPoint, int dimension, Set<Content> updatedNodes) {
        List<LabellingFn<DP>> chain = schema.getDimensionChain(dimension);
        List<Long> labels = calculateLabels(dataPoint, chain);
        List<Node> dimensionPathNodes = trailProperDimensionPath(root, labels);

        Node child = null;
        // start with finest level ...
        for (int i = dimensionPathNodes.size() - 1; i >= 0; i--) {
            Node pathNode = dimensionPathNodes.get(i);
            boolean update = processDimensionPathNode(pathNode, child, dimension, updatedNodes);

            if (update) {
                if (dimension == schema.getDimension() - 1) {
                    SummedTimeCountsTable summedTimeCountsTable = pathNode.getContent(SummedTimeCountsTable.class);
                    long timeLabel = timeLabellingFn.label(dataPoint);
                    summedTimeCountsTable.insert(timeLabel);

                } else {
                    add(pathNode.getContent(Node.class), dataPoint, dimension + 1, updatedNodes);
                }
                updatedNodes.add(pathNode.getContent());
            }

            child = pathNode;
        }
    }

    private boolean processDimensionPathNode(Node node, Node child, int dimension, Set<Content> updatedNodes) {
        // We have a single child node.
        if (node.getChildLinks().size() == 1) {
            node.setSharedContentWithNode(child);

        } else if (node.getContent() == null) {
            // If we have no content (this will only happen if we have no children) then we need to link to the next dimension.
            node.setContent(false, createNewContent(dimension));
            return true;

        } else if (node.isContentShared() && !updatedNodes.contains(node.getContent())) {
            // This part is tough to understand: if our content link is shared and the content node we link to is not 'updated'.
            // This happens when there has been an update downstream from our current node, but its content node, doesn't match
            // the node that's been updated.  In such a case, we need to split off and make a new content node.
            Content shallowCopy = node.getContent().shallowCopy();
            node.setContent(false, shallowCopy);

            return true;

        } else if (!node.isContentShared()) {
            // If we have a direct link to a content node, mark it as updated
            return true;
        }

        return false;
    }

    private Content createNewContent(int dimension) {
        if (dimension == schema.getDimension() - 1) {
            // if the next dimension is time, then we need to make a new time series table
            return new SummedTimeCountsTable();
        } else {
            // Otherwise we need to make a new node in the next dimension (dimension root)
            return new Node();
        }
    }

    private List<Node> trailProperDimensionPath(Node root, List<Long> labels) {
        List<Node> stack = new ArrayList<>();
        stack.add(root);

        Node node = root;
        for (Long label : labels) {
            Node child = getOrCreateProperChildNode(node, label);
            stack.add(child);
            node = child;
        }

        return stack;
    }

    /**
     * Builds a path of nodes to the finest level in a chain creating new nodes with shared links when necessary
     */
    private Node getOrCreateProperChildNode(Node node, Long label) {
        Link labelChildLink = node.getChildLinks().get(label);
        if (labelChildLink == null) {
            return node.newProperChild(label);

        } else if (labelChildLink.isShared()) {
            Node child = (Node) labelChildLink.getTarget().shallowCopy();
            node.getChildLinks().put(label, new Link<>(false, child));
            return child;

        } else {
            return (Node) labelChildLink.getTarget();
        }
    }

    private List<Long> calculateLabels(DP dataPoint, List<LabellingFn<DP>> chain) {
        List<Long> labels = new ArrayList<>(chain.size());
        for (LabellingFn<DP> labellingFn : chain) {
            labels.add(labellingFn.label(dataPoint));
        }
        return labels;
    }

    public String toPrettyString() {
        StringBuilder sb = new StringBuilder();
        root.appendPrettyPrint(sb, 0);
        return sb.toString();
    }
}
