package proyectofinal.EstructurasDeDatos.Pilas;

public class Stack<T> implements Iterable<T> {
    private Node<T> top;
    private int size;

    public Stack() {
        this.top = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("stack is empty");
        } else {
            return top.getdata();
        }
    }

    public void clear() {
        top = null;
        size = 0;
    }

    public void push(T data) {
        Node<T> newNode = new Node<T>(data);

        if (isEmpty()) {
            top = newNode;
        } else {
            newNode.setNextNode(top);
            top = newNode;
        }
        size++;
    }

    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("stack is empty");
        } else {
            T data = top.getdata();
            top = top.getNextNode();
            size--;
            return data;
        }
    }

    public boolean contains(T data) {
        if (isEmpty()) {
            return false;
        } else {
            Node<T> aux = top;
            while (aux != null) {
                if (aux.getdata().equals(data)) {
                    return true;
                }
                aux = aux.getNextNode();
            }
            return false;
        }
    }

    @Override
    public StackIterator<T> iterator() {
        return new StackIterator<T>(top);
    }
}