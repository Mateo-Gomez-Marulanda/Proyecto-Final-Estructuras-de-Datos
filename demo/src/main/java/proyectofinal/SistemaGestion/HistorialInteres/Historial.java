package proyectofinal.SistemaGestion.HistorialInteres;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;

public abstract class Historial<T> {
    private SimpleLinkedList<T> historial;

    public Historial() {
        this.historial = new SimpleLinkedList<>();
    }

    // agrega un elemento al historial de interés del cliente
    public void agregarHistorial(T elemento) {
        this.historial.addFirst(elemento);
    }

    // elimina un elemento del historial de interés del cliente
    public void eliminarHistorial(T elemento) {
        this.historial.removeElement(elemento);
    }

    // consulta general del historial de interés del cliente
    public  void consultarHistorial(){
        for (T elemento : historial) {
            System.out.println(elemento);
        }
    }

    // consulta detallada de un elemento por su ID
    public abstract T consultarElemento(String codigo);

    // permite obtener el historial completo para su consulta o manipulación externa
    protected SimpleLinkedList<T> getHistorial() {
        return historial;
    }
}