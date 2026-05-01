package proyectofinal.SistemaGestion.AgendamientoVisitas;

import proyectofinal.Personal.Client;
import proyectofinal.Inmueble.Property;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase de apoyo que representa una solicitud de visita.
 * Actúa como el contenedor de datos (DTO) para las Colas y el Historial.
 */
public class VisitRequest {
    private Client client;
    private Property property;
    private LocalDateTime dateTime;
    private String status; // "PENDING", "COMPLETED", "CANCELLED", "RECHEDULED", "CONFIRM"
    private String notes;

    public VisitRequest(Client client, Property property, LocalDateTime dateTime) {
        this.client = client;
        this.property = property;
        this.dateTime = dateTime;
        this.status = "PENDING";
        this.notes = "";
    }


    public void markAsCompleted() {
        this.status = "COMPLETED";
    }

    public void markAsCancelled() {
        this.status = "CANCELLED";
    }

    public void markAsConfirm(){
        this.status = "CONFIRM";
    }

    public void markAsRecheduled(){
        this.status= "RECHEDULED";
    }

    public Client getClient() {
        return client;
    }

    public Property getProperty() {
        return property;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Formatea la fecha para mostrarla en la tabla de la GUI.
     */
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    /**
     * Representación en texto para depuración o consolas.
     */
    @Override
    public String toString() {
        return String.format("[%s] Cliente: %s - Inmueble: %s - Fecha: %s", 
                status, client.getName(), property.getCode(), getFormattedDate());
    }


    public void setDateTime(LocalDateTime newDate) {
        this.dateTime = newDate;
    }
}