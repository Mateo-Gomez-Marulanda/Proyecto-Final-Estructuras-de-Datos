package proyectofinal.SistemaGestion.AgendamientoVisitas;

import java.time.LocalDate;
import java.time.LocalTime;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.Client;


public class Visit {
    private String code; // código único para identificar la visita
    private Client client; // Cliente
    private Property property; // Inmueble
    private LocalDate date;
    private LocalTime time;
    private Advisor assignedAdvisor; // Asesor
    private VisitStatus visitStatus; // pendiente, confirmada, realizada, cancelada, reprogramada
    private String postObservations;

    public Visit(String code, Client client, Property property, LocalDate date, LocalTime time,
            Advisor assignedAdvisor,
            VisitStatus visitStatus, String postObservations) {
        this.code = code;
        this.client = client;
        this.property = property;
        this.date = date;
        this.time = time;
        this.assignedAdvisor = assignedAdvisor;
        this.visitStatus = visitStatus;
        this.postObservations = postObservations;
    }

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
        return "Code: " + code + " | Client: " + client + " | Property: " + property + " | Date: " + date
                + " | Time: " + time
                + " | Assigned Advisor: " + assignedAdvisor + " | Status: " + visitStatus + " | Observations: "
                + postObservations;
    }
}