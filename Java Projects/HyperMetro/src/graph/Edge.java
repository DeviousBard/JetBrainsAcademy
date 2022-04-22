package graph;

public class Edge<T> implements IEdge<T> {

    private final INode<T> source;
    private final INode<T> target;
    private final double weight;

    public Edge(INode<T> source, INode<T> target) {
        this(source, target, 1.0d);
    }

    public Edge(INode<T> source, INode<T> target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public double getWeight() {
        return this.weight;
    }

    @Override
    public INode<T> getSource() {
        return this.source;
    }

    @Override
    public INode<T> getTarget() {
        return this.target;
    }

    @Override
    public boolean hasSource(INode<T> node) {
        return this.source.equals(node);
    }

    @Override
    public boolean hasTarget(INode<T> node) {
        return this.target.equals(node);
    }

    @Override
    public boolean hasNode(INode<T> node) {
        return this.hasSource(node) || this.hasTarget(node);
    }

    @Override
    public int hashCode() {
        return (this.source.getLabel() + "|" + this.target.getLabel()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IEdge) {
            @SuppressWarnings("unchecked")
            IEdge<T> e = (IEdge<T>)obj;
            return this.source.equals(e.getSource()) && this.target.equals(e.getTarget());
        }
        return false;
    }
}
