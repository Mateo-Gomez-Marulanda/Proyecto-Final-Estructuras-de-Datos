package proyectofinal.SistemaGestion.Grafos;

import proyectofinal.EstructurasDeDatos.Grafo.Edge;
import proyectofinal.EstructurasDeDatos.Grafo.Graph;
import proyectofinal.EstructurasDeDatos.Grafo.Vertex;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.SistemaGestion.AgendamientoVisitas.Visit;

public class GraphService {

    private static GraphService instance;
    private final Graph<Object> grafo;

    private GraphService() {
        this.grafo = new Graph<>();
    }

    public static synchronized GraphService getInstance() {
        if (instance == null) {
            instance = new GraphService();
        }
        return instance;
    }

    public Graph<Object> getGrafo() {
        return grafo;
    }

    /**
     * Sincronización desacoplada: recibe los datos necesarios en lugar de 
     * buscar el AppContext, evitando la dependencia circular.
     */
    public void synchronizeData(Iterable<Visit> visitHistory, Iterable<Property> properties) {
        // 1. Relación Cliente <-> Propiedad (basada en visitas)
        for (Visit v : visitHistory) {
            if (v.getClient() != null && v.getProperty() != null) {
                Vertex<Object> v1 = getOrCreateVertex(v.getClient().getId(), v.getClient());
                Vertex<Object> v2 = getOrCreateVertex(v.getProperty().getCode(), v.getProperty());

                String edgeId = "VIS-" + v.getClient().getId() + "-" + v.getProperty().getCode();
                if (!grafo.containsEdge(edgeId)) {
                    grafo.addEdge(edgeId, v1, v2, "VISIT", 1);
                }
            }
        }

        // 2. Relación Zona <-> Propiedad
        for (Property p : properties) {
            if (p.getZone() != null) {
                Vertex<Object> v1 = getOrCreateVertex(p.getZone().toString(), p.getZone());
                Vertex<Object> v2 = getOrCreateVertex(p.getCode(), p);

                String edgeId = "ZONE-" + p.getCode();
                if (!grafo.containsEdge(edgeId)) {
                    grafo.addEdge(edgeId, v1, v2, "LOCATED_IN", 0);
                }
            }
        }
    }

    public Vertex<Object> getOrCreateVertex(String id, Object data) {
        if (!grafo.containsVertex(id)) {
            grafo.addVertex(id, data);
        }
        return grafo.getVertex(id);
    }

    public SimpleLinkedList<String> getRelatedProperties(String nodeId) {
        SimpleLinkedList<String> neighbors = new SimpleLinkedList<>();
        try {
            Vertex<Object> v = grafo.getVertex(nodeId);
            for (Edge<Object> edge : v.getIncidentEdges()) {
                if (edge.getFrom().getId().equals(nodeId)) {
                    neighbors.add(edge.getTo().getId());
                } else {
                    neighbors.add(edge.getFrom().getId());
                }
            }
        } catch (Exception e) {
            System.out.println("Nota: El nodo " + nodeId + " no existe o no tiene conexiones.");
        }
        return neighbors;
    }

    public String getMostActiveClient() {
        String topId = "N/A";
        int maxEdges = -1;
        for (var v : grafo.getVertex()) {
            if (v.getData() instanceof proyectofinal.Personal.Client) {
                int count = v.getIncidentEdges().size();
                if (count > maxEdges) {
                    maxEdges = count;
                    topId = v.getId();
                }
            }
        }
        return topId;
    }

    public String getColdProperties() {
        StringBuilder sb = new StringBuilder();
        for (var v : grafo.getVertex()) {
            if (v.getData() instanceof proyectofinal.Inmueble.Property) {
                if (v.getIncidentEdges().size() == 0) {
                    sb.append(v.getId()).append(" ");
                }
            }
        }
        return sb.length() > 0 ? sb.toString() : "Ninguna (todas tienen actividad)";
    }
}