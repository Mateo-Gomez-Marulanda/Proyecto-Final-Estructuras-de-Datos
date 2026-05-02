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
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;

/**
 * Clase encargada de la persistencia de datos en archivos .txt
 * Centraliza el guardado y carga de Inmuebles, Clientes e Historial de Visitas.
 */
public class PersistenceManager {

    private static final String PROP_FILE = "properties.txt";
    private static final String CLIENT_FILE = "clients.txt";
    private static final String VISIT_FILE = "visits.txt";
    private static final String ADVISOR_FILE = "advisors.txt";

    // =========================================================
    // MÉTODOS DE GUARDADO (SAVE)
    // =========================================================

    public static void saveAll(PropertyManager pm, SimpleLinkedList<Client> clients, SimpleLinkedList<Advisor> advisors,
            VisitManager vm) {
        saveAdvisors(advisors);
        saveClients(clients);
        saveProperties(pm);
        saveVisitHistory(vm.getVisitHistory());
        System.out.println(">>> [SISTEMA] Datos guardados exitosamente.");
    }

    private static void saveAdvisors(SimpleLinkedList<Advisor> advisors) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ADVISOR_FILE))) {
            for (Advisor a : advisors) {
                // Formato: id;nombre;contacto;especialidad;visitas;cierres
                writer.println(String.format("%s;%s;%s;%s;%s;%d",
                        a.getId(), a.getName(), a.getContactInfo(),
                        a.getZoneSpecialty(), a.getScheduledVisits(), a.getCompletedClosings()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveProperties(PropertyManager manager) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROP_FILE))) {
            for (Property p : manager.getProperties()) {
                // Guardamos el código del asesor al final para poder buscarlo al cargar
                String advisorID = (p.getResponsibleAdvisor() != null) ? p.getResponsibleAdvisor().getId() : "NONE";

                writer.println(String.format("%s;%s;%s;%s;%s;%s;%.2f;%.2f;%d;%d;%s;%b;%s",
                        p.getCode(), // d[0]
                        p.getAddress(), // d[1]
                        p.getCity(), // d[2]
                        p.getNeighborhood(), // d[3]
                        p.getType(), // d[4] (Enum)
                        p.getPurpose(), // d[5]
                        p.getPrice(), // d[6]
                        p.getArea(), // d[7]
                        p.getRooms(), // d[8]
                        p.getBathrooms(), // d[9]
                        p.getPropertyStatus(), // d[10]
                        p.isAvailable(), // d[11]
                        advisorID // d[12]
                ));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar inmuebles: " + e.getMessage());
        }
    }

    private static void saveClients(SimpleLinkedList<Client> clients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENT_FILE))) {
            for (Client c : clients) {
                // Importante: Mantener exactamente el mismo orden que usaremos en la carga
                // (Load)
                writer.println(String.format("%s;%s;%s;%s;%s;%.2f;%s;%s;%d;%s;%s",
                        c.getId(), // d[0]
                        c.getName(), // d[1]
                        c.getEmail(), // d[2]
                        c.getPhoneNumber(), // d[3]
                        c.getClientType(), // d[4]
                        c.getBudget(), // d[5]
                        c.getInterestZones(), // d[6]
                        c.getDesiredPropertyType(), // d[7] (Enum se guarda como String)
                        c.getMinRooms(), // d[8]
                        c.getSearchStatus(), // d[9]
                        c.getPassword() // d[10]
                ));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar clientes: " + e.getMessage());
        }
    }

    private static void saveVisitHistory(SimpleLinkedList<VisitRequest> history) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VISIT_FILE))) {
            for (VisitRequest v : history) {
                // Formato: idCliente;codInmueble;fecha;estado;notas
                // Limpiamos las notas de posibles ";" para no romper el split en la carga
                String cleanNotes = v.getNotes().replace(";", ",");
                writer.println(String.format("%s;%s;%s;%s;%s",
                        v.getClient().getId(),
                        v.getProperty().getCode(),
                        v.getDateTime().toString(),
                        v.getStatus(),
                        cleanNotes));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar historial de visitas: " + e.getMessage());
        }
    }

    // =========================================================
    // MÉTODOS DE CARGA (LOAD)
    // =========================================================

    public static void loadAll(PropertyManager pm, SimpleLinkedList<Client> clients, SimpleLinkedList<Advisor> advisors,
            VisitManager vm) {
        loadAdvisors(advisors);
        loadClients(clients);
        loadProperties(pm, advisors);
        loadVisitHistory(vm, pm, clients);
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

                // Conversión de datos
                TypeProperty type = TypeProperty.valueOf(d[4]);
                double price = Double.parseDouble(d[6]);
                double area = Double.parseDouble(d[7]);
                int rooms = Integer.parseInt(d[8]);
                int bathrooms = Integer.parseInt(d[9]);
                boolean available = Boolean.parseBoolean(d[11]);

                // Buscamos el asesor responsable real (Referencia)
                Advisor advisor = findAdvisorById(advisors, d[12]);

                // Creamos el objeto completo
                Property p = new Property(
                        d[0], d[1], d[2], d[3], type, d[5],
                        price, area, rooms, bathrooms, d[10], available, advisor);

                // IMPORTANTE: registrar en el manager para llenar Hash y Árbol
                pm.registerProperty(p, "System_Load");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar inmuebles: " + e.getMessage());
        }
    }

    // Auxiliar para encontrar al asesor
    private static Advisor findAdvisorById(SimpleLinkedList<Advisor> advisors, String id) {
        if (id.equals(null))
            return null;
        for (Advisor a : advisors) {
            if (a.getId().equals(id))
                return a;
        }
        return null;
    }

    private static void loadClients(SimpleLinkedList<Client> clients) {
        File file = new File(CLIENT_FILE);
        if (!file.exists())
            return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty())
                    continue;

                String[] d = line.split(";");

                // PARSING DE TIPOS COMPLEJOS:
                double budget = Double.parseDouble(d[5]);

                // Para el Enum: TypeProperty.valueOf convierte el String de vuelta al Enum
                TypeProperty type = TypeProperty.valueOf(d[7]);

                int rooms = Integer.parseInt(d[8]);

                // Creamos el objeto con TODOS sus atributos originales
                Client c = new Client(
                        d[0], // id
                        d[1], // name
                        d[2], // email
                        d[3], // phoneNumber
                        d[4], // clientType
                        budget,
                        d[6], // interestZones (se carga como String/Object)
                        type,
                        rooms,
                        d[9], // searchStatus
                        d[10] // password
                );

                clients.add(c);
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
                String[] d = scanner.nextLine().split(";");
                // Constructor: id, name, contact, zone, visits, closings
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

                // Buscamos las referencias de objetos reales para mantener la integridad
                Client c = findClientById(clients, d[0]);
                Property p = pm.findByCode(d[1]);

                if (c != null && p != null) {
                    VisitRequest v = new VisitRequest(c, p, LocalDateTime.parse(d[2]));
                    if (d[3].equals("COMPLETED"))
                        v.markAsCompleted();
                    else if (d[3].equals("CANCELLED"))
                        v.markAsCancelled();
                    else if (d[3].equals("CONFIRM"))
                        v.markAsConfirm();
                    else if (d[3].equals("RECHEDULED"))
                        v.markAsRecheduled();

                    v.setNotes(d[4]);
                    vm.getVisitHistory().add(v);
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar historial de visitas: " + e.getMessage());
        }
    }

    // --- FUNCIÓN AUXILIAR ---
    private static Client findClientById(SimpleLinkedList<Client> clients, String id) {
        for (Client c : clients) {
            if (c.getId().equals(id))
                return c;
        }
        return null;
    }
}