package proyectofinal.EstructurasDeDatos.ColasDePrioridad;

// Implementación de una cola de prioridad usando estructura de heap mínimo
// Los elementos con MENOR prioridad se desencolan primero
public class PriorityQueue<T> {

    // Arreglo dinámico que almacena los nodos en estru ctura de heap
    private PriorityNode<T>[] heap;
    private int size;

    private static final int DEFAULT_CAPACITY = 10;

    // Constructor: inicializa la cola de prioridad con capacidad predeterminada
    public PriorityQueue() {
        heap = new PriorityNode[DEFAULT_CAPACITY];
        size = 0;
    }

    // Añade un elemento con su nivel de prioridad
    // Los elementos con menor valor numérico tienen mayor prioridad
    public void enqueue(T element, int priority) {
        // Redimensiona si el arreglo está lleno
        if (size == heap.length) {
            resize();
        }

        // Inserta el nuevo elemento al final
        heap[size] = new PriorityNode<T>(element, priority);
        // Sube el elemento para mantener la propiedad de heap mínimo
        siftUp(size);
        size++;
    }

    // Extrae y retorna el elemento con la mayor prioridad (menor número)
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("the priority queue is empty");
        }

        // Guarda el elemento raíz (mayor prioridad)
        T result = heap[0].getElement();

        // Mueve el último elemento a la raíz solo si hay más de un elemento
        size--;
        if (size > 0) {
            heap[0] = heap[size];
            // Restaura la propiedad de heap bajando el elemento
            siftDown(0);
        }

        // Limpia la referencia del último elemento para evitar memory leak
        heap[size] = null;

        return result;
    }

    // Retorna el elemento con mayor prioridad sin removerlo
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("the priority queue is empty");
        }
        return heap[0].getElement();
    }

    // Retorna el nivel de prioridad del elemento al frente de la cola
    public int peekPriority() {
        if (isEmpty()) {
            throw new IllegalStateException("the priority queue is empty");
        }
        return heap[0].getPriority();
    }

    // Retorna la cantidad actual de elementos en la cola
    public int size() {
        return size;
    }

    // Verifica si la cola está vacía
    public boolean isEmpty() {
        return size == 0;
    }

    // Sube un elemento en el heap para mantener la propiedad de heap mínimo
    // Se ejecuta después de insertar un elemento al final
    private void siftUp(int index) {
        // Compara con el padre y intercambia si el padre tiene mayor prioridad (número
        // mayor)
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            // Si el elemento actual tiene menor prioridad que el padre, intercambia
            if (heap[index].getPriority() < heap[parentIndex].getPriority()) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    // Baja un elemento en el heap para mantener la propiedad de heap mínimo
    // Se ejecuta después de remover la raíz
    private void siftDown(int index) {
        while (true) {
            // Índice del hijo izquierdo y derecho
            int smallest = index;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            // Si el hijo izquierdo existe y tiene menor prioridad, actualiza smallest
            if (leftChild < size && heap[leftChild].getPriority() < heap[smallest].getPriority()) {
                smallest = leftChild;
            }

            // Si el hijo derecho existe y tiene menor prioridad, actualiza smallest
            if (rightChild < size && heap[rightChild].getPriority() < heap[smallest].getPriority()) {
                smallest = rightChild;
            }

            // Si smallest no es el nodo actual, intercambia y continúa bajando
            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    // Intercambia dos elementos en el arreglo del heap
    private void swap(int index1, int index2) {
        PriorityNode<T> temp = heap[index1];
        heap[index1] = heap[index2];
        heap[index2] = temp;
    }

    // Duplica la capacidad del arreglo cuando está lleno
    private void resize() {
        PriorityNode<T>[] newHeap = new PriorityNode[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }
}
