package proyectofinal.SistemaGestion.Persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Scanner;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.Client;
import proyectofinal.Personal.ClientManager; // Importamos el Manager
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;

public class PersistenceManager {

    private static final String PROP_FILE = "properties.txt";
    private static final String CLIENT_FILE = "clients.txt";
    private static final String VISIT_FILE = "visits.txt";
    private static final String ADVISOR_FILE = "advisors.txt";

    // Modificado para recibir ClientManager en lugar de solo la lista
    public static void saveAll(PropertyManager pm, ClientManager cm, SimpleLinkedList<Advisor> advisors,
            VisitManager vm) {
        saveAdvisors(advisors);
        saveClients(cm.getAllClients()); // Extraemos la lista del manager para guardarla
        saveProperties(pm);
        saveVisitHistory(vm.getVisitHistory());
        System.out.println(">>> [SISTEMA] Datos guardados exitosamente.");
    }

    private static void saveAdvisors(SimpleLinkedList<Advisor> advisors) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ADVISOR_FILE))) {
            for (Advisor a : advisors) {
                writer.println(String.format("%s;%s;%s;%s;%s;%d",
                        a.getId(), a.getName(), a.getContactInfo(),
                        a.getZoneSpecialty(), a.getScheduledVisits(), a.getCompletedClosings()));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar asesores: " + e.getMessage());
        }
    }

    private static void saveProperties(PropertyManager manager) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROP_FILE))) {
            for (Property p : manager.getProperties()) {
                String advisorID = (p.getResponsibleAdvisor() != null) ? p.getResponsibleAdvisor().getId() : "NONE";
                writer.println(String.format("%s;%s;%s;%s;%s;%s;%.2f;%.2f;%d;%d;%s;%b;%s",
                        p.getCode(), p.getAddress(), p.getCity(), p.getZone(), p.getType(), p.getPurpose(),
                        p.getPrice(), p.getArea(), p.getRooms(), p.getBathrooms(), p.getPropertyStatus(),
                        p.isAvailable(), advisorID));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar inmuebles: " + e.getMessage());
        }
    }

    private static void saveClients(SimpleLinkedList<Client> clients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENT_FILE))) {
            for (Client c : clients) {
                writer.println(String.format("%s;%s;%s;%s;%s;%.2f;%s;%s;%d;%s;%s",
                        c.getId(), c.getName(), c.getEmail(), c.getPhoneNumber(), c.getClientType(),
                        c.getBudget(), c.getInterestZones(), c.getDesiredPropertyType(), c.getMinRooms(),
                        c.getSearchStatus(), c.getPassword()));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar clientes: " + e.getMessage());
        }
    }

    private static void saveVisitHistory(SimpleLinkedList<VisitRequest> history) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VISIT_FILE))) {
            for (VisitRequest v : history) {
                String cleanNotes = v.getNotes() != null ? v.getNotes().replace(";", ",") : "";
                writer.println(String.format("%s;%s;%s;%s;%s",
                        v.getClient().getId(), v.getProperty().getCode(),
                        v.getDateTime().toString(), v.getStatus(), cleanNotes));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar historial de visitas: " + e.getMessage());
        }
    }

    // =========================================================
    // MÉTODOS DE CARGA REFACTORIZADOS
    // =========================================================

    public static void loadAll(PropertyManager pm, ClientManager cm, SimpleLinkedList<Advisor> advisors,
            VisitManager vm) {
        loadAdvisors(advisors);
        loadClients(cm); // Pasamos el manager completo para la carga
        loadProperties(pm, advisors);
        loadVisitHistory(vm, pm, cm.getAllClients());
        System.out.println(">>> [SISTEMA] Datos cargados exitosamente.");
    }

    public static void loadProperties(PropertyManager pm, SimpleLinkedList<Advisor> advisors) {
        File file = new File(PROP_FILE);
        if (!file.exists())
            return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty())
                    continue;
                String[] d = line.split(";");

                TypeProperty type = TypeProperty.valueOf(d[4].toUpperCase());
                // Previene caídas si viene con coma regional
                double price = Double.parseDouble(d[6].replace(",", "."));
                double area = Double.parseDouble(d[7].replace(",", "."));
                int rooms = Integer.parseInt(d[8]);
                int bathrooms = Integer.parseInt(d[9]);
                boolean available = Boolean.parseBoolean(d[11]);

                Advisor advisor = findAdvisorById(advisors, d[12]);

                Property p = new Property(d[0], d[1], d[2], d[3], type, d[5],
                        price, area, rooms, bathrooms, d[10], available, advisor);

                pm.registerProperty(p, "System_Load");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar inmuebles: " + e.getMessage());
        }
    }

    private static Advisor findAdvisorById(SimpleLinkedList<Advisor> advisors, String id) {
        if (id == null || id.equals("NONE"))
            return null;
        for (Advisor a : advisors) {
            if (a.getId().equals(id))
                return a;
        }
        return null;
    }

    private static void loadClients(ClientManager cm) {
    File file = new File(CLIENT_FILE);
    if (!file.exists()) return;

    try (Scanner scanner = new Scanner(file)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) continue;

            String[] d = line.split(";");

            // SEGURIDAD: Si la línea pertenece a un rol administrativo, se ignora
            String tipoCliente = d[4].toUpperCase();
            if (tipoCliente.equals("ADMIN") || tipoCliente.equals("ADVISOR") || tipoCliente.equals("ASESOR")) {
                continue;
            }

            // 1. Parseos seguros con los nuevos índices desplazados
            double budget     = Double.parseDouble(d[5].replace(",", "."));
            String zones      = d[6];
            String city       = d[7]; // <--- NUEVO: d[7] ahora es la ciudad de interés
            
            // Los índices de aquí en adelante aumentan en +1 debido al desplazamiento
            TypeProperty type = TypeProperty.valueOf(d[8].toUpperCase()); 
            int rooms         = Integer.parseInt(d[9]);
            String status     = d[10];
            String password   = d[11];

            // 2. Inyección al Manager utilizando la firma actualizada de registerFull
            cm.registerFull(
                    d[0],     // id
                    d[1],     // name
                    d[2],     // email
                    d[3],     // phoneNumber
                    tipoCliente,
                    budget,
                    zones,
                    city,     // <--- Pasamos la ciudad al constructor/manager
                    type,     
                    rooms,
                    status,   
                    password  
            );
        }
    } catch (Exception e) {
        System.err.println("Error al cargar cliente: " + e.getMessage());
    }
}

    private static void loadAdvisors(SimpleLinkedList<Advisor> advisors) {
        File file = new File(ADVISOR_FILE);
        if (!file.exists())
            return;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty())
                    continue;
                String[] d = line.split(";");
                advisors.add(new Advisor(d[0], d[1], d[2], d[3], d[4], Integer.parseInt(d[5])));
            }
        } catch (Exception e) {
            System.err.println("Error carga Asesores: " + e.getMessage());
        }
    }

    private static void loadVisitHistory(VisitManager vm, PropertyManager pm, SimpleLinkedList<Client> clients) {
        File file = new File(VISIT_FILE);
        if (!file.exists())
            return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty())
                    continue;
                String[] d = line.split(";");

                Client c = findClientById(clients, d[0]);
                Property p = pm.findByCode(d[1]);

                if (c != null && p != null) {
                    VisitRequest v = new VisitRequest(c, p, LocalDateTime.parse(d[2]));

                    // Normalización por si las strings difieren en mayúsculas/minúsculas
                    String status = d[3].toUpperCase();
                    if (status.equals("COMPLETED") || status.equals("REALIZADA"))
                        v.markAsCompleted();
                    else if (status.equals("CANCELLED") || status.equals("CANCELADA"))
                        v.markAsCancelled();
                    else if (status.equals("CONFIRM") || status.equals("CONFIRMADA"))
                        v.markAsConfirm();
                    else if (status.equals("RESCHEDULED") || status.equals("REPROGRAMADA"))
                        v.markAsRecheduled();

                    v.setNotes(d[4]);
                    vm.getVisitHistory().add(v);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar historial de visitas: " + e.getMessage());
        }
    }

    private static Client findClientById(SimpleLinkedList<Client> clients, String id) {
        for (Client c : clients) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }
}