package proyectofinal.EstructurasDeDatos.TablasHash;

public class Node<K, V> {
    private K key;
    private V value;
    private Node<K, V> next;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
        this.next = null;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Node<K, V> getNext() {
        return next;
    }

    public void setNext(Node<K, V> next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "Node [key=" + key + ", value=" + value + ", next=" + next + "]";
    }

    /*
     * clases que deben implementar los metodos equals y hashcode, para que se
     * puedan comparar los objetos de la clase Node, esto para los objetos que seran
     * almacenados en la tabla hash.
     * 
     * - cliente: para buscar por id
     * - inmueble: para buscar por id
     * - asesor: para buscar por id o nombre
     * - permitir busqueda por visitas a inmuble o zona (toca crear un contenerdor
     * de zonas)
     * - agrupar propiedad por zona, tipo o estado (falta verificar si "propiedad"
     * es una clase o un atributo de inmueble)
     */
}