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
            priorityVisits.enqueue(request, 1);
        } else if (client.getClientType().equalsIgnoreCase("Frecuente")) {
            priorityVisits.enqueue(request, 2);
        } else {
            pendingVisits.enqueue(request);
        }
    }

    public void confirmVisit(VisitRequest request) {
        if (request == null) return;
        request.markAsConfirm();
        removeVisitFromQueues(request);
        visitHistory.add(request);
    }

    public void rescheduleVisit(VisitRequest request, LocalDateTime newDate) {
        if (request == null) return;
        request.setDateTime(newDate); 
        request.markAsRescheduled();
        removeVisitFromQueues(request);
        visitHistory.add(request);
    }

    public void cancelVisit(VisitRequest request, String reason) {
        if (request == null) return;
        request.markAsCancelled();
        request.setNotes("Motivo cancelación: " + reason);
        removeVisitFromQueues(request);
        visitHistory.add(request);
    }

    public VisitRequest getNextVisitToAttend() {
        if (!priorityVisits.isEmpty()) return priorityVisits.dequeue();
        if (!pendingVisits.isEmpty()) return pendingVisits.dequeue();
        return null;
    }

    public void removeVisitFromQueues(VisitRequest target) {
        if (target == null) return;

        // Limpieza segura en Cola de Prioridad
        PriorityQueue<VisitRequest> tempPriority = new PriorityQueue<>();
        while (!priorityVisits.isEmpty()) {
            int p = priorityVisits.peekPriority();
            VisitRequest v = priorityVisits.dequeue();
            // Comparamos los códigos únicos de visita
            if (!v.getCode().trim().equalsIgnoreCase(target.getCode().trim())) {
                tempPriority.enqueue(v, p);
            }
        }
        this.priorityVisits = tempPriority;

        // Limpieza segura en Cola Normal
        Queue<VisitRequest> tempPending = new Queue<>();
        while (!pendingVisits.isEmpty()) {
            VisitRequest v = pendingVisits.dequeue();
            if (!v.getCode().trim().equalsIgnoreCase(target.getCode().trim())) {
                tempPending.enqueue(v);
            }
        }
        this.pendingVisits = tempPending;
    }

    public void processVisitCompletion(VisitRequest request, String resultNotes, boolean interested) {
        if (request == null) throw new IllegalArgumentException("La solicitud no puede ser nula.");

        request.markAsCompleted();
        request.setNotes(resultNotes);

        Client client = request.getClient();
        Property property = request.getProperty();
        
        client.getVisitedPropertiesHistory().add(property);

        if (interested) {
            property.setPropertyStatus("EN NEGOCIACIÓN");
            property.setAvailable(false); 
            client.setSearchStatus("Interesado en " + property.getCode());
        } else {
            property.setPropertyStatus("DISPONIBLE");
            property.setAvailable(true);
        }

        visitHistory.add(request);
    }

    public SimpleLinkedList<VisitRequest> getAllPendingAndActiveVisits() {
        SimpleLinkedList<VisitRequest> list = new SimpleLinkedList<>();
        
        PriorityQueue<VisitRequest> tempPriority = new PriorityQueue<>();
        Queue<VisitRequest> tempPending = new Queue<>();

        while (!priorityVisits.isEmpty()) {
            int p = priorityVisits.peekPriority();
            VisitRequest v = priorityVisits.dequeue();
            list.add(v);
            tempPriority.enqueue(v, p);
        }
        this.priorityVisits = tempPriority;

        while (!pendingVisits.isEmpty()) {
            VisitRequest v = pendingVisits.dequeue();
            list.add(v);
            tempPending.enqueue(v);
        }
        this.pendingVisits = tempPending;

        return list;
    }

    public int getTotalPending() {
        return pendingVisits.size() + priorityVisits.size();
    }

    public SimpleLinkedList<VisitRequest> getVisitHistory() {
        return visitHistory;
    }
}