package proyectofinal.EstructurasDeDatos.Arboles;

//ESTRUCTURA DE NODOS PARA ARBOLES BINARIOS

public class Node<T> {
    private T data;
    private Node<T> father; // se usa una referencia al padre para facilitar la eliminación de nodos
    private Node<T> right;
    private Node<T> left;

    public Node(T data) {
        this.data = data;
        this.father = null;
        this.right = null;
        this.left = null;
    }

    public T getValue() {
        return data;
    }

    public void setValue(T data) {
        this.data = data;
    }

    public Node<T> getFather() {
        return father;
    }

    public void setFather(Node<T> father) {
        this.father = father;
    }

    public Node<T> getRight() {
        return right;
    }

    public void setRight(Node<T> right) {
        this.right = right;
    }

    public Node<T> getLeft() {
        return left;
    }

    public void setLeft(Node<T> left) {
        this.left = left;
    }

    @Override
    public String toString() {
        return "Node [data=" + data + ", father=" + father + ", right=" + right + ", left=" + left
                + "]";
    }
}