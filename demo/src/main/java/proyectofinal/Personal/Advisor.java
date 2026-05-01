package proyectofinal.Personal;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;

public class Advisor {
    private String id;
    private String name;
    private String contactInfo;
    private String zoneSpecialty;
    private SimpleLinkedList<Property> assignedProperties;
    private Object scheduledVisits; // estructura de datos a definir
    private int completedClosings;

    public Advisor(String id, String name, String contactInfo, String zoneSpecialty,
            Object scheduledVisits, int completedClosings) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.zoneSpecialty = zoneSpecialty;
        this.assignedProperties = new SimpleLinkedList<>();
        this.scheduledVisits = scheduledVisits;
        this.completedClosings = completedClosings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getZoneSpecialty() {
        return zoneSpecialty;
    }

    public void setZoneSpecialty(String zoneSpecialty) {
        this.zoneSpecialty = zoneSpecialty;
    }

    public Object getScheduledVisits() {
        return scheduledVisits;
    }

    public void setScheduledVisits(Object scheduledVisits) {
        this.scheduledVisits = scheduledVisits;
    }

    public int getCompletedClosings() {
        return completedClosings;
    }

    public void setCompletedClosings(int completedClosings) {
        this.completedClosings = completedClosings;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Contact: " + contactInfo
                + " | Zone Specialty: " + zoneSpecialty + " | Assigned Properties: " + assignedProperties
                + " | Scheduled Visits: " + scheduledVisits + " | Completed Closings: " + completedClosings;
    }

    public void assignProperty(Property property) {
        assignedProperties.addLast(property);
    }

    public void removeProperty(Property property) {
        assignedProperties.removeElement(property);
    }

    public Property getAssignedProperty(String code) {
        for (Property property : assignedProperties) {
            if (property.getCode().equals(code)) {
                return property;
            }
        }
        return null; // No encontrado
    }
}