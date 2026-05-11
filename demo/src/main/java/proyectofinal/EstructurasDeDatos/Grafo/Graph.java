package proyectofinal.EstructurasDeDatos.Grafo;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;

//grafo dirigido ponderado

public class Graph<T> {
    private SimpleLinkedList<Vertex<T>> vertex; // lista de vertices del grafo
    private SimpleLinkedList<Edge<T>> edges; // lista de aristas del grafo

    public Graph() {
        this.vertex = new SimpleLinkedList<>();
        this.edges = new SimpleLinkedList<>();
    }

    public boolean isEmpty() {
        return vertex.size() == 0;
    }

    // el tamaño se define en base al numero de vertices
    public int size() {
        return vertex.size();
    }

    public int edgeCount() {
        return edges.size();
    }

    public SimpleLinkedList<Vertex<T>> getVertex() {
        return vertex;
    }

    public SimpleLinkedList<Edge<T>> getEdges() {
        return edges;
    }

    public void addVertex(String id, T data) {
        if (id == null) {
            throw new IllegalArgumentException("vertex id cannot be null");
        }

        // se relaiza una verificacion para evitar agregar vertices con el mismo id al
        // grafo
        for (Vertex<T> v : vertex) {
            if (id.equals(v.getId())) {
                throw new IllegalArgumentException("vertex with the same id already exists");
            }
        }

        Vertex<T> aux = new Vertex<>(id, data);
        vertex.add(aux);
    }

    public void removeVertex(String id) {
        if (id == null) {
            throw new IllegalArgumentException("vertex id cannot be null");
        }

        // se itera sobre la lista de vertices para encontrar el vertice con el id
        // especificado
        for (int i = 0; i < vertex.size(); i++) {
            Vertex<T> v = vertex.get(i);
            if (v.getId().equals(id)) {
                vertex.remove(v); // elimina referencia en la lista
                removeAdjacentEdges(v);
                break;
            }
        }
    }

    // metodo para eliminar una arista si se elimina el vertice al que hace
    // referencia
    private void removeAdjacentEdges(Vertex<T> vertex) {
        for (int i = edges.size() - 1; i >= 0; i--) {
            Edge<T> e = edges.get(i); // arista auxilar
            // verifica que la arista contenga el vertice que se elimino
            if (e.getFrom().equals(vertex) || e.getTo().equals(vertex)) {
                removeEdge(e.getId()); // se elimina la arista
            }
        }
    }

    // se agrega una arista al grafo, el usuario debe ingresar cada atributo de la
    // arista
    public void addEdge(String id, Vertex<T> from, Vertex<T> to, String type, int weight) {
        if (id == null) {
            throw new IllegalArgumentException("edge id cannot be null");
        }

        // verificar que los vertices de origen y destino no sean nulos y que la arista
        // no exista previamente en el grafo
        if (from == null || to == null) {
            throw new IllegalArgumentException("from and to vertices cannot be null");
        }

        // se verifica que no exista una arista con el mismo id en el grafo para evitar
        // duplicados
        for (Edge<T> e : edges) {
            if (id.equals(e.getId())) {
                throw new IllegalArgumentException("edge with the same id already exists");
            }
        }

        Edge<T> aux = new Edge<>(id, from, to, type, weight); // nueva arista

        // se agrega en la lista global de aristas del grafo y en las listas de
        // incidencia de los vertices de origen y destino
        edges.add(aux);
        from.addEdge(aux);
        to.addEdge(aux);
    }

    // eliminar una arista del grafo mediante su id
    public void removeEdge(String id) {
        if (id == null) {
            throw new IllegalArgumentException("edge id cannot be null");
        }

        // se itera sobre la lista de aristas para encontrar la arista con el id
        // especificado
        for (int i = 0; i < edges.size(); i++) {
            Edge<T> e = edges.get(i);
            if (e.getId().equals(id)) {

                // se elimina la arista de la lista global de aristas del grafo y de las listas
                // de incidencia de los vertices
                edges.remove(e);
                e.getFrom().removeEdge(id);
                e.getTo().removeEdge(id);
                break;
            }
        }
    }

    public Vertex<T> getVertex(String id) {
        if (id == null) {
            throw new IllegalArgumentException("vertex id cannot be null");
        }

        for (Vertex<T> v : vertex) {
            if (id.equals(v.getId())) {
                return v;
            }
        }
        throw new IllegalArgumentException("vertex with id " + id + " not found");
    }

    public boolean containsVertex(String id) {
        for (Vertex<T> v : vertex) {
            if (id.equals(v.getId())) {
                return true;
            }
        }

        return false;
    }

    public Edge<T> getEdge(String id) {
        if (id == null) {
            throw new IllegalArgumentException("edge id cannot be null");
        }

        for (Edge<T> e : edges) {
            if (id.equals(e.getId())) {
                return e;
            }
        }
        throw new IllegalArgumentException("edge with id " + id + " not found");
    }

    public boolean containsEdge(String id) {
        for (Edge<T> e : edges) {
            if (id.equals(e.getId())) {
                return true;
            }
        }
        return false;
    }
}