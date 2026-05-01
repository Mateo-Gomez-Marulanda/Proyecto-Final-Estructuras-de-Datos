package proyectofinal.SistemaGestion.OperacionDeNegocio;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.Client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BusinessOperation {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final SimpleStringProperty identifier;
    private final SimpleObjectProperty<Property>      relatedProperty;
    private final SimpleObjectProperty<Client>        client;
    private final SimpleObjectProperty<Advisor>       advisor;
    private final SimpleObjectProperty<LocalDate>     date;
    private final SimpleObjectProperty<OperationType> operationType;
    private final SimpleDoubleProperty agreedValue;
    private final SimpleDoubleProperty commission;
    private final SimpleObjectProperty<ProcessStatus> processStatus;

    public BusinessOperation(String identifier, Property relatedProperty, Client client,
                             Advisor advisor, LocalDate date, OperationType operationType,
                             double agreedValue, double commission, ProcessStatus processStatus) {
        this.identifier      = new SimpleStringProperty(identifier);
        this.relatedProperty = new SimpleObjectProperty<>(relatedProperty);
        this.client          = new SimpleObjectProperty<>(client);
        this.advisor         = new SimpleObjectProperty<>(advisor);
        this.date            = new SimpleObjectProperty<>(date);
        this.operationType   = new SimpleObjectProperty<>(operationType);
        this.agreedValue     = new SimpleDoubleProperty(agreedValue);
        this.commission      = new SimpleDoubleProperty(commission);
        this.processStatus   = new SimpleObjectProperty<>(processStatus);
    }

    // ─────────────────────────────────────────────
    // Business logic
    // ─────────────────────────────────────────────

    /**
     * Advances the operation to the next logical status.
     * IN_PROGRESS → PENDING_SIGNATURE → COMPLETED
     */
    public void advanceStatus() {
        switch (processStatus.get()) {
            case IN_PROGRESS:
                processStatus.set(ProcessStatus.PENDING_SIGNATURE);
                break;
            case PENDING_SIGNATURE:
                processStatus.set(ProcessStatus.COMPLETED);
                break;
            default:
                throw new RuntimeException(
                    "Cannot advance status from: " + processStatus.get());
        }
    }

    public void cancel() {
        if (processStatus.get() == ProcessStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel an already completed operation.");
        }
        processStatus.set(ProcessStatus.CANCELLED);
    }

    public boolean isCompleted() {
        return processStatus.get() == ProcessStatus.COMPLETED;
    }

    public boolean isCancelled() {
        return processStatus.get() == ProcessStatus.CANCELLED;
    }

    public boolean isActive() {
        return processStatus.get() == ProcessStatus.IN_PROGRESS
            || processStatus.get() == ProcessStatus.PENDING_SIGNATURE;
    }

    // Property getters (for TableView binding)
    public SimpleStringProperty identifierProperty()               { return identifier; }
    public SimpleObjectProperty<Property> relatedPropertyProperty(){ return relatedProperty; }
    public SimpleObjectProperty<Client> clientProperty()           { return client; }
    public SimpleObjectProperty<Advisor> advisorProperty()         { return advisor; }
    public SimpleObjectProperty<LocalDate> dateProperty()          { return date; }
    public SimpleObjectProperty<OperationType> operationTypeProperty() { return operationType; }
    public SimpleDoubleProperty agreedValueProperty()              { return agreedValue; }
    public SimpleDoubleProperty commissionProperty()               { return commission; }
    public SimpleObjectProperty<ProcessStatus> processStatusProperty() { return processStatus; }


    // Value getters
    public String getIdentifier()          { return identifier.get(); }
    public Property getRelatedProperty()   { return relatedProperty.get(); }
    public Client getClient()             { return client.get(); }
    public Advisor getAdvisor()           { return advisor.get(); }
    public LocalDate getDate()            { return date.get(); }
    public OperationType getOperationType(){ return operationType.get(); }
    public double getAgreedValue()        { return agreedValue.get(); }
    public double getCommission()         { return commission.get(); }
    public ProcessStatus getProcessStatus(){ return processStatus.get(); }

    // Value setters
    public void setIdentifier(String identifier)           { this.identifier.set(identifier); }
    public void setRelatedProperty(Property relatedProperty){ this.relatedProperty.set(relatedProperty); }
    public void setClient(Client client)                   { this.client.set(client); }
    public void setAdvisor(Advisor advisor)                { this.advisor.set(advisor); }
    public void setDate(LocalDate date)                    { this.date.set(date); }
    public void setOperationType(OperationType type)       { this.operationType.set(type); }
    public void setAgreedValue(double value)               { this.agreedValue.set(value); }
    public void setCommission(double commission)           { this.commission.set(commission); }
    public void setProcessStatus(ProcessStatus status)     { this.processStatus.set(status); }

    // Formatted strings — convenient for TableView cells
    public String getDateFormatted() {
        return date.get() != null ? date.get().format(FORMATTER) : "—";
    }

    public String getAgreedValueFormatted() {
        return String.format("$%,.2f", agreedValue.get());
    }

    public String getCommissionFormatted() {
        return String.format("$%,.2f", commission.get());
    }

    // toString
    @Override
    public String toString() {
        return "ID: " + getIdentifier()
                + " | Type: " + getOperationType()
                + " | Property: " + (getRelatedProperty() != null ? getRelatedProperty().getCode() : "—")
                + " | Client: " + (getClient() != null ? getClient().getName() : "—")
                + " | Advisor: " + (getAdvisor() != null ? getAdvisor().getName() : "—")
                + " | Date: " + getDateFormatted()
                + " | Value: " + getAgreedValueFormatted()
                + " | Commission: " + getCommissionFormatted()
                + " | Status: " + getProcessStatus();
    }
}