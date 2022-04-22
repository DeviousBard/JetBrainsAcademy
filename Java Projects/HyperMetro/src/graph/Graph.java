package graph;

import java.util.*;

public class Graph<T> implements IGraph<T> {

    private final Map<String, INode<T>> nodesByLabel = new HashMap<>();
    private final Map<INode<T>, List<IEdge<T>>> edgeMap = new HashMap<>();

    public Graph() {
    }

    @Override
    public void addNode(String label) {
        this.addNode(this.getNodeByLabel(label));
    }

    @Override
    public void addNode(INode<T> node) {
        this.getEdgeMap().putIfAbsent(node, new ArrayList<>());
    }

    @Override
    public void removeNode(String label) {
        this.removeNode(this.getNodeByLabel(label));
    }

    @Override
    public void removeNode(INode<T> node) {
        this.getEdgeMap().remove(node);
        this.removeNodeFromEdges(node);
    }

    private void removeNodeFromEdges(INode<T> node) {
        this.getEdgeMap().values().forEach(el -> el.removeIf(e -> e.hasNode(node)));
    }

    @Override
    public void addEdge(String label1, String label2) {
        this.addEdge(this.getNodeByLabel(label1), this.getNodeByLabel(label2));
    }

    @Override
    public void addEdge(INode<T> node1, INode<T> node2) {
        this.addWeightedEdge(node1, node2, 1.0d);
    }

    @Override
    public void addWeightedEdge(String label1, String label2, double weight) {
        this.addWeightedEdge(this.getNodeByLabel(label1), this.getNodeByLabel(label2), weight);
    }

    @Override
    public void addWeightedEdge(INode<T> source, INode<T> target, double weight) {
        IEdge<T> edge1 = new Edge<>(source, target, weight);
        List<IEdge<T>> edges1 = this.getEdgeMap().getOrDefault(source, new ArrayList<>());
        this.getEdgeMap().put(source, edges1);
        if (edges1 != null) {
            Set<IEdge<T>> e1Set = new HashSet<>(edges1);
            if (!e1Set.contains(edge1)) {
                edges1.add(edge1);
            }
        }
        IEdge<T> edge2 = new Edge<>(target, source, weight);
        List<IEdge<T>> edges2 = this.getEdgeMap().getOrDefault(target, new ArrayList<>());
        this.getEdgeMap().put(target, edges2);
        if (edges2 != null) {
            Set<IEdge<T>> e2Set = new HashSet<>(edges2);
            if (!e2Set.contains(edge2)) {
                edges2.add(edge2);
            }
        }
    }

    @Override
    public List<IEdge<T>> getAdjacentNodes(String label) {
        return this.getAdjacentNodes(this.getNodeByLabel(label));
    }

    @Override
    public List<IEdge<T>> getAdjacentNodes(INode<T> node) {
        return this.getEdgeMap().getOrDefault(node, new ArrayList<>());
    }

    @Override
    public Map<INode<T>, List<IEdge<T>>> getEdgeMap() {
        return this.edgeMap;
    }

    @Override
    public Set<INode<T>> getNodes() {
        return new HashSet<>(this.nodesByLabel.values());
    }

    public INode<T> getNodeByLabel(String label) {
        return this.getNodeByLabel(label, true);
    }

    public INode<T> getNodeByLabel(String label, boolean createIfNotFound) {
        INode<T> node = this.nodesByLabel.get(label);
        if (node == null && createIfNotFound) {
            this.nodesByLabel.put(label, new Node<>(label));
            node = this.nodesByLabel.get(label);
        }
        return node;
    }

    @Override
    public void calculateShortestPathFromSource(String sourceLabel, boolean unweighted) {
        this.calculateShortestPathFromSource(this.getNodeByLabel(sourceLabel), unweighted);
    }

    @Override
    public void calculateShortestPathFromSource(INode<T> sourceNode, boolean unweighted) {
        this.clearShortestPaths();
        sourceNode.setDistance(0);
        Set<INode<T>> settledNodes = new HashSet<>();
        Set<INode<T>> unsettledNodes = new HashSet<>();
        unsettledNodes.add(sourceNode);

        while (unsettledNodes.size() != 0) {
            INode<T> currentNode = getShortestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (IEdge<T> edge : this.getAdjacentNodes(currentNode)) {
                double edgeWeight = unweighted ? 1.0d : edge.getWeight();
                INode<T> n = this.getNodeByLabel(edge.getTarget().getLabel());
                if (!settledNodes.contains(n)) {
                    calculateMinimumDistance(n, edgeWeight, currentNode);
                    unsettledNodes.add(n);
                }
            }
            settledNodes.add(currentNode);
        }
        for (INode<T> node : this.getNodes()) {
            node.getShortestPath().add(node);
        }
    }

    private void clearShortestPaths() {
        for (INode<T> node : this.getNodes()) {
            node.setShortestPath(new ArrayList<>());
            node.setDistance(Double.MAX_VALUE);
        }
    }

    private INode<T> getShortestDistanceNode(Set<INode<T>> unsettledNodes) {
        INode<T> shortestDistanceNode = null;
        double shortestDistance = Double.MAX_VALUE;
        for (INode<T> node : unsettledNodes) {
            double nodeDistance = node.getDistance();
            if (nodeDistance < shortestDistance) {
                shortestDistance = nodeDistance;
                shortestDistanceNode = node;
            }
        }
        return shortestDistanceNode;
    }

    private void calculateMinimumDistance(INode<T> evaluationNode, double edgeWeigh, INode<T> sourceNode) {
        double sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            List<INode<T>> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }
}
