package graph;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IGraph<T> {

    void addNode(String label);

    void addNode(INode<T> node);

    void removeNode(String label);

    void removeNode(INode<T> node);

    void addEdge(String label1, String label2);

    void addEdge(INode<T> node1, INode<T> node2);

    void addWeightedEdge(String label1, String label2, double weight);

    void addWeightedEdge(INode<T> node1, INode<T> node2, double weight);

    List<IEdge<T>> getAdjacentNodes(String label);

    List<IEdge<T>> getAdjacentNodes(INode<T> node);

    Map<INode<T>, List<IEdge<T>>> getEdgeMap();

    Set<INode<T>> getNodes();

    INode<T> getNodeByLabel(String label);

    INode<T> getNodeByLabel(String label, boolean createIfNotFound);

    void calculateShortestPathFromSource(String sourceLabel, boolean unweighted);

    void calculateShortestPathFromSource(INode<T> sourceNode, boolean unweighted);
}
