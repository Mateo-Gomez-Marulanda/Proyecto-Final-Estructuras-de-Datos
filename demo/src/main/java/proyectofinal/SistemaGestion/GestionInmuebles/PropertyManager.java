package proyectofinal.SistemaGestion.GestionInmuebles;

import proyectofinal.EstructurasDeDatos.Pilas.Stack;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyChange.ChangeType;

public class PropertyManager {

    // Catálogo principal de inmuebles
    private SimpleLinkedList<Property> properties;

    // Pila 1: deshacer cambios en publicaciones (precio, área, habitaciones, etc.)
    private Stack<PropertyChange> modificationHistory;

    // Pila 2: revertir cambios de estado (estadoInmueble, disponibilidad)
    private Stack<PropertyChange> statusHistory;

    // Pila 3: historial de acciones administrativas
    private Stack<PropertyChange> adminActionsHistory;

    public PropertyManager() {
        this.properties = new SimpleLinkedList<>();
        this.modificationHistory = new Stack<>();
        this.statusHistory = new Stack<>();
        this.adminActionsHistory = new Stack<>();
    }

    // CRUD básico
    public void registerProperty(Property property, String responsiblePerson) {
        properties.addLast(property);
        registerAdminAction(property.getCode(),
                "Property registered in the system", responsiblePerson);
    }

    public Property findByCode(String code) {
        for (Property p : properties) {
            if (p.getCode().equals(code)) return p;
        }
        return null;
    }

    // Pila 1: modificaciones de publicación (precio, área, habitaciones, etc.)
    public void modifyPrice(String code, double newPrice, String responsiblePerson) {
        Property property = findByCode(code);
        if (property == null) throw new RuntimeException("Property not found: " + code);

        PropertyChange change = new PropertyChange(
                code, ChangeType.FIELD_MODIFICATION,
                "price", property.getPrice(), newPrice, responsiblePerson);

        property.setPrice(newPrice);
        modificationHistory.push(change);
    }

    public void modifyArea(String code, double newArea, String responsiblePerson) {
        Property property = findByCode(code);
        if (property == null) throw new RuntimeException("Property not found: " + code);

        PropertyChange change = new PropertyChange(
                code, ChangeType.FIELD_MODIFICATION,
                "area", property.getArea(), newArea, responsiblePerson);

        property.setArea(newArea);
        modificationHistory.push(change);
    }

    public void modifyRooms(String code, int newRoomCount, String responsiblePerson) {
        Property property = findByCode(code);
        if (property == null) throw new RuntimeException("Property not found: " + code);

        PropertyChange change = new PropertyChange(
                code, ChangeType.FIELD_MODIFICATION,
                "rooms", property.getRooms(), newRoomCount, responsiblePerson);

        property.setRooms(newRoomCount);
        modificationHistory.push(change);
    }

    /**
     * Deshace el último cambio realizado en publicaciones.
     * Restaura el valor anterior del campo modificado.
     */
    public PropertyChange undoLastModification() {
        if (modificationHistory.isEmpty()) {
            throw new RuntimeException("No modifications to undo");
        }

        PropertyChange last = modificationHistory.pop();
        Property property = findByCode(last.getPropertyCode());
        if (property == null) return last; // ya no existe, solo se retorna el registro

        switch (last.getModifiedField()) {
            case "price":
                property.setPrice((double) last.getPreviousValue());
                break;
            case "area":
                property.setArea((double) last.getPreviousValue());
                break;
            case "rooms":
                property.setRooms((int) last.getPreviousValue());
                break;
        }
        return last;
    }

    // Pila 2: cambios de estado de la propiedad
    public void changePropertyStatus(String code, String newStatus, String responsiblePerson) {
        Property property = findByCode(code);
        if (property == null) throw new RuntimeException("Property not found: " + code);

        PropertyChange change = new PropertyChange(
                code, ChangeType.STATUS_CHANGE,
                "propertyStatus", property.getPropertyStatus(), newStatus, responsiblePerson);

        property.setPropertyStatus(newStatus);
        statusHistory.push(change);
    }

    public void changeAvailability(String code, boolean newAvailability, String responsiblePerson) {
        Property property = findByCode(code);
        if (property == null) throw new RuntimeException("Property not found: " + code);

        PropertyChange change = new PropertyChange(
                code, ChangeType.STATUS_CHANGE,
                "isAvailable", property.isAvailable(), newAvailability, responsiblePerson);

        property.setAvailable(newAvailability);
        statusHistory.push(change);
    }

    /**
     * Revierte el último cambio de estado de cualquier propiedad.
     */
    public PropertyChange revertLastStatusChange() {
        if (statusHistory.isEmpty()) {
            throw new RuntimeException("No status changes to revert");
        }

        PropertyChange last = statusHistory.pop();
        Property property = findByCode(last.getPropertyCode());
        if (property == null) return last;

        switch (last.getModifiedField()) {
            case "propertyStatus":
                property.setPropertyStatus((String) last.getPreviousValue());
                break;
            case "isAvailable":
                property.setAvailable((boolean) last.getPreviousValue());
                break;
        }
        return last;
    }

    // Pila 3: historial de acciones administrativas
    public void registerAdminAction(String propertyCode, String description, String responsiblePerson) {
        PropertyChange action = new PropertyChange(propertyCode, description, responsiblePerson);
        adminActionsHistory.push(action);
    }

    public void printActionHistory() {
        if (adminActionsHistory.isEmpty()) {
            System.out.println("No administrative actions registered.");
            return;
        }
        System.out.println("=== ADMINISTRATIVE ACTIONS HISTORY (most recent first) ===");
        for (PropertyChange action : adminActionsHistory) {
            System.out.println(action);
        }
    }

    // Utilidades de consulta
    public PropertyChange peekLastModification() {
        return modificationHistory.isEmpty() ? null : modificationHistory.peek();
    }

    public PropertyChange peekLastStatusChange() {
        return statusHistory.isEmpty() ? null : statusHistory.peek();
    }

    public int getTotalPendingModifications() {
        return modificationHistory.size();
    }

    public SimpleLinkedList<Property> getProperties() {
        return properties;
    }
}