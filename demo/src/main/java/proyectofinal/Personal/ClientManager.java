package proyectofinal.Personal;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.EstructurasDeDatos.TablasHash.HashTable;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;

public class ClientManager {
    private Client current;
    private SimpleLinkedList<Client> clients;
    private HashTable<String, Client> clientTable;

    public ClientManager() {
        this.clients = new SimpleLinkedList<>();
        this.clientTable = new HashTable<>(10);
    }

    public void login(String id, String password) {
        Client found = clientTable.get(id);
        if (found == null)
            throw new RuntimeException("Usuario no encontrado.");
        if (!found.getPassword().equals(password))
            throw new RuntimeException("Contraseña incorrecta.");

        this.current = found;
    }

    // Registro rápido para GUI: incluye validación de seguridad de contraseña
    public void registerBasic(String id, String name, String email, String password, String phoneNumber) {
        if (clientTable.containsKey(id))
            throw new RuntimeException("El ID ya existe.");

        validatePassword(password);

        Client newClient = new Client(id, name, email, password, phoneNumber);
        clients.add(newClient);
        clientTable.put(id, newClient);
    }

    // Registro completo: útil para la carga masiva desde archivos persistentes
    public void registerFull(String id, String name, String email, String phoneNumber, String clientType,
            double budget, String interestZones, String interestCity, TypeProperty desiredPropertyType, int minRooms,
            String searchStatus, String password) {

        Client newClient = new Client(id, name, email, phoneNumber, clientType, budget,
                interestZones, interestCity, desiredPropertyType, minRooms, searchStatus, password);
        clients.add(newClient);
        clientTable.put(id, newClient);
    }

    // Valida requisitos mínimos de seguridad antes de crear el objeto
    private void validatePassword(String password) {
        if (password == null || password.length() < 8)
            throw new RuntimeException("Contraseña debe tener al menos 8 caracteres.");

        boolean hasDigit = false, hasUpper = false, hasLower = false;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c))
                hasDigit = true;
            else if (Character.isUpperCase(c))
                hasUpper = true;
            else if (Character.isLowerCase(c))
                hasLower = true;
        }

        if (!hasDigit || !hasUpper || !hasLower) {
            throw new RuntimeException("La contraseña requiere mayúscula, minúscula y un número.");
        }
    }

    // Elimina de forma síncrona en Lista y HashTable para mantener integridad
    public void deleteAccount() {
        if (current == null)
            throw new RuntimeException("No hay sesión activa.");

        String idToDelete = current.getId();
        clients.remove(current);
        clientTable.remove(idToDelete);
        logout();
    }

    public void updateClient(String email, String phoneNumber, double budget,
            String interestZones, String interestCity, TypeProperty desiredPropertyType, int minRooms) {
        if (current == null)
            throw new RuntimeException("No hay sesión activa para actualizar.");

        current.setEmail(email);
        current.setPhoneNumber(phoneNumber);
        current.setBudget(budget);
        current.setInterestZones(interestZones);
        current.setInterestCity(interestCity);
        current.setDesiredPropertyType(desiredPropertyType);
        current.setMinRooms(minRooms);
    }

    // Filtra el inventario basado en el perfil del cliente loggeado
    public SimpleLinkedList<Property> getRecommendations(SimpleLinkedList<Property> inventory) {
        if (current == null)
            return new SimpleLinkedList<>();

        SimpleLinkedList<Property> matches = new SimpleLinkedList<>();
        double maxBudget = current.getBudget() * 1.10; // Margen del 10%

        for (Property p : inventory) {
            // Si no está disponible o supera el presupuesto máximo, se ignora
            if (!p.isAvailable() || p.getPrice() > maxBudget
                    || !p.getCity().equalsIgnoreCase(current.getInterestCity())) {
                continue;
            }

            int score = 0;

            // Sumamos puntos por cada coincidencia
            if (p.getZone().name().equalsIgnoreCase(current.getInterestZones())) {
                score += 5;
            }
            if (p.getType().equals(current.getDesiredPropertyType())) {
                score += 3;
            }
            if (p.getRooms() >= current.getMinRooms()) {
                score += 2;
            }
            if (p.getPrice() <= current.getBudget()) {
                score += 1; // Punto extra por estar en el presupuesto ideal sin el 10%
            }

            // Si cumple con el umbral mínimo de afinidad, se añade directamente
            if (score >= 5) {
                matches.add(p);
            }
        }

        return matches;
    }

    public void logout() {
        this.current = null;
    }

    public Client getCurrent() {
        return current;
    }

    public SimpleLinkedList<Client> getAllClients() {
        return clients;
    }

    public HashTable<String, Client> getClientTable() {
        return clientTable;
    }

    public void setClientTable(HashTable<String, Client> clientTable) {
        this.clientTable = clientTable;
    }

    public void markAsFavorite(Property p) {
        if (current == null)
            throw new RuntimeException("No hay sesión activa.");
        if (current.getFavoriteProperties().indexOf(p) == -1) {
            current.getFavoriteProperties().add(p);
        }
    }
}