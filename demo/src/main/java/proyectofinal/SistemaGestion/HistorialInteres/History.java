package proyectofinal.SistemaGestion.HistorialInteres;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;

public abstract class History<T> {
    private SimpleLinkedList<T> historyList;

    public History() {
        this.historyList = new SimpleLinkedList<>();
    }

    // agrega un elemento al historial de interés del cliente
    public void addToHistory(T element) {
        this.historyList.addFirst(element);
    }

    // elimina un elemento del historial de interés del cliente
    public void removeFromHistory(T element) {
        this.historyList.removeElement(element);
    }

    // consulta general del historial de interés del cliente
    public void displayHistory() {
        for (T element : historyList) {
            System.out.println(element);
        }
    }

    // consulta detallada de un elemento por su ID
    public abstract T findElementById(String code);

    // permite obtener el historial completo para su consulta o manipulación externa
    protected SimpleLinkedList<T> getHistoryList() {
        return historyList;
    }
}