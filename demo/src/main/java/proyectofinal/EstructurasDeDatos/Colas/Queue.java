package proyectofinal.EstructurasDeDatos.Colas;

public class Queue<T> implements Iterable<T> {
    private Node<T> front;
    private Node<T> rear;
    private int size;

    public Queue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("queue is empty");
        } else {
            return front.getdata();
        }
    }

    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }

    public void enqueue(T data) {
        Node<T> newNode = new Node<>(data);

        if (isEmpty()) {
            front = newNode;
            rear = newNode;
            size++;
        } else {
            rear.setNextNode(newNode);
            rear = newNode;
        }
        size++;
    }

    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("queue is empty");
        } else {
            T data = front.getdata();
            front = front.getNextNode();
            size--;
            return data;
        }
    }

    public boolean contains(T data) {
        if (isEmpty()) {
            return false;
        } else {
            Node<T> aux = front;
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
    public QueueIterator<T> iterator() {
        return new QueueIterator<T>(front);
    }
}