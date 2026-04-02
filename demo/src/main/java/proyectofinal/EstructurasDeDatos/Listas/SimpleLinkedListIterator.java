package proyectofinal.EstructurasDeDatos.Listas;

import java.util.Iterator;

public class SimpleLinkedListIterator<T> implements Iterator<T> {
    private Node<T> aux;

    public SimpleLinkedListIterator(Node<T> aux) {
        this.aux = aux;
    }

    @Override
    public boolean hasNext() {
        return aux != null;
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new java.util.NoSuchElementException();
        }
        T data = aux.getdata();
        aux = aux.getNextNode();
        return data;
    }
}