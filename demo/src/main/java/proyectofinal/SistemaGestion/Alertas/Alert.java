package proyectofinal.SistemaGestion.Alertas;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alert {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final SimpleStringProperty id;
    private final SimpleObjectProperty<AlertType>   alertType;
    private final SimpleObjectProperty<AlertStatus> alertStatus;
    private final SimpleStringProperty description;
    private final SimpleStringProperty relatedEntityCode;
    private final SimpleStringProperty relatedEntityType; // "PROPERTY", "CLIENT", "VISIT", "CONTRACT"
    private final SimpleObjectProperty<LocalDateTime> generatedAt;
    private final SimpleObjectProperty<LocalDateTime> resolvedAt;
    private final SimpleStringProperty observations;

    public Alert(String id, AlertType alertType, String description,
                 String relatedEntityCode, String relatedEntityType) {
        this.id               = new SimpleStringProperty(id);
        this.alertType        = new SimpleObjectProperty<>(alertType);
        this.alertStatus      = new SimpleObjectProperty<>(AlertStatus.PENDING); // always starts PENDING
        this.description      = new SimpleStringProperty(description);
        this.relatedEntityCode = new SimpleStringProperty(relatedEntityCode);
        this.relatedEntityType = new SimpleStringProperty(relatedEntityType);
        this.generatedAt      = new SimpleObjectProperty<>(LocalDateTime.now());
        this.resolvedAt       = new SimpleObjectProperty<>(null);
        this.observations     = new SimpleStringProperty(null);
    }

    // Business logic
    public void markAsReviewed(String observations) {
        if (this.alertStatus.get() != AlertStatus.PENDING) {
            throw new RuntimeException("Only PENDING alerts can be marked as reviewed.");
        }
        this.alertStatus.set(AlertStatus.REVIEWED);
        this.resolvedAt.set(LocalDateTime.now());
        this.observations.set(observations);
    }

    public void markAsDismissed(String observations) {
        if (this.alertStatus.get() != AlertStatus.PENDING) {
            throw new RuntimeException("Only PENDING alerts can be dismissed.");
        }
        this.alertStatus.set(AlertStatus.DISMISSED);
        this.resolvedAt.set(LocalDateTime.now());
        this.observations.set(observations);
    }

    public boolean isPending() {
        return this.alertStatus.get() == AlertStatus.PENDING;
    }

    // Property getters (for TableView binding)
    public SimpleStringProperty idProperty()               { return id; }
    public SimpleObjectProperty<AlertType> alertTypeProperty()     { return alertType; }
    public SimpleObjectProperty<AlertStatus> alertStatusProperty() { return alertStatus; }
    public SimpleStringProperty descriptionProperty()      { return description; }
    public SimpleStringProperty relatedEntityCodeProperty(){ return relatedEntityCode; }
    public SimpleStringProperty relatedEntityTypeProperty(){ return relatedEntityType; }
    public SimpleObjectProperty<LocalDateTime> generatedAtProperty() { return generatedAt; }
    public SimpleObjectProperty<LocalDateTime> resolvedAtProperty()  { return resolvedAt; }
    public SimpleStringProperty observationsProperty()     { return observations; }

    // Value getters
    public String getId()                  { return id.get(); }
    public AlertType getAlertType()        { return alertType.get(); }
    public AlertStatus getAlertStatus()    { return alertStatus.get(); }
    public String getDescription()         { return description.get(); }
    public String getRelatedEntityCode()   { return relatedEntityCode.get(); }
    public String getRelatedEntityType()   { return relatedEntityType.get(); }
    public LocalDateTime getGeneratedAt()  { return generatedAt.get(); }
    public LocalDateTime getResolvedAt()   { return resolvedAt.get(); }
    public String getObservations()        { return observations.get(); }

    // Formatted date strings — convenient for TableView cells
    public String getGeneratedAtFormatted() {
        return generatedAt.get() != null ? generatedAt.get().format(FORMATTER) : "—";
    }

    public String getResolvedAtFormatted() {
        return resolvedAt.get() != null ? resolvedAt.get().format(FORMATTER) : "—";
    }

    // toString
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(getId()).append("] ")
          .append(getAlertType())
          .append(" | Status: ").append(getAlertStatus())
          .append(" | Entity: ").append(getRelatedEntityType())
          .append(" ").append(getRelatedEntityCode())
          .append(" | Generated: ").append(getGeneratedAtFormatted())
          .append(" | ").append(getDescription());

        if (getResolvedAt() != null) {
            sb.append(" | Resolved: ").append(getResolvedAtFormatted());
        }
        if (getObservations() != null) {
            sb.append(" | Observations: ").append(getObservations());
        }
        return sb.toString();
    }
}