package proyectofinal.Personal;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;

public class Client {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String clientType;
    private double budget;
    private TypeProperty desiredPropertyType;
    private int minRooms;
    private String searchStatus;
    private String password;
    private String interestZones;
    private SimpleLinkedList<Property> favoriteProperties = new SimpleLinkedList<>();
    private SimpleLinkedList<Property> visitedPropertyHistory = new SimpleLinkedList<>();

    // Constructor para Registro Inicial (GUI de Registro)
    public Client(String id, String name, String email, String password, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;

        // Valores por defecto para evitar errores en lógica o persistencia
        this.clientType = "POTENTIAL";
        this.searchStatus = "INACTIVE";
        this.budget = 0.0;
        this.minRooms = 0;
        this.interestZones= "Sin definir";
    }

    public Client(String id, String name, String email, String phoneNumber, String clientType,
            double budget, String interestZones, TypeProperty desiredPropertyType, int minRooms,
            String searchStatus, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.clientType = clientType;
        this.budget = budget;
        this.desiredPropertyType = desiredPropertyType;
        this.minRooms = minRooms;
        this.searchStatus = searchStatus;
        this.password = password;
        this.interestZones = interestZones;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public String getInterestZones() {
        return interestZones;
    }

    public void setInterestZones(String interestZones) {
        this.interestZones = interestZones;
    }

    public TypeProperty getDesiredPropertyType() {
        return desiredPropertyType;
    }

    public void setDesiredPropertyType(TypeProperty desiredPropertyType) {
        this.desiredPropertyType = desiredPropertyType;
    }

    public int getMinRooms() {
        return minRooms;
    }

    public void setMinRooms(int minRooms) {
        this.minRooms = minRooms;
    }

    public String getSearchStatus() {
        return searchStatus;
    }

    public void setSearchStatus(String searchStatus) {
        this.searchStatus = searchStatus;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public SimpleLinkedList<Property> getFavoriteProperties() {
        return favoriteProperties;
    }

    public void setFavoriteProperties(SimpleLinkedList<Property> favoriteProperties) {
        this.favoriteProperties = favoriteProperties;
    }

    public SimpleLinkedList<Property> getVisitedPropertiesHistory() {
        return visitedPropertyHistory;
    }

    public void setVisitedPropertiesHistory(SimpleLinkedList<Property> history) {
        this.visitedPropertyHistory = history;
    }

    @Override
    public String toString() {
        return String.format("ID: %s | Name: %s | Status: %s | Type: %s",
                id, name, searchStatus, clientType);
    }

    public String toFileLine() {
        return String.join(";",
                id,
                name,
                email,
                (phoneNumber != null ? phoneNumber : "N/A"),
                clientType,
                String.valueOf(budget),
                (interestZones != null ? interestZones.toString() : "NONE"),
                (desiredPropertyType != null ? desiredPropertyType.name() : "NONE"),
                String.valueOf(minRooms),
                searchStatus,
                password);
    }
}