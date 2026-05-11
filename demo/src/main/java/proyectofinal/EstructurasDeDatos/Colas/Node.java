package proyectofinal.EstructurasDeDatos.Colas;

public class Node<T> {
    private Node<T> nextNode;
    private T data;

    public Node(T data) {
        this.nextNode = null;
        this.data = data;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }

    public T getdata() {
        return data;
    }

    public void setdata(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Node [nextNode=" + nextNode + ", data=" + data + "]";
    }
}