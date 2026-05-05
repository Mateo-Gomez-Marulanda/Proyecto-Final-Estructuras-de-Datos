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

    public void addVertex(String id, T data) {
        if (id == null) {
            throw new IllegalArgumentException("vertex id cannot be null");
        }

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

        for (Vertex<T> v : vertex) {
            if (v.getId().equals(id)) {
                vertex.remove(v);
            }
        }
    }

    public void addEdge(String id, Vertex<T> from, Vertex<T> to, String type, int weight) {
        if (id == null) {
            throw new IllegalArgumentException("edge id cannot be null");
        }

        if (from == null || to == null || !vertex.contains(from) || !vertex.contains(to)) {
            throw new IllegalArgumentException("from and to vertices cannot be null");
        }

        for (Edge<T> e : edges) {
            if (id.equals(e.getId())) {
                throw new IllegalArgumentException("edge with the same id already exists");
            }
        }

        Edge<T> aux = new Edge<>(id, from, to, type, weight);
        edges.add(aux);
        from.addEdge(aux);
        to.addEdge(aux);
    }

    public void removeEdge(String id) {
        if (id == null) {
            throw new IllegalArgumentException("edge id cannot be null");
        }

        for (Edge<T> e : edges) {
            if (e.getId().equals(id)) {
                edges.remove(e);
                e.getFrom().removeEdge(id);
                e.getTo().removeEdge(id);
            }
        }
    }
}