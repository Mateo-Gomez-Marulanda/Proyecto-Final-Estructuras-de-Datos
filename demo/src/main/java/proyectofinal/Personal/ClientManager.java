package proyectofinal.Personal;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.EstructurasDeDatos.TablasHash.HashTable;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;

public class ClientManager {
    private Client current; // Instancia de el cliente que este loggeado en la aplicacion
    private SimpleLinkedList<Client> clients;
    private HashTable<String, Client> clientTable;

    public ClientManager() {
        this.clients = new SimpleLinkedList<>();
        this.clientTable = new HashTable<>(10);
    }

    public void login(String id, String password) {
        Client found = clientTable.get(id);

        if (found == null) {
            throw new RuntimeException("El usuario con ID " + id + " no existe.");
        }

        if (found.getPassword().equals(password)) {
            this.current = found;
        } else {
            throw new RuntimeException("Contraseña incorrecta.");
        }
    }

    public void setCurrent(Client client) {
        this.current = client;
    }

    public Client getCurrent() {
        return current;
    }

    public void logout() {
        this.current = null;
    }

    public boolean registerClient(String id, String name, String email, String phoneNumber, String clientType,
            double budget, Object interestZones, TypeProperty desiredPropertyType, int minRooms,
            String searchStatus, String password) {
        if (clientTable.containsKey(id)) {
            throw new RuntimeException("Error: El ID ya se encuentra registrado.");
        }
        
        try {
            Client newClient = new Client(id, name, email, phoneNumber, clientType, budget, 
                                          interestZones, desiredPropertyType, minRooms, searchStatus,password);
            
            clients.addLast(newClient);      // Para listado general
            clientTable.put(id, newClient); // Para búsquedas y login
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error en el registro: " + e.getMessage());
        }
    }

    public void deleteClient() {
        if(current == null){
            throw new RuntimeException("No hay sesion activa");
        }
        clients.removeElement(current);
        logout();
    }

    public Client findClientById(String id) {
        return clientTable.get(id);
    }

    public void updatePassword(String password){
        if(current != null){
            current.setPassword(password);
        }
    }

    public void updateClient(String email, String phoneNumber,
            double budget, Object interestZones, TypeProperty desiredPropertyType, int minRooms) {

        if (current != null) {
            current.setEmail(email);
            current.setPhoneNumber(phoneNumber);
            current.setBudget(budget);
            current.setDesiredPropertyType(desiredPropertyType);
            current.setInterestZones(interestZones);
            current.setMinRooms(minRooms);
        } else {
            throw new RuntimeException("No se pudo actualizar: Cliente no encontrado.");
        }
    }

    /**
     * REQUISITO 4.5: Favoritos.
     */
    public void markAsFavorite(Property p) {
        if (current == null) throw new RuntimeException("Inicie sesión para guardar favoritos.");
        
        if (current.getFavoriteProperties().indexOf(p) == -1) {
            current.getFavoriteProperties().addLast(p);
        }
    }

    /**
     * REQUISITO 4.8: Recomendación de inmuebles.
     * Cruza datos del perfil del cliente con el inventario disponible.
     */
    public SimpleLinkedList<Property> getRecommendations(SimpleLinkedList<Property> inventory) {
        if (current == null) return new SimpleLinkedList<>();

        SimpleLinkedList<Property> matches = new SimpleLinkedList<>();
        for (Property p : inventory) {
            // Lógica: coincide tipo, habitaciones y el precio no supera el 10% de su presupuesto
            boolean matchType = p.getType().equals(current.getDesiredPropertyType());
            boolean matchRooms = p.getRooms() >= current.getMinRooms();
            boolean matchPrice = p.getPrice() <= (current.getBudget() * 1.10);

            if (matchType && matchRooms && matchPrice) {
                matches.addLast(p);
            }
        }
        return matches;
    }

    public SimpleLinkedList<Client> getAllClients() {
        return clients;
    }

}
