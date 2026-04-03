package proyectofinal.EstructurasDeDatos.Listas;

import java.util.Iterator;

public class SimpleLinkedList<T> implements Iterable<T> {
    private Node<T> first;
    private int size;

    public SimpleLinkedList() {
        this.first = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public boolean validIndex(int index) {
        if (index < 0 || index > size) {
            return false;
        }
        return true;
    }

    public void clearList() {
        first = null;
        size = 0;
    }

    public void addFirst(T data) {
        Node<T> newNode = new Node<>(data);

        if (isEmpty()) {
            first = newNode;
        } else {
            newNode.setNextNode(first);
            first = newNode;
        }
        size++;
    }

    public void addLast(T data) {
        Node<T> newNode = new Node<>(data);

        if (isEmpty()) {
            first = newNode;
        } else {
            Node<T> aux = first;
            while (aux.getNextNode() != null) {
                aux = aux.getNextNode();
            }
            aux.setNextNode(newNode);

        }
        size++;
    }

    public void add(T data, int index) {
        if (index < 0 || index > size) {
            throw new RuntimeException("error index");
        }

        if (index == 0) {
            addFirst(data);
            return;
        }
        if (index == size) {
            addLast(data);
            return;
        }

        Node<T> aux = first;
        for (int i = 0; i < index - 1; i++) {
            aux = aux.getNextNode();
        }

        Node<T> next = new Node<>(data);
        next.setNextNode(aux.getNextNode());
        aux.setNextNode(next);

        size++;
    }

    public void removeFirst() {
        if (isEmpty()) {
            throw new RuntimeException("list is empty");
        } else {
            Node<T> next = first.getNextNode(); // obtiene el siguiente nodo al primero
            first.setdata(null); // cambia el primer nodo a null
            first = next; // el primer nodo es ahora el siguiente
            size--;
        }
    }

    public void removeLast() {
        if (isEmpty()) {
            throw new RuntimeException("list is empty");
        }

        if (size == 1) {
            first = null;
        } else {
            Node<T> aux = first;
            while (aux.getNextNode().getNextNode() != null) {
                aux = aux.getNextNode();
            }
            aux.setNextNode(null);
        }
        size--;
    }

    // elimina en base al indice
    public void removeIndex(int index) {
        if (!validIndex(index)) {
            throw new RuntimeException("error index");
        }

        if (isEmpty()) {
            throw new RuntimeException("list is empty");
        } else {
            if (index == 0) {
                removeFirst();
                return;
            }
            if (index == size - 1) {
                removeLast();
                return;
            }
        }

        Node<T> aux = first;
        for (int i = 1; i < index - 1; i++) {
            aux = aux.getNextNode();
        }

        aux.setNextNode(aux.getNextNode().getNextNode());
        size--;
    }

    // eliminar en base al dato
    public void removeElement(T data) {
        if (isEmpty()) {
            throw new RuntimeException("list is empty");
        }

        if (first.getdata().equals(data)) {
            removeFirst();
            return;
        }

        Node<T> aux = first;
        while (aux != null) {
            if (aux.getNextNode().getdata().equals(data)) {
                aux.setNextNode(aux.getNextNode().getNextNode());
                size--;
                return;
            }
            aux = aux.getNextNode();
        }
        throw new RuntimeException("Element not found: " + data);
    }

    // OBTIENE EL DATO SEGUN EL INDICE
    public T get(int index) {
        if (isEmpty()) {
            throw new RuntimeException("list is empty");
        }

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        Node<T> aux = first;
        for (int i = 0; i < index; i++) {
            aux = aux.getNextNode();
        }
        return aux.getdata();
    }

    // OBTIENE EL INDICE SEGUN EL DATO
    public int indexOf(T data) {

        Node<T> aux = first;
        int cont = 0;
        while (aux != null) {
            if (aux.getdata().equals(data)) {
                return cont;
            }
            aux = aux.getNextNode();
            cont++;
        }
        return -1;
    }

    // MODIFICA EL NODO SEGUN EL INDICE
    public void modifyNode(int index, T newData) {
        if (!validIndex(index) || index == size) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }

        if (isEmpty()) {
            throw new RuntimeException("list is empty");
        }

        Node<T> aux = first;
        for (int i = 0; i < index; i++) {
            aux = aux.getNextNode();
        }
        aux.setdata(newData);
    }

    // ITERADOR PROPIO PARA PODER USAR FOR-EACH O DEMAS METODOS ITERABLE
    @Override
    public Iterator<T> iterator() {
        return new SimpleLinkedListIterator<T>(first);
    }
}