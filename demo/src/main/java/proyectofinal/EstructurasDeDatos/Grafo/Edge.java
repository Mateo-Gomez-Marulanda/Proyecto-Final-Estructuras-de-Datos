package proyectofinal.EstructurasDeDatos.Grafo;

//clase vertice que representa la conexion dirigda entre vertices 
public class Edge<T> {
    private String id;
    private Vertex<T> from; // vertice de salida
    private Vertex<T> to; // vertice de llegada
    private String type; // tipo relacion (opcional)
    private int weight; // valor del peso de la relacion

    public Edge(String id, Vertex<T> from, Vertex<T> to, String type, int weight) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.type = type;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Vertex<T> getFrom() {
        return from;
    }

    public void setFrom(Vertex<T> from) {
        this.from = from;
    }

    public Vertex<T> getTo() {
        return to;
    }

    public void setTo(Vertex<T> to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Edge [id=" + id + ", from=" + from + ", to=" + to + ", type=" + type + ", weight=" + weight + "]";
    }
}