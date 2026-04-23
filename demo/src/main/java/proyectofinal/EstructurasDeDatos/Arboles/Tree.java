package proyectofinal.EstructurasDeDatos.Arboles;

public class Tree<T> {
    private Node<T> root;

    public Tree() {
        this.root = null;
    }

    private int getHeight(Node<T> node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(getHeight(node.getLeft()), getHeight(node.getRight()));
    }

    // Metodos de recorrido por niveles: por niveles o amplitud
    public void levelOrder() {
        if (root == null) {
            System.out.println("tree is empty.");
        } else {
            levelOrderR(root);
            System.out.println(); // salto de línea al final
        }
    }

    private void levelOrderR(Node<T> current){
        
    }

    // Metodos de recorrido en profundidad: preorden, inorden y postorden
    public void inOrder() {
        if (root == null) {
            System.out.println("tree is empty.");
        } else {
            inOrderR(root);
            System.out.println(); // salto de línea al final
        }
    }

    private void inOrderR(Node<T> current) {
        if (current != null) {
            inOrderR(current.getLeft()); // Recorre el subárbol izquierdo
            System.out.print(current.getData() + " "); // Muestra el valor del nodo actual
            inOrderR(current.getRight()); // Recorre el subárbol derecho
        }
    }

    public void preOrder() {
        if (root == null) {
            System.out.println("tree is empty.");
        } else {
            preOrderR(root);
            System.out.println();
        }
    }

    private void preOrderR(Node<T> current) {
        if (current != null) {
            System.out.print(current.getData() + " "); // obtiene el valor del nodo actual
            preOrderR(current.getLeft()); // subarbol izquierdo
            preOrderR(current.getRight()); // subarbol derecho
        }
    }

    public void postOrder() {
        if (root == null) {
            System.out.println("tree is empty.");
        } else {
            postOrderR(root);
            System.out.println();
        }
    }

    private void postOrderR(Node<T> current) {
        if (current != null) {
            postOrderR(current.getLeft()); // subarbol izquierdo
            postOrderR(current.getRight()); // subarbol derecho
            System.out.print(current.getData() + " "); // obtiene el valor del nodo actual
        }
    }
}