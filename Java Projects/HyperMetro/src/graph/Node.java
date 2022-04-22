package graph;

import java.util.LinkedList;
import java.util.List;

public class Node<T> implements INode<T> {
    private final String label;
    private T nodeData;

    private List<INode<T>> shortestPath = new LinkedList<>();

    private double distance = Double.MAX_VALUE;

    public Node(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int hashCode() {
        return this.getLabel().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof INode) {
            @SuppressWarnings("unchecked")
            INode<T> node = (INode<T>) obj;
            return this.getLabel().equals(node.getLabel());
        }
        return false;
    }

    @Override
    public List<INode<T>> getShortestPath() {
        return this.shortestPath;
    }

    @Override
    public void setShortestPath(List<INode<T>> shortestPath) {
        this.shortestPath = shortestPath;
    }

    @Override
    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }

    public T getNodeData() {
        return this.nodeData;
    }

    public void setNodeData(T nodeData) {
        this.nodeData = nodeData;
    }
}
