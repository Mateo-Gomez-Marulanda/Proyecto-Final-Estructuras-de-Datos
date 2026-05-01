package proyectofinal.EstructurasDeDatos.ColasDePrioridad;

public class PriorityNode<T> {
    private T element;
    private int priority;

    public PriorityNode(T element, int priority) {
        this.element = element;
        this.priority = priority;
    }

    public T getElement() {
        return element;
    }

    public void setElement(T element) {
        this.element = element;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "PriorityNode [element=" + element + ", priority=" + priority + "]";
    }
}