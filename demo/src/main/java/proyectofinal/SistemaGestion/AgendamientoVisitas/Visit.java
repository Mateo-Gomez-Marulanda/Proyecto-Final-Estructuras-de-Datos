package proyectofinal.SistemaGestion.AgendamientoVisitas;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.Client;

public class Visit {
    private String code;
    private Client client;
    private Property property;
    private LocalDate date;
    private LocalTime time;
    private Advisor assignedAdvisor;
    private VisitStatus visitStatus; // USAMOS EL ENUM PARA SEGURIDAD
    private String postObservations;

    // Constructor optimizado para creaciones nuevas
    public Visit(Client client, Property property, LocalDate date, LocalTime time, Advisor assignedAdvisor) {
        this.code = generateUniqueCode(); // Autogenerado como en Visit
        this.client = client;
        this.property = property;
        this.date = date;
        this.time = time;
        this.assignedAdvisor = assignedAdvisor;
        this.visitStatus = VisitStatus.PENDING; // Por defecto
        this.postObservations = "";
    }

    // Método traído de Visit
    private String generateUniqueCode() {
        return "VIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Método traído de Visit adaptado a LocalDate y LocalTime
    public String getFormattedDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return date.format(dateFormatter) + " " + time.format(timeFormatter);
    }

    // Getters y Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public Advisor getAssignedAdvisor() {
        return assignedAdvisor;
    }

    public void setAssignedAdvisor(Advisor assignedAdvisor) {
        this.assignedAdvisor = assignedAdvisor;
    }

    public VisitStatus getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(VisitStatus visitStatus) {
        this.visitStatus = visitStatus;
    }

    public String getPostObservations() {
        return postObservations;
    }

    public void setPostObservations(String postObservations) {
        this.postObservations = postObservations;
    }

    @Override
    public String toString() {
        return String.format("[%s] Cliente: %s - Inmueble: %s - Fecha: %s",
                visitStatus, client.getName(), property.getCode(), getFormattedDate());
    }
}