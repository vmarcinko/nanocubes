package vmarcinko.nanocubes;

import vmarcinko.nanocubes.temporal.TemporalTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NanocubeFactory {
    private NanocubeFactory() {
    }

    public static <DP> Node createNanocube(List<DP> dataPoints, Schema<DP> schema, LabellingFn<DP> timeLabellingFn) {
        Node nanocube = new Node();
        for (DP dataPoint : dataPoints) {
            Set<Content> updatedNodes = new HashSet<>();
            add(nanocube, dataPoint, 0, schema, timeLabellingFn, updatedNodes);
        }
        return nanocube;
    }

    /**
     * add nodes depth-first to a given label.  As you recurse to lower levels, construct shared links or split off to
     */
    private static <DP> void add(Node root, DP dataPoint, int dimension, Schema<DP> schema, LabellingFn<DP> timeLabellingFn, Set<Content> updatedNodes) {
        List<LabellingFn<DP>> chain = schema.getDimensionChain(dimension);
        List<Object> labels = calculateLabels(dataPoint, chain);
        List<Node> dimensionPathNodes = trailProperDimensionPath(root, labels);

        Node child = null;
        // start with finest level ...
        for (int i = dimensionPathNodes.size() - 1; i >= 0; i--) {
            Node pathNode = dimensionPathNodes.get(i);
            boolean update = processDimensionPathNode(pathNode, child, dimension, schema, updatedNodes);

            if (update) {
                if (dimension == schema.getDimension() - 1) {
                    TemporalTable temporalTable = (TemporalTable) pathNode.getContentLink().getTarget();
                    Long timeLabel = (Long) timeLabellingFn.label(dataPoint);
                    temporalTable.registerEvent(timeLabel);

                } else {
                    add((Node) pathNode.getContentLink().getTarget(), dataPoint, dimension + 1, schema, timeLabellingFn, updatedNodes);
                }
                updatedNodes.add(pathNode.getContentLink().getTarget());
            }

            child = pathNode;
        }
    }

    private static <DP> boolean processDimensionPathNode(Node node, Node child, int dimension, Schema<DP> schema, Set<Content> updatedNodes) {
        // We have a single child node.
        if (node.getChildLinks().size() == 1) {
            node.setSharedContent(child);

        } else if (node.getContentLink() == null) {
            // If we have no content (this will only happen if we have no children) then we need to link to the next dimension.
            node.setContentLink(new Link<>(false, createNewContent(schema, dimension)));
            return true;

        } else if (node.getContentLink().isShared() && !updatedNodes.contains(node.getContentLink().getTarget())) {
            // This part is tough to understand: if our content link is shared and the content node we link to is not 'updated'.
            // This happens when there has been an update downstream from our current node, but its content node, doesn't match
            // the node that's been updated.  In such a case, we need to split off and make a new content node.
            Content shallowCopy = node.getContentLink().getTarget().shallowCopy();
            node.setContentLink(new Link<>(false, shallowCopy));

            return true;

        } else if (!node.getContentLink().isShared()) {
            // If we have a direct link to a content node, mark it as updated
            return true;
        }

        return false;
    }

    private static <DP> Content createNewContent(Schema<DP> schema, int dimension) {
        if (dimension == schema.getDimension() - 1) {
            // if the next dimension is time, then we need to make a new time series table
            return new TemporalTable();
        } else {
            // Otherwise we need to make a new node in the next dimension
            return new Node();
        }
    }

    private static List<Node> trailProperDimensionPath(Node root, List<Object> labels) {
        List<Node> stack = new ArrayList<>();
        stack.add(root);

        Node node = root;
        for (Object label : labels) {
            Node child = getOrCreateProperChildNode(node, label);
            stack.add(child);
            node = child;
        }

        return stack;
    }

    /**
     * Builds a path of nodes to the finest level in a chain creating new nodes with shared links when necessary
     */
    private static Node getOrCreateProperChildNode(Node node, Object label) {
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

    private static <DP> List<Object> calculateLabels(DP dataPoint, List<LabellingFn<DP>> chain) {
        List<Object> labels = new ArrayList<>(chain.size());
        for (LabellingFn<DP> labellingFn : chain) {
            labels.add(labellingFn.label(dataPoint));
        }
        return labels;
    }
}
