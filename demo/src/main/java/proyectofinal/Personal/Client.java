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
    private Object interestZones; // data structure to be defined
    private TypeProperty desiredPropertyType;
    private int minRooms;
    private String searchStatus;

    private SimpleLinkedList<Property> favoriteProperties = new SimpleLinkedList<>();

    public Client(String id, String name, String email, String phoneNumber, String clientType,
            double budget, Object interestZones, TypeProperty desiredPropertyType, int minRooms,
            String searchStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.clientType = clientType;
        this.budget = budget;
        this.interestZones = interestZones;
        this.desiredPropertyType = desiredPropertyType;
        this.minRooms = minRooms;
        this.searchStatus = searchStatus;
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

    public Object getInterestZones() {
        return interestZones;
    }

    public void setInterestZones(Object interestZones) {
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

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Email: " + email + " | Phone: "
                + phoneNumber + " | Client Type: " + clientType + " | Budget: $" + budget
                + " | Interest Zones: " + interestZones + " | Desired Property Type: " + desiredPropertyType
                + " | Min Rooms: " + minRooms + " | Search Status: "
                + searchStatus;
    }

    public void searchProperties(Property property) {
        // Logic to search available properties based on client criteria
    }

    public void scheduleVisit() {
        // Logic to schedule a visit to a specific property
    }

    public void markAsFavorite(Property property) {
        favoriteProperties.addLast(property);
    }

    public void registerPurchaseRentIntent() {
        // Logic to register intent to purchase or rent a property
    }

    public void checkInteractionHistory() {
        // Logic to check the client's interaction history with the real estate agency
    }
}