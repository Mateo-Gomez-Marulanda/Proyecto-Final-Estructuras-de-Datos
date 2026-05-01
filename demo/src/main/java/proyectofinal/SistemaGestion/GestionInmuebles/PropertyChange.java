package proyectofinal.SistemaGestion.GestionInmuebles;

import java.time.LocalDateTime;

public class PropertyChange {

    public enum ChangeType {
        FIELD_MODIFICATION,
        STATUS_CHANGE,
        ADMINISTRATIVE_ACTION
    }

    private String propertyCode;
    private ChangeType changeType;
    private String modifiedField;    // ej: "price", "isAvailable", "propertyStatus"
    private Object previousValue;
    private Object newValue;
    private String responsiblePerson; // nombre del asesor que hizo el cambio
    private LocalDateTime dateTime;
    private String description;

    // Constructor para modificaciones de campo y cambios de estado
    public PropertyChange(String propertyCode, ChangeType changeType,
                          String modifiedField, Object previousValue,
                          Object newValue, String responsiblePerson) {
        this.propertyCode = propertyCode;
        this.changeType = changeType;
        this.modifiedField = modifiedField;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.responsiblePerson = responsiblePerson;
        this.dateTime = LocalDateTime.now();
        this.description = null;
    }

    // Constructor para acciones administrativas
    public PropertyChange(String propertyCode, String description, String responsiblePerson) {
        this.propertyCode = propertyCode;
        this.changeType = ChangeType.ADMINISTRATIVE_ACTION;
        this.modifiedField = null;
        this.previousValue = null;
        this.newValue = null;
        this.responsiblePerson = responsiblePerson;
        this.dateTime = LocalDateTime.now();
        this.description = description;
    }

    public String getPropertyCode() { return propertyCode; }
    public ChangeType getChangeType() { return changeType; }
    public String getModifiedField() { return modifiedField; }
    public Object getPreviousValue() { return previousValue; }
    public Object getNewValue() { return newValue; }
    public String getResponsiblePerson() { return responsiblePerson; }
    public LocalDateTime getDateTime() { return dateTime; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        if (changeType == ChangeType.ADMINISTRATIVE_ACTION) {
            return "[" + dateTime + "] ADMIN ACTION | Property: " + propertyCode
                    + " | " + description + " | Responsible: " + responsiblePerson;
        }
        return "[" + dateTime + "] " + changeType + " | Property: " + propertyCode
                + " | Field: " + modifiedField
                + " | Previous: " + previousValue
                + " | New: " + newValue
                + " | Responsible: " + responsiblePerson;
    }
}