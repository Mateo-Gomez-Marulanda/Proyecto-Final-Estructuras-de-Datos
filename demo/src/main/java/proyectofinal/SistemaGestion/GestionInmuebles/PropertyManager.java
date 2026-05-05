package proyectofinal.SistemaGestion.GestionInmuebles;

import proyectofinal.EstructurasDeDatos.Pilas.Stack;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.EstructurasDeDatos.TablasHash.HashTable;
import proyectofinal.EstructurasDeDatos.Arboles.Tree;
import proyectofinal.Inmueble.Property;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyChange.ChangeType;

public class PropertyManager {

    // 1. Catálogo principal (Requisito 5.1)
    private SimpleLinkedList<Property> properties;

    // 2. Búsqueda rápida por código O(1) (Requisito 5.5)
    private HashTable<String, Property> propertyTable;

    // 3. Ordenamiento y rangos por precio O(log n) (Requisito 5.6)
    private Tree<Property> priceTree;

    // 4. Pilas para Deshacer/Historial (Requisito 5.2)
    private Stack<PropertyChange> modificationHistory;
    private Stack<PropertyChange> statusHistory;
    private Stack<PropertyChange> adminActionsHistory;

    public PropertyManager() {
        this.properties = new SimpleLinkedList<>();
        this.propertyTable = new HashTable<>(50);
        this.priceTree = new Tree<>();
        this.modificationHistory = new Stack<>();
        this.statusHistory = new Stack<>();
        this.adminActionsHistory = new Stack<>();
    }


    public void registerProperty(Property property, String responsiblePerson) {
        if (propertyTable.containsKey(property.getCode())) {
            throw new RuntimeException("El código del inmueble ya existe: " + property.getCode());
        }

        // Insertar en todas las estructuras para mantener consistencia
        properties.add(property);
        propertyTable.put(property.getCode(), property);
        priceTree.put(property); 

        registerAdminAction(property.getCode(), "Registro inicial de propiedad", responsiblePerson);
    }

    public Property findByCode(String code) {
        return propertyTable.get(code);
    }

    public void modifyPrice(String code, double newPrice, String responsiblePerson) {
        Property property = findByCode(code);
        if (property == null) throw new RuntimeException("Inmueble no encontrado.");

        PropertyChange change = new PropertyChange(
                code, ChangeType.FIELD_MODIFICATION,
                "price", property.getPrice(), newPrice, responsiblePerson);

        priceTree.remove(property); 
        property.setPrice(newPrice);
        priceTree.put(property); 

        modificationHistory.push(change);
    }

    public void modifyArea(String code, double newArea, String responsiblePerson) {
        Property property = findByCode(code);
        if (property == null) throw new RuntimeException("Inmueble no encontrado.");

        PropertyChange change = new PropertyChange(
                code, ChangeType.FIELD_MODIFICATION,
                "area", property.getArea(), newArea, responsiblePerson);

        property.setArea(newArea);
        modificationHistory.push(change);
    }


    public PropertyChange undoLastModification() {
        if (modificationHistory.isEmpty()) throw new RuntimeException("No hay modificaciones para deshacer");

        PropertyChange last = modificationHistory.pop();
        Property property = findByCode(last.getPropertyCode());
        
        if (property != null) {
            switch (last.getModifiedField()) {
                case "price":
                    priceTree.remove(property);
                    property.setPrice((double) last.getPreviousValue());
                    priceTree.put(property);
                    break;
                case "area":
                    property.setArea((double) last.getPreviousValue());
                    break;
                case "rooms":
                    property.setRooms((int) last.getPreviousValue());
                    break;
            }
        }
        return last;
    }


    public void changePropertyStatus(String code, String newStatus, String responsiblePerson) {
        Property property = findByCode(code);
        if (property == null) throw new RuntimeException("Inmueble no encontrado.");

        PropertyChange change = new PropertyChange(
                code, ChangeType.STATUS_CHANGE,
                "propertyStatus", property.getPropertyStatus(), newStatus, responsiblePerson);

        property.setPropertyStatus(newStatus);
        statusHistory.push(change);
    }

    public PropertyChange revertLastStatusChange() {
        if (statusHistory.isEmpty()) throw new RuntimeException("No hay cambios de estado para revertir");

        PropertyChange last = statusHistory.pop();
        Property property = findByCode(last.getPropertyCode());
        
        if (property != null) {
            if (last.getModifiedField().equals("propertyStatus")) {
                property.setPropertyStatus((String) last.getPreviousValue());
            } else if (last.getModifiedField().equals("isAvailable")) {
                property.setAvailable((boolean) last.getPreviousValue());
            }
        }
        return last;
    }

    public void registerAdminAction(String propertyCode, String description, String responsiblePerson) {
        PropertyChange action = new PropertyChange(propertyCode, description, responsiblePerson);
        adminActionsHistory.push(action);
    }

    public SimpleLinkedList<Property> getProperties() {
        return properties;
    }

    public Tree<Property> getPriceTree() {
        return priceTree;
    }
}