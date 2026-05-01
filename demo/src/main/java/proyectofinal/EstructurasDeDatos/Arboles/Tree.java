package proyectofinal.EstructurasDeDatos.Arboles;

import java.util.LinkedList;
import java.util.Queue;

public class Tree<T extends Comparable<T>> {
    private Node<T> root;
    private int size;
    private boolean encontrado; // Para rastrear si se encontró un nodo en remove

    public Tree() {
        this.root = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public Node<T> root() {
        return root;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean put(T data) {
        if (data == null) {
            throw new IllegalArgumentException("The data cannot be null");
        }
        if (root == null) {
            root = new Node<>(data);
            size++;
            return true;
        } else {
            return putR(data, root);
        }
    }

    // metodo recursivo para agregar un nodo - retorna true si se insertó, false si
    // ya existe
    private boolean putR(T data, Node<T> current) {

        if (data.equals(current.getValue())) { // condicion para no aceptar repetidos
            return false; // Ya existe, no se agregó

        } else if (data.compareTo(current.getValue()) < 0) {
            // Si data es MENOR que current, va a la IZQUIERDA
            if (current.getLeft() == null) {

                Node<T> newNode = new Node<>(data);
                current.setLeft(newNode); // se asigna el nuevo nodo a la izquierda del nodo actual
                size++;
                return true; // Se agregó exitosamente
            } else {
                // llamado recursivo para seguir buscando el lugar correcto en el subarbol
                // izquierdo
                return putR(data, current.getLeft());
            }
        } else {
            // Si data es MAYOR que current, va a la DERECHA
            if (current.getRight() == null) {
                Node<T> newNode = new Node<>(data);
                current.setRight(newNode); // se asigna el nuevo nodo a la derecha del nodo actual
                size++;
                return true; // Se agregó exitosamente
            } else {
                // llamado recursivo para seguir buscando el lugar correcto en el subarbol
                // derecho
                return putR(data, current.getRight());
            }
        }
    }

    public boolean remove(T data) {
        if (data == null) {
            throw new IllegalArgumentException("The data cannot be null");
        }
        encontrado = false;
        root = removeR(root, data);
        return encontrado; // Retorna true si se eliminó, false si no existía
    }

    // metodo recursivo para eliminar un nodo del árbol
    private Node<T> removeR(Node<T> current, T data) {
        if (current == null) {
            return null; // Valor no encontrado
        }

        int comparacion = data.compareTo(current.getValue());

        if (comparacion < 0) {
            // El valor está en el subárbol izquierdo
            current.setLeft(removeR(current.getLeft(), data));
        } else if (comparacion > 0) {
            // El valor está en el subárbol derecho
            current.setRight(removeR(current.getRight(), data));
        } else {
            // Nodo encontrado
            encontrado = true; // Marcar que se encontró el nodo

            // Caso 1: Nodo sin hijos (hoja)
            if (current.getLeft() == null && current.getRight() == null) {
                size--;
                return null;
            }

            // Caso 2: Nodo con solo hijo derecho
            if (current.getLeft() == null) {
                size--;
                return current.getRight();
            }

            // Caso 3: Nodo con solo hijo izquierdo
            if (current.getRight() == null) {
                size--;
                return current.getLeft();
            }

            // Caso 4: Nodo con dos hijos
            // Encontrar el nodo más pequeño del subárbol derecho (sucesor inorden)
            Node<T> minRight = findMin(current.getRight());
            // Reemplazar el valor del nodo actual con el valor del sucesor
            current.setValue(minRight.getValue());
            // Eliminar el nodo sucesor del subárbol derecho
            current.setRight(removeR(current.getRight(), minRight.getValue()));
        }

        return current;
    }

    // metodo auxiliar para encontrar el nodo con el valor mínimo en un subárbol
    private Node<T> findMin(Node<T> current) {
        while (current.getLeft() != null) {
            current = current.getLeft();
        }
        return current;
    }

    public boolean binarySearch(T data) {
        if (data == null) {
            throw new IllegalArgumentException("The data cannot be null");
        }
        return binarySearchR(root, data); // se llama al metodo recursivo
    }
    public void weight(){
        if (root == null) {
            System.out.println("El árbol está vacío");
        } else {
            int weight = weightR(root);
            System.out.println("El peso del árbol es: " + weight);
        }
    }

    private int weightR(Node<T> current) {
        if (current == null) {
            return 0;
        }
        int leftWeight = weightR(current.getLeft());
        int rightWeight = weightR(current.getRight());
        return leftWeight + rightWeight + 1;
    }

    public void height() {
        if (root == null) {
            System.out.println("El árbol está vacío");
        } else {
            int height = heightR(root);
            System.out.println("La altura del árbol es: " + height);
        }
    }

    private int heightR(Node<T> current) {
        if (current == null) {
            // Altura de un árbol vacío es -1
            return -1; 
        }
        int leftHeight = heightR(current.getLeft());
        int rightHeight = heightR(current.getRight());
        // Altura del nodo actual
        return Math.max(leftHeight, rightHeight) + 1; 
    }

    public void levels() {
        if (root == null) {
            System.out.println("El árbol está vacío");
        } else {
            int levels = levelsR(root);
            System.out.println("El número de niveles del árbol es: " + levels);
        }
    }

    private int levelsR(Node<T> current) {
        if (current == null) {
            return 0; // Un árbol vacío tiene 0 niveles
        }
        int leftLevels = levelsR(current.getLeft());
        int rightLevels = levelsR(current.getRight());
        // El número de niveles es el máximo entre los niveles de los subárboles + 1 para el nodo actual
        return Math.max(leftLevels, rightLevels) + 1; 
    }

    // metodo recursivo para buscar un valor en el arbol
    private boolean binarySearchR(Node<T> current, T data) {
        if (current == null) {
            return false;
        }

        // se guarda el valor de la comparacion con el fin de poder comparar el tamaño
        // de los datos sin importar su tipo
        int comparacion = current.getValue().compareTo(data);

        if (comparacion == 0) {
            return true; // Encontrado
        } else if (comparacion > 0) {
            return binarySearchR(current.getLeft(), data); // Izquierda: valores menores
        } else {
            return binarySearchR(current.getRight(), data); // Derecha: valores mayores
        }
    }

    // Metodos de recorrido en profundidad: preorden, inorden y postorden
    public String inOrder() {
        if (root == null) {
            return null; // Árbol vacío
        } else {
            StringBuilder sb = new StringBuilder();
            inOrderR(root, sb);
            return sb.toString().trim();
        }
    }

    private void inOrderR(Node<T> current, StringBuilder sb) {
        if (current != null) {
            inOrderR(current.getLeft(), sb); // Recorre el subárbol izquierdo
            sb.append(current.getValue()).append(" "); // Agrega el valor del nodo actual
            inOrderR(current.getRight(), sb); // Recorre el subárbol derecho
        }
    }

    public String preOrder() {
        if (root == null) {
            return null; // Árbol vacío
        } else {
            StringBuilder sb = new StringBuilder();
            preOrderR(root, sb);
            return sb.toString().trim();
        }
    }

    private void preOrderR(Node<T> current, StringBuilder sb) {
        if (current != null) {
            sb.append(current.getValue()).append(" "); // Agrega el valor del nodo actual
            preOrderR(current.getLeft(), sb); // subarbol izquierdo
            preOrderR(current.getRight(), sb); // subarbol derecho
        }
    }

    public String postOrder() {
        if (root == null) {
            return null; // Árbol vacío
        } else {
            StringBuilder sb = new StringBuilder();
            postOrderR(root, sb);
            return sb.toString().trim();
        }
    }

    private void postOrderR(Node<T> current, StringBuilder sb) {
        if (current != null) {
            postOrderR(current.getLeft(), sb); // subarbol izquierdo
            postOrderR(current.getRight(), sb); // subarbol derecho
            sb.append(current.getValue()).append(" "); // Agrega el valor del nodo actual
        }
    }

    public String levelOrder() {
        if (isEmpty())
            return null; // Árbol vacío

        Queue<Node<T>> queue = new LinkedList<>();
        StringBuilder sb = new StringBuilder();

        queue.add(root);

        while (!queue.isEmpty()) {
            int size = queue.size(); // nodos en el nivel actual

            for (int i = 0; i < size; i++) {
                Node<T> current = queue.poll();

                sb.append(current.getValue()).append(" ");

                if (current.getLeft() != null)
                    queue.add(current.getLeft());
                if (current.getRight() != null)
                    queue.add(current.getRight());
            }
            
            sb.append("\n"); // salto de nivel
        }

        return sb.toString().trim(); // Retorna la cadena sin espacios al final
    }
}