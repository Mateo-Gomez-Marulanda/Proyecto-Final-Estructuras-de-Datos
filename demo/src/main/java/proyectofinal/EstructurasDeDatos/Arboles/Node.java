package proyectofinal.EstructurasDeDatos.Arboles;

//ESTRUCTURA DE NODOS PARA ARBOLES BINARIOS

public class Node<T> {
    private T data;
    private Node<T> father;
    private Node<T> rightSon;
    private Node<T> leftSon;

    public Node(T data) {
        this.data = data;
        this.father = null;
        this.rightSon = null;
        this.leftSon = null;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Node<T> getFather() {
        return father;
    }

    public void setFather(Node<T> father) {
        this.father = father;
    }

    public Node<T> getRightSon() {
        return rightSon;
    }

    public void setRightSon(Node<T> rightSon) {
        this.rightSon = rightSon;
    }

    public Node<T> getLeftSon() {
        return leftSon;
    }

    public void setLeftSon(Node<T> leftSon) {
        this.leftSon = leftSon;
    }

    @Override
    public String toString() {
        return "Node [data=" + data + ", father=" + father + ", rightSon=" + rightSon + ", leftSon=" + leftSon
                + "]";
    }
}