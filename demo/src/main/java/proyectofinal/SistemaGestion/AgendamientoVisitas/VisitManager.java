package proyectofinal.SistemaGestion.AgendamientoVisitas;

import java.time.LocalDate;
import java.time.LocalTime;
import proyectofinal.EstructurasDeDatos.Colas.Queue;
import proyectofinal.EstructurasDeDatos.ColasDePrioridad.PriorityQueue;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;

public class VisitManager {

    private Queue<Visit> pendingVisits;
    private PriorityQueue<Visit> priorityVisits;
    private SimpleLinkedList<Visit> visitHistory;

    public VisitManager() {
        this.pendingVisits = new Queue<>();
        this.priorityVisits = new PriorityQueue<>();
        this.visitHistory = new SimpleLinkedList<>();
    }

    // 1. Adaptado para usar LocalDate y LocalTime, y crear un objeto Visit
    public void scheduleVisit(Client client, Property property, LocalDate date, LocalTime time) {
        // Generamos un código único básico para la visita
       
        
        Visit newVisit = new Visit(
                client, 
                property, 
                date, 
                time, 
                property.getResponsibleAdvisor()
        );

       if (client.getClientType() != null && client.getClientType().equalsIgnoreCase("Premium")) {
            priorityVisits.enqueue(newVisit, 1);
        } else if (client.getClientType() != null && client.getClientType().equalsIgnoreCase("Frecuente")) {
            priorityVisits.enqueue(newVisit, 2);
        } else {
            pendingVisits.enqueue(newVisit);
        }

        client.addVisita(newVisit);
    }

    // 2. Adaptado para usar VisitStatus.CONFIRMADA
    public void confirmVisit(Visit visit) {
        if (visit == null) return;
        visit.setVisitStatus(VisitStatus.CONFIRM);
        removeVisitFromQueues(visit);
        visit.getClient().removeVisita(visit);
        visit.getClient().getVisitedPropertiesHistory().add(visit.getProperty());
        visitHistory.add(visit);
    }

    // 3. Adaptado para usar LocalDate/LocalTime y VisitStatus.REPROGRAMADA
    public void rescheduleVisit(Visit visit, LocalDate newDate, LocalTime newTime) {
        if (visit == null) return;
        removeVisitFromQueues(visit);
        visit.setDate(newDate); 
        visit.setTime(newTime);
        visit.setVisitStatus(VisitStatus.PENDING);
        
       enqueue(visit);
    }

    // 4. Adaptado para usar VisitStatus.CANCELADA y setPostObservations
    public void cancelVisit(Visit visit, String reason) {
        if (visit == null) return;
        visit.setVisitStatus(VisitStatus.CANCELLED);
        visit.setPostObservations("Motivo cancelación: " + reason);
        removeVisitFromQueues(visit);
        visit.getClient().removeVisita(visit);
        visitHistory.add(visit);
    }

    private void enqueue(Visit v) {
        String type = v.getClient().getClientType();
        if (type != null && type.equalsIgnoreCase("Premium")) {
            priorityVisits.enqueue(v, 1);
        } else if (type != null && type.equalsIgnoreCase("Frecuente")) {
            priorityVisits.enqueue(v, 2);
        } else {
            pendingVisits.enqueue(v);
        }
    }

    public Visit getNextVisitToAttend() {
        if (!priorityVisits.isEmpty()) return priorityVisits.dequeue();
        if (!pendingVisits.isEmpty()) return pendingVisits.dequeue();
        return null;
    }

    public void removeVisitFromQueues(Visit target) {
        if (target == null) return;

        // Limpieza segura en Cola de Prioridad
        PriorityQueue<Visit> tempPriority = new PriorityQueue<>();
        while (!priorityVisits.isEmpty()) {
            int p = priorityVisits.peekPriority();
            Visit v = priorityVisits.dequeue();
            if (!v.getCode().trim().equalsIgnoreCase(target.getCode().trim())) {
                tempPriority.enqueue(v, p);
            }
        }
        this.priorityVisits = tempPriority;

        // Limpieza segura en Cola Normal
        Queue<Visit> tempPending = new Queue<>();
        while (!pendingVisits.isEmpty()) {
            Visit v = pendingVisits.dequeue();
            if (!v.getCode().trim().equalsIgnoreCase(target.getCode().trim())) {
                tempPending.enqueue(v);
            }
        }
        this.pendingVisits = tempPending;
    }

    // 5. Adaptado para usar VisitStatus.REALIZADA
    public void processVisitCompletion(Visit visit, String resultNotes, boolean interested) {
        if (visit == null) throw new IllegalArgumentException("La visita no puede ser nula.");

        visit.setVisitStatus(VisitStatus.COMPLETED);
        visit.setPostObservations(resultNotes);

        Client client = visit.getClient();
        Property property = visit.getProperty();
        
        // Asumiendo que tu clase Client tiene este método
        if(client.getVisitedPropertiesHistory() != null){
             client.getVisitedPropertiesHistory().add(property);
        }

        if (interested) {
            property.setPropertyStatus("EN NEGOCIACIÓN");
            property.setAvailable(false); 
            // Asumiendo que Client tiene este método
            client.setSearchStatus("Interesado en " + property.getCode()); 
        } else {
            property.setPropertyStatus("DISPONIBLE");
            property.setAvailable(true);
        }

        visitHistory.add(visit);
    }

    public SimpleLinkedList<Visit> getAllPendingAndActiveVisits() {
        SimpleLinkedList<Visit> list = new SimpleLinkedList<>();
        
        PriorityQueue<Visit> tempPriority = new PriorityQueue<>();
        Queue<Visit> tempPending = new Queue<>();

        while (!priorityVisits.isEmpty()) {
            int p = priorityVisits.peekPriority();
            Visit v = priorityVisits.dequeue();
            list.add(v);
            tempPriority.enqueue(v, p);
        }
        this.priorityVisits = tempPriority;

        while (!pendingVisits.isEmpty()) {
            Visit v = pendingVisits.dequeue();
            list.add(v);
            tempPending.enqueue(v);
        }
        this.pendingVisits = tempPending;

        return list;
    }

    public int getTotalPending() {
        return pendingVisits.size() + priorityVisits.size();
    }

    public SimpleLinkedList<Visit> getVisitHistory() {
        return visitHistory;
    }
}