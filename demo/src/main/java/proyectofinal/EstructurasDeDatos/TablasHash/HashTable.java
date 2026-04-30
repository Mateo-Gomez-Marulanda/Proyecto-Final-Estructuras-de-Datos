package proyectofinal.EstructurasDeDatos.TablasHash;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;

public class HashTable<K, V> {
    private Node<K, V>[] table;
    private int size;
    private int capacity;
    private double LOADFACTOR;

    public HashTable(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Invalid Capacity ");
        }

        this.capacity = capacity;
        this.LOADFACTOR = 0.75;
        this.size = 0;
        this.table = new Node[capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public void clear() {
        table = new Node[capacity];
        size = 0;
    }

    private int calculateIndex(K key) {
        // Obtiene el código hash de la clave
        int hash = key.hashCode();

        // Calcula el módulo para obtener un índice en el rango de la tabla
        // Puede ser negativo si el hashCode es negativo
        int index = hash % capacity;

        // Si el índice es negativo, suma capacity para convertirlo a un índice positivo
        // válido
        // Si ya es positivo, retorna el índice sin cambios

        return index < 0 ? index + capacity : index;
    }

    /**
     * Metodo para redimensionar la tabla hash cuando se alcanza el factor de carga,
     * se crea una nueva tabla con el doble de capacidad y se rehashan los elementos
     * de la tabla original a la nueva tabla, esto se hace para mantener un buen
     * rendimiento en las operaciones de insercion, busqueda y eliminacion, ya que
     * una tabla hash con un factor de carga alto puede generar colisiones y
     * disminuir el rendimiento de las operaciones.
     */

    private void resize() {
        capacity *= 2; // se aumenta la capacidad al doble (norma general para tablas hash)
        Node<K, V>[] newTable = new Node[capacity]; // nueva tabla con la nueva capacidad

        for (Node<K, V> node : table) {
            while (node != null) {
                // se calcula el indice apartir del primer nodo de la antiguia tabla
                int index = calculateIndex(node.getKey());

                // se crea un nuevo nodo con la misma clave y valor del nodo actual
                Node<K, V> newNode = new Node<>(node.getKey(), node.getValue());

                if (newTable[index] == null) {
                    newTable[index] = newNode; // se agrega el nuevo nodo si la posicion esta vacia
                } else {
                    Node<K, V> current = newTable[index]; // referencia al nodo actual de la nueva tabla

                    // se recorre la lista enlazada de la nueva tabla hasta encontrar el ultimo
                    // nodo, y se agrega el nuevo nodo al final de la lista
                    while (current.getNext() != null) {
                        current = current.getNext();
                    }
                    current.setNext(newNode);
                }
                node = node.getNext();
            }
        }
        table = newTable; // se asigna la nueva tabla a la tabla original
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = calculateIndex(key);
        Node<K, V> newNode = new Node<>(key, value);

        if (table[index] == null) {
            table[index] = newNode; // se agrega el nuevo nodo si la posicion esta vacia
            size++;
        } else { // si la posicion no esta vacia, se corre la tabla
            Node<K, V> current = table[index]; // referencia al nodo actual de la tabla
            while (current != null) {
                if (current.getKey().equals(key)) { // se verifica si la clave ya existe en la tabla
                    current.setValue(value); // si exite se actualiza el valor de la clave
                    return;
                }
                if (current.getNext() == null) {
                    break;
                }
                current = current.getNext();
            }
            current.setNext(newNode); // si no existe la clave entoces se agrega al final
            size++;
        }
        // se verifica si se alcanzo el factor de carga, si es asi se redimensiona la
        // tabla
        if ((double) size / capacity >= LOADFACTOR) {
            resize();
        }
    }

    /**
     * metodo para eliminar un elemento de la tabla hash a partir de la clave, se
     * busca el nodo con la clave especificada, si se encuentra se elimina de la
     * lista enlazada y se devuelve el valor asociado a la clave, si no se encuentra
     * se devuelve null
     * 
     * @param key
     * @return
     */
    public V remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = calculateIndex(key);
        Node<K, V> current = table[index]; // referencia al nodo actual de la tabla
        Node<K, V> aux = null; // referencia al nodo anteior al actual (previous)

        while (current != null) {

            if (current.getKey().equals(key)) {

                // se elimina el nodo actual que hace referencia al primer nodo solo si el nodo
                // auxiliar es null
                if (aux == null) {
                    table[index] = current.getNext();
                } else {
                    aux.setNext(current.getNext()); // se elimina el nodo actual (intermedio o ultimo)
                }

                current.setNext(null); // permite eliminar nodos de memoria

                size--;
                return current.getValue(); // retorna el valor asociado a la clave eliminada
            }

            aux = current;
            current = current.getNext();
        }

        return null; // si no encuentra la clave se devuelve null
    }

    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = calculateIndex(key);

        Node<K, V> current = table[index]; // referencia al nodo actual de la tabla
        while (current != null) {
            if (current.getKey().equals(key)) {
                return current.getValue();
            }

            current = current.getNext();
        }

        return null;
    }

    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        int index = calculateIndex(key);
        Node<K, V> current = table[index];

        while (current != null) {
            if (current.getKey().equals(key)) {
                return true;
            }
            current = current.getNext();
        }

        return false;
    }

    public boolean containsValue(V value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        for (Node<K, V> node : table) { // recorre cada indice de la tabla

            Node<K, V> current = node; // accede al primer nodo de dicho indice
            while (current != null) {
                if (current.getValue().equals(value)) {
                    return true;
                }

                current = current.getNext();
            }
        }

        return false;
    }

    // retorna una lista de todas las claves de la tabla
    public SimpleLinkedList<K> keySet() {
        SimpleLinkedList<K> keys = new SimpleLinkedList<>();

        for (int i = 0; i < table.length; i++) {
            Node<K, V> current = table[i]; // referencia al primer elemento del bucket

            while (current != null) { // recorrer todo el bucket
                keys.addLast(current.getKey()); // se agrega a la lista de claves
                current = current.getNext(); // se pasa a la siguiente posicion
            }
        }
        return keys; // retornar la lista de claves
    }

    // retorna una lista de todos los valores de la tabla
    public SimpleLinkedList<V> values() {
        SimpleLinkedList<V> values = new SimpleLinkedList<>();

        for (int i = 0; i < table.length; i++) {
            Node<K, V> current = table[i]; // referencia al primer elemento del bucket

            while (current != null) { // recorrer todo el bucket
                values.addLast(current.getValue()); // se agrega a la lista de valores
                current = current.getNext(); // se pasa a la siguiente posicion
            }
        }
        return values; // retornar la lista de valores
    }

    // retorna una lista de todos los pares de la tabla
    public SimpleLinkedList<Node<K, V>> entrySet() {
        SimpleLinkedList<Node<K, V>> pairs = new SimpleLinkedList<>();

        for (int i = 0; i < table.length; i++) {
            Node<K, V> current = table[i]; // referencia al primer elemento del bucket

            while (current != null) { // recorrer todo el bucket
                pairs.addLast(current); // se agrega a la lista de pares
                current = current.getNext(); // se pasa a la siguiente posicion
            }
        }
        return pairs; // retornar la lista de pares
    }
}