package proyectofinal.SistemaGestion.OperacionDeNegocio;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.Client;
import java.time.LocalDate;

public class BusinessOperation {
    private String identifier;
    private Property relatedProperty;
    private Client client;
    private Advisor advisor;
    private LocalDate date;
    private OperationType operationType;
    private double agreedValue;
    private double commission;
    private String processStatus;

    public BusinessOperation(String identifier, Property relatedProperty, Client client, Advisor advisor,
            LocalDate date, OperationType operationType, double agreedValue, double commission, String processStatus) {
        this.identifier = identifier;
        this.relatedProperty = relatedProperty;
        this.client = client;
        this.advisor = advisor;
        this.date = date;
        this.operationType = operationType;
        this.agreedValue = agreedValue;
        this.commission = commission;
        this.processStatus = processStatus;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Property getRelatedProperty() {
        return relatedProperty;
    }

    public void setRelatedProperty(Property relatedProperty) {
        this.relatedProperty = relatedProperty;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Advisor getAdvisor() {
        return advisor;
    }

    public void setAdvisor(Advisor advisor) {
        this.advisor = advisor;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public double getAgreedValue() {
        return agreedValue;
    }

    public void setAgreedValue(double agreedValue) {
        this.agreedValue = agreedValue;
    }

    public double getCommission() {
        return commission;
    }

    public void setCommission(double commission) {
        this.commission = commission;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    @Override
    public String toString() {
        return "Identifier: " + identifier + " | Property: " + relatedProperty + " | Client: " + client
                + " | Advisor: " + advisor + " | Date: " + date + " | Operation Type: " + operationType
                + " | Agreed Value: $" + agreedValue + " | Commission: $" + commission + " | Process Status: "
                + processStatus;
    }
}