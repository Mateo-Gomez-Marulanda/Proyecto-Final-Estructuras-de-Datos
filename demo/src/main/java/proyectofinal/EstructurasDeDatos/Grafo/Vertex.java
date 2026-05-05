package proyectofinal.EstructurasDeDatos.Grafo;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;

//clase vertice que representa el contenedor de la informacion en el grafo
public class Vertex<T> {
    private String id;
    private T data;

    // lista de adyacencia para identificar las conexciones del vertice

    private SimpleLinkedList<Edge<T>> incidentEdges; // aristas incidentes
    private SimpleLinkedList<Edge<T>> outgoingEdges; // aristas salientes

    public Vertex(String id, T data) {
        this.id = id;
        this.data = data;
        this.incidentEdges = new SimpleLinkedList<>();
        this.outgoingEdges = new SimpleLinkedList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // obtiene las aristas incidentes y salientes
    public SimpleLinkedList<Edge<T>> getIncidentEdges() {
        return incidentEdges;
    }

    public SimpleLinkedList<Edge<T>> getOutgoingEdges() {
        return outgoingEdges;
    }

    // agrega una arista a la lista de incidencia del vertice
    public void addEdge(Edge<T> edge) {
        if (edge != null) {
            if (id.equals(edge.getFrom().getId())) { // verifica si la arista es saliente
                incidentEdges.add(edge); // agrega a la lista de aristas salientes
            } else {
                if (id.equals(edge.getTo().getId())) { // verifica si la arista es entrante
                    outgoingEdges.add(edge); // agrega a la lista de aristas entrantes
                } else {
                    // si la arista no conecta con este vertice, se lanza una excepcion
                    throw new IllegalArgumentException("the edge does not connect to this vertex");
                }
            }
        }
    }

    // metodos para eliminar una arista de la lista de incidencia del vertice

    // elimina en base al id de la arista
    public boolean removeEdge(String edgeId) {
        if (edgeId != null) {
            // Buscar y remover de incidentEdges
            for (int i = 0; i < incidentEdges.size(); i++) {
                Edge<T> edge = incidentEdges.get(i);
                if (edge != null && edgeId.equals(edge.getId())) {
                    incidentEdges.remove(i);
                    return true;
                }
            }

            // Buscar y remover de outgoingEdges
            for (int i = 0; i < outgoingEdges.size(); i++) {
                Edge<T> edge = outgoingEdges.get(i);
                if (edge != null && edgeId.equals(edge.getId())) {
                    outgoingEdges.remove(i);
                    return true;
                }
            }
        }
        return false;
    }

    // elimina en base a la arista completa
    public boolean removeEdge(Edge<T> edge) {
        if (edge != null && edge.getId() != null) {
            return removeEdge(edge.getId());
        }
        return false;
    }

    // retorna la lista de vecinos (destino de aristas salientes)
    public SimpleLinkedList<Vertex<T>> getNeighbors() {
        SimpleLinkedList<Vertex<T>> neighbors = new SimpleLinkedList<>();
        for (int i = 0; i < incidentEdges.size(); i++) {
            Edge<T> edge = incidentEdges.get(i);
            if (edge != null) {
                neighbors.add(edge.getTo());
            }
        }
        return neighbors;
    }

    // obtiene el grado del vertice (suma de aristas entrantes y salientes)
    public int getDegree() {
        return incidentEdges.size() + outgoingEdges.size();
    }

    @Override
    public String toString() {
        return "Vertex [id=" + id + ", data=" + data + ", incidentEdges=" + incidentEdges.size() + ", outgoingEdges="
                + outgoingEdges.size() + "]";
    }
}