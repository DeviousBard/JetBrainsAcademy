package graph;

public interface IEdge<T> {
    double getWeight();

    INode<T> getSource();

    INode<T> getTarget();

    boolean hasSource(INode<T> node);

    boolean hasTarget(INode<T> node);

    boolean hasNode(INode<T> node);
}
