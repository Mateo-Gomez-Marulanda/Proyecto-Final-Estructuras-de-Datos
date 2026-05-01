package proyectofinal.SistemaGestion.AgendamientoVisitas;

import java.time.LocalDateTime;

import proyectofinal.EstructurasDeDatos.Colas.Queue;
import proyectofinal.EstructurasDeDatos.ColasDePrioridad.PriorityQueue;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;

public class VisitManager {

    private Queue<VisitRequest> pendingVisits;
    private PriorityQueue<VisitRequest> priorityVisits;
    private SimpleLinkedList<VisitRequest> visitHistory;

    public VisitManager() {
        this.pendingVisits = new Queue<>();
        this.priorityVisits = new PriorityQueue<>();
        this.visitHistory = new SimpleLinkedList<>();
    }


    public void scheduleVisit(Client client, Property property, LocalDateTime date) {
        VisitRequest request = new VisitRequest(client, property, date);

        if (client.getClientType().equalsIgnoreCase("Premium")) {
            priorityVisits.enqueue(request, 1); // Prioridad Máxima
        } else if (client.getClientType().equalsIgnoreCase("Frecuente")) {
            priorityVisits.enqueue(request, 2); // Prioridad Media
        } else {
            pendingVisits.enqueue(request); // Cola Normal (FIFO)
        }
    }

    public void confirmVisit(VisitRequest request) {
        if (request != null) {
            request.markAsConfirm();
        }
    }

    public void rescheduleVisit(VisitRequest request, LocalDateTime newDate) {
        if (request == null) return;

        // Actualizamos datos del ticket
        request.setDateTime(newDate); 
        request.markAsRecheduled();
        
        // Al reprogramar, le damos prioridad 2 para que no pierda su lugar frente a solicitudes nuevas de clientes normales.
        priorityVisits.enqueue(request, 2);
    }

    public void cancelVisit(VisitRequest request, String reason) {
        if (request == null) return;
        
        request.markAsCancelled();
        request.setNotes("Motivo cancelación: " + reason);
        visitHistory.addLast(request);
    }


    /**
     * Obtiene la siguiente visita para ser atendida por un asesor.
     */
    public VisitRequest getNextVisitToAttend() {
        if (!priorityVisits.isEmpty()) {
            return priorityVisits.dequeue();
        } 
        if (!pendingVisits.isEmpty()) {
            return pendingVisits.dequeue();
        }
        return null;
    }

    /**
     * Cierre definitivo de la visita tras la atención del asesor.
     */
    public void processVisitCompletion(VisitRequest request, String resultNotes, boolean interested) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de visita no puede ser nula.");
        }

        request.markAsCompleted();
        request.setNotes(resultNotes);

        Client client = request.getClient();
        Property property = request.getProperty();
        
        // Registrar en el historial del cliente (Requisito 4.5)
        client.getVisitedPropertiesHistory().addLast(property);

        // Lógica de Negocio (Requisito 4.6)
        if (interested) {
            property.setPropertyStatus("EN NEGOCIACIÓN");
            property.setAvailable(false); 
            client.setSearchStatus("Interesado en " + property.getCode());
        } else {
            property.setPropertyStatus("DISPONIBLE");
            property.setAvailable(true);
        }

        // Mover al historial definitivo
        visitHistory.addLast(request);
    }


    public int getTotalPending() {
        return pendingVisits.size() + priorityVisits.size();
    }

    public SimpleLinkedList<VisitRequest> getVisitHistory() {
        return visitHistory;
    }
}