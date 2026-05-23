package proyectofinal.Inmueble;

import proyectofinal.Personal.Advisor;

public class Property implements Comparable<Property> {
    private String code;
    private String address;
    private String city;
    private ZoneProperty zone;
    private TypeProperty propertyType;
    private String purpose; // venta o arriendo
    private double price;
    private double area;
    private int rooms;
    private int bathrooms;
    private String propertyStatus;
    private boolean isAvailable;
    private Advisor responsibleAdvisor;
    private int priceChangeCount = 0;

    public Property(String code, String address, String city, ZoneProperty zone, TypeProperty propertyType,
            String purpose, double price, double area, int rooms, int bathrooms,
            String propertyStatus, boolean isAvailable, Advisor responsibleAdvisor) {
        this.code = code;
        this.address = address;
        this.city = city;
        this.zone = zone;
        this.propertyType = propertyType;
        this.purpose = purpose;
        this.price = price;
        this.area = area;
        this.rooms = rooms;
        this.bathrooms = bathrooms;
        this.propertyStatus = propertyStatus;
        this.isAvailable = isAvailable;
        this.responsibleAdvisor = responsibleAdvisor;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public ZoneProperty getZone() {
        return zone;
    }

    public void setZone(ZoneProperty zone) {
        this.zone = zone;
    }

    public TypeProperty getType() {
        return propertyType;
    }

    public void setType(TypeProperty propertyType) {
        this.propertyType = propertyType;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public double getPrice() {
        return price;
    }

   public void setPrice(double newPrice) {
        if (this.price != newPrice) {
            this.price = newPrice;
            this.priceChangeCount++;
        }
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public String getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(String propertyStatus) {
        this.propertyStatus = propertyStatus;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Advisor getResponsibleAdvisor() {
        return responsibleAdvisor;
    }

    public void setResponsibleAdvisor(Advisor responsibleAdvisor) {
        this.responsibleAdvisor = responsibleAdvisor;
    }

    @Override
    public String toString() {
        return "Code: " + code + " | Address: " + address + " | City: " + city + " | zone: "
                + zone + " | Property Type: " + propertyType + " | Purpose: " + purpose + " | Price: $"
                + price + " | Area: " + area + " m² | Rooms: " + rooms + " | Bathrooms: "
                + bathrooms + " | Status: " + propertyStatus + " | Available: " + (isAvailable ? "Yes" : "No")
                + " | Responsible Advisor: " + responsibleAdvisor;
    }

    @Override
    public int compareTo(Property other) {
       // Ordenamos de menor a mayor precio
        if (this.price < other.price) return -1;
        if (this.price > other.price) return 1;
        

        return this.code.compareTo(other.code);
    }

    public int getPriceChangeCount() {
        return priceChangeCount;
    }

    public void setPriceChangeCount(int priceChangeCount) {
        this.priceChangeCount = priceChangeCount;
    }

}