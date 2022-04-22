package graph;

import java.util.List;

 public interface INode<T> extends Comparable<INode<T>> {
    String getLabel();

    List<INode<T>> getShortestPath();

    void setShortestPath(List<INode<T>> shortestPath);

    void setDistance(double distance);

    double getDistance();

    T getNodeData();

    void setNodeData(T nodeData);

    @Override
    default int compareTo(INode<T> node) {
        return this.getLabel().compareTo(node.getLabel());
    }
}
