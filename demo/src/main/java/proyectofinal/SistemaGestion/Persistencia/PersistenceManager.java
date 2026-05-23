package proyectofinal.SistemaGestion.Persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Inmueble.ZoneProperty;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.Client;
import proyectofinal.Personal.ClientManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;
import proyectofinal.SistemaGestion.Contratos.Contract;
import proyectofinal.SistemaGestion.Contratos.ContractStatus;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;

public class PersistenceManager {

    private static final String PROP_FILE = "properties.txt";
    private static final String CLIENT_FILE = "clients.txt";
    private static final String VISIT_FILE = "visits.txt";
    private static final String ADVISOR_FILE = "advisors.txt";
    private static final String CONTRACT_FILE = "contracts.txt";

    public static void saveAll(PropertyManager pm, ClientManager cm, SimpleLinkedList<Advisor> advisors,
            VisitManager vm, SimpleLinkedList<Contract> contracts) {
        saveAdvisors(advisors);
        saveClients(cm.getAllClients());
        saveProperties(pm);
        saveVisitHistory(vm.getVisitHistory());
        saveContracts(contracts);
        System.out.println(">>> [SISTEMA] Datos guardados exitosamente.");
    }

    public static void loadAll(PropertyManager pm, ClientManager cm, SimpleLinkedList<Advisor> advisors,
            VisitManager vm, SimpleLinkedList<Contract> contracts) { // <- Agregamos la lista de contratos al cargador global
        loadAdvisors(advisors);
        loadClients(cm);
        loadProperties(pm, advisors);
        loadVisitHistory(vm, pm, cm.getAllClients());
        loadContracts(pm, advisors, cm.getAllClients(), contracts); // <- Se le inyecta la lista de contratos destino
        System.out.println(">>> [SISTEMA] Datos cargados exitosamente.");
    }

    // --- MÉTODOS DE GUARDADO ---

    private static void saveAdvisors(SimpleLinkedList<Advisor> advisors) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ADVISOR_FILE))) {
            for (Advisor a : advisors) {
                writer.println(String.format("%s;%s;%s;%s;%s;%d",
                        a.getId(), a.getName(), a.getContactInfo(),
                        a.getZoneSpecialty(), a.getScheduledVisits(), a.getCompletedClosings()));
            }
        } catch (IOException e) {
            System.err.println("Error asesores: " + e.getMessage());
        }
    }

    private static void saveProperties(PropertyManager manager) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PROP_FILE))) {
            for (Property p : manager.getProperties()) {
                String adv = (p.getResponsibleAdvisor() != null) ? p.getResponsibleAdvisor().getId() : "NONE";
                writer.println(String.format("%s;%s;%s;%s;%s;%s;%.2f;%.2f;%d;%d;%s;%b;%s;%d",
                        p.getCode(), p.getAddress(), p.getCity(), p.getZone(), p.getType(), p.getPurpose(),
                        p.getPrice(), p.getArea(), p.getRooms(), p.getBathrooms(), p.getPropertyStatus(),
                        p.isAvailable(), adv, p.getPriceChangeCount()));
            }
        } catch (IOException e) {
            System.err.println("Error inmuebles: " + e.getMessage());
        }
    }

    private static void saveClients(SimpleLinkedList<Client> clients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENT_FILE))) {
            for (Client c : clients)
                writer.println(c.toFileLine());
        } catch (IOException e) {
            System.err.println("Error clientes: " + e.getMessage());
        }
    }

    private static void saveVisitHistory(SimpleLinkedList<VisitRequest> history) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VISIT_FILE))) {
            for (VisitRequest v : history) {
                writer.println(String.format("%s;%s;%s;%s;%s",
                        v.getClient().getId(), v.getProperty().getCode(), v.getDateTime().toString(),
                        v.getStatus(), v.getNotes() != null ? v.getNotes().replace(";", ",") : ""));
            }
        } catch (IOException e) {
            System.err.println("Error visitas: " + e.getMessage());
        }
    }

    private static void saveContracts(SimpleLinkedList<Contract> contracts) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CONTRACT_FILE))) {
            for (Contract c : contracts) {
                String advId = (c.getApprovingAdvisor() != null) ? c.getApprovingAdvisor().getId() : "NONE";
                writer.println(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s",
                        c.getId(), c.getName(), c.getAddress(), c.getOwner().getId(),
                        c.getRelatedProperty().getCode(), c.getCreationDate().toString(),
                        c.getExpirationDate().toString(), advId,
                        c.getStatus().name()));
            }
        } catch (IOException e) {
            System.err.println("Error contratos: " + e.getMessage());
        }
    }

    // --- MÉTODOS DE CARGA ---

    private static void loadAdvisors(SimpleLinkedList<Advisor> advisors) {
        File file = new File(ADVISOR_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(";");
                advisors.add(new Advisor(d[0], d[1], d[2], d[3], d[4], Integer.parseInt(d[5])));
            }
        } catch (Exception e) {
            System.err.println("Error carga Asesores: " + e.getMessage());
        }
    }

    private static void loadClients(ClientManager cm) {
        File file = new File(CLIENT_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(";");
                if (d[4].equalsIgnoreCase("ADMIN") || d[4].equalsIgnoreCase("ADVISOR"))
                    continue;
                cm.registerFull(d[0], d[1], d[2], d[3], d[4], Double.parseDouble(d[5].replace(",", ".")),
                        d[6], d[7], TypeProperty.valueOf(d[8].toUpperCase()), Integer.parseInt(d[9]), d[10], d[11]);
            }
        } catch (Exception e) {
            System.err.println("Error carga clientes: " + e.getMessage());
        }
    }

    private static void loadProperties(PropertyManager pm, SimpleLinkedList<Advisor> advisors) {
        File file = new File(PROP_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty())
                    continue;

                String[] d = line.split(";");
                Advisor adv = findAdvisorById(advisors, d[12]);

                Property p = new Property(
                        d[0], d[1], d[2],
                        ZoneProperty.valueOf(d[3].toUpperCase()),
                        TypeProperty.valueOf(d[4].toUpperCase()),
                        d[5],
                        Double.parseDouble(d[6].replace(",", ".")),
                        Double.parseDouble(d[7].replace(",", ".")),
                        Integer.parseInt(d[8]),
                        Integer.parseInt(d[9]),
                        d[10],
                        Boolean.parseBoolean(d[11]),
                        adv);

                if (d.length > 13) {
                    p.setPriceChangeCount(Integer.parseInt(d[13]));
                }

                pm.registerProperty(p, "System_Load");
            }
        } catch (Exception e) {
            System.err.println("Error carga propiedades: " + e.getMessage());
        }
    }

    private static void loadContracts(PropertyManager pm, SimpleLinkedList<Advisor> advisors,
            SimpleLinkedList<Client> clients, SimpleLinkedList<Contract> contractsDestination) {
        File file = new File(CONTRACT_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(";");
                
                Client c = findClientById(clients, d[3]);
                Property p = pm.findByCode(d[4]);
                Advisor adv = findAdvisorById(advisors, d[7]);
                
                if (c != null && p != null) {
                    Contract ct = new Contract(d[0], d[1], d[2], c, p, LocalDate.parse(d[5]), LocalDate.parse(d[6]), adv);
                    ct.setStatus(ContractStatus.valueOf(d[8].toUpperCase()));
                    
                    // CORRECCIÓN 1: Agregar explícitamente el contrato a la lista de AppContext
                    contractsDestination.add(ct); 
                }
            }
        } catch (Exception e) {
            System.err.println("Error carga contratos: " + e.getMessage());
        }
    }

    private static void loadVisitHistory(VisitManager vm, PropertyManager pm, SimpleLinkedList<Client> clients) {
        File file = new File(VISIT_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(";");
                
                Client c = findClientById(clients, d[0]);
                Property p = pm.findByCode(d[1]);
                
                if (c != null && p != null) {
                    VisitRequest v = new VisitRequest(c, p, LocalDateTime.parse(d[2]));
                    
                    v.setStatus(d[3]); 
                    
                    v.setNotes(d.length > 4 ? d[4] : "");
                    vm.getVisitHistory().add(v);
                }
            }
        } catch (Exception e) {
            System.err.println("Error carga visitas: " + e.getMessage());
        }
    }

    private static Advisor findAdvisorById(SimpleLinkedList<Advisor> advisors, String id) {
        if ("NONE".equalsIgnoreCase(id)) return null;
        for (Advisor a : advisors)
            if (a.getId().equals(id))
                return a;
        return null;
    }

    private static Client findClientById(SimpleLinkedList<Client> clients, String id) {
        for (Client c : clients)
            if (c.getId().equals(id))
                return c;
        return null;
    }
}