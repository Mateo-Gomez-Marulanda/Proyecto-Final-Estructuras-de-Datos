package proyectofinal.EstructurasDeDatos.Pilas;

import java.util.Iterator;

public class StackIterator<T> implements Iterator<T> {
        private Node<T> aux;

    public StackIterator(Node<T> aux) {
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
