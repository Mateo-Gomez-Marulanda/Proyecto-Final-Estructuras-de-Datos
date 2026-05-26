package proyectofinal.SistemaGestion.Persistencia;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Inmueble.ZoneProperty;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.Client;
import proyectofinal.Personal.ClientManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.Visit;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitStatus;
import proyectofinal.SistemaGestion.Contratos.Contract;
import proyectofinal.SistemaGestion.Contratos.ContractStatus;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;

public class PersistenceManager {

    private static final String PROP_FILE = "properties.txt";
    private static final String CLIENT_FILE = "clients.txt";
    private static final String VISIT_FILE = "visits.txt";
    private static final String ACTIVE_VISITS_FILE = "active_visits.txt";
    private static final String ADVISOR_FILE = "advisors.txt";
    private static final String CONTRACT_FILE = "contracts.txt";
    private static final String BUSINESS_OPERATION_FILE = "business_operations.txt";
    private static final String CLIENT_HISTORY_FILE = "client_history.txt";
    private static final String CLIENT_FAVORITES_FILE = "client_favorites.txt";

    public static void saveAll(PropertyManager pm, ClientManager cm, SimpleLinkedList<Advisor> advisors,
            VisitManager vm, SimpleLinkedList<Contract> contracts, SimpleLinkedList<BusinessOperation> operations) {
        saveAdvisors(advisors);
        saveClients(cm.getAllClients());
        saveProperties(pm);
        
        // Guardado de relaciones Many-to-Many de clientes
        saveClientHistories(cm.getAllClients());
        saveClientFavorites(cm.getAllClients());
        
        saveVisitHistory(vm.getVisitHistory());
        saveActiveVisits(vm); 
        saveContracts(contracts);
        
        // Guardar las operaciones transaccionales de la inmobiliaria
        saveBusinessOperations(operations);
        
        System.out.println(">>> [SISTEMA] Datos guardados exitosamente.");
    }

    public static void loadAll(PropertyManager pm, ClientManager cm, SimpleLinkedList<Advisor> advisors,
            VisitManager vm, SimpleLinkedList<Contract> contracts, SimpleLinkedList<BusinessOperation> operations) {
        loadAdvisors(advisors);
        loadClients(cm);
        loadProperties(pm, advisors);
        
        // Carga relacional (requiere clientes y propiedades en memoria)
        loadClientHistories(cm.getAllClients(), pm);
        loadClientFavorites(cm.getAllClients(), pm);
        
        loadVisitHistory(vm, pm, cm.getAllClients());
        loadActiveVisits(vm, pm, cm.getAllClients()); 
        loadContracts(pm, advisors, cm.getAllClients(), contracts);
        
        // Cargar las operaciones enlazando sus objetos correspondientes
        loadBusinessOperations(operations, pm, cm.getAllClients(), advisors);
        
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
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error asesores: " + e.getMessage());
        }
    }

    public static void saveBusinessOperations(SimpleLinkedList<BusinessOperation> operations) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BUSINESS_OPERATION_FILE))) {
            for (BusinessOperation op : operations) {
                String advId = (op.getAdvisor() != null) ? op.getAdvisor().getId() : "NONE";
                writer.println(String.format("%s;%s;%s;%s;%s;%s;%.2f;%.2f;%s",
                        op.getIdentifier(),
                        op.getRelatedProperty().getCode(),
                        op.getClient().getId(),
                        advId,
                        op.getDate().toString(),
                        op.getOperationType().name(),
                        op.getAgreedValue(),
                        op.getCommission(),
                        op.getProcessStatus().name()));
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error guardando operaciones de negocio: " + e.getMessage());
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
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error inmuebles: " + e.getMessage());
        }
    }

    private static void saveClients(SimpleLinkedList<Client> clients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENT_FILE))) {
            for (Client c : clients)
                writer.println(c.toFileLine());
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error clientes: " + e.getMessage());
        }
    }

    private static void saveClientHistories(SimpleLinkedList<Client> clients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENT_HISTORY_FILE))) {
            for (Client c : clients) {
                for (Property p : c.getVisitedPropertiesHistory()) {
                    writer.println(c.getId() + ";" + p.getCode());
                }
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error guardando historial de clientes: " + e.getMessage());
        }
    }

    private static void saveClientFavorites(SimpleLinkedList<Client> clients) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CLIENT_FAVORITES_FILE))) {
            for (Client c : clients) {
                for (Property p : c.getFavoriteProperties()) {
                    writer.println(c.getId() + ";" + p.getCode());
                }
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error guardando favoritos de clientes: " + e.getMessage());
        }
    }

    private static void saveVisitHistory(SimpleLinkedList<Visit> history) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VISIT_FILE))) {
            for (Visit v : history) {
                writer.println(String.format("%s;%s;%s;%s;%s;%s",
                        v.getClient().getId(), v.getProperty().getCode(), v.getDate(),
                        v.getTime(), v.getVisitStatus().name(),
                        v.getPostObservations() != null ? v.getPostObservations().replace(";", ",") : ""));
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error guardando historial: " + e.getMessage());
        }
    }

    private static void saveActiveVisits(VisitManager vm) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACTIVE_VISITS_FILE))) {
            for (Visit v : vm.getAllPendingAndActiveVisits()) {
                writer.println(String.format("%s;%s;%s;%s;%s",
                        v.getClient().getId(), v.getProperty().getCode(), v.getDate(),
                        v.getTime(), v.getVisitStatus().name()));
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error guardando visitas activas: " + e.getMessage());
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
            writer.flush(); // 🔥 Asegura el volcado inmediato al archivo plano
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
                if (line.trim().isEmpty())
                    continue;
                String[] d = line.split(";");
                advisors.add(new Advisor(d[0], d[1], d[2], d[3], d[4], Integer.parseInt(d[5])));
            }
        } catch (Exception e) {
            System.err.println("Error carga Asesores: " + e.getMessage());
        }
    }

    public static void loadBusinessOperations(SimpleLinkedList<BusinessOperation> dest, PropertyManager pm,
            SimpleLinkedList<Client> clients, SimpleLinkedList<Advisor> advisors) {
        File file = new File(BUSINESS_OPERATION_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty())
                    continue;
                String[] d = line.split(";");
                if (d.length < 9)
                    continue;

                Property p = pm.findByCode(d[1]);
                Client c = findClientById(clients, d[2]);
                Advisor adv = findAdvisorById(advisors, d[3]);

                if (p != null && c != null) {
                    BusinessOperation op = new BusinessOperation(
                            d[0], p, c, adv,
                            java.time.LocalDate.parse(d[4]),
                            OperationType.valueOf(d[5].toUpperCase().trim()),
                            Double.parseDouble(d[6].replace(",", ".")),
                            Double.parseDouble(d[7].replace(",", ".")),
                            proyectofinal.SistemaGestion.OperacionDeNegocio.ProcessStatus.valueOf(d[8].toUpperCase().trim()));
                    dest.add(op);
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando operaciones de negocio: " + e.getMessage());
        }
    }

    private static void loadClients(ClientManager cm) {
        File file = new File(CLIENT_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty())
                    continue;
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
                        ZoneProperty.valueOf(d[3].toUpperCase().trim()),
                        TypeProperty.valueOf(d[4].toUpperCase().trim()),
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

    private static void loadClientHistories(SimpleLinkedList<Client> clients, PropertyManager pm) {
        File file = new File(CLIENT_HISTORY_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty())
                    continue;
                String[] d = line.split(";");
                if (d.length < 2)
                    continue;

                Client c = findClientById(clients, d[0]);
                Property p = pm.findByCode(d[1]);

                if (c != null && p != null) {
                    c.getVisitedPropertiesHistory().add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Error carga historial de clientes: " + e.getMessage());
        }
    }

    private static void loadClientFavorites(SimpleLinkedList<Client> clients, PropertyManager pm) {
        File file = new File(CLIENT_FAVORITES_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty())
                    continue;
                String[] d = line.split(";");
                if (d.length < 2)
                    continue;

                Client c = findClientById(clients, d[0]);
                Property p = pm.findByCode(d[1]);

                if (c != null && p != null) {
                    c.getFavoriteProperties().add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Error carga favoritos de clientes: " + e.getMessage());
        }
    }

    private static void loadContracts(PropertyManager pm, SimpleLinkedList<Advisor> advisors,
            SimpleLinkedList<Client> clients, SimpleLinkedList<Contract> contractsDestination) {
        File file = new File(CONTRACT_FILE);
        if (!file.exists())
            return;

        // 💡 Limpieza de seguridad para evitar duplicaciones fantasmas en memoria RAM
        Contract.getContractRegistry().clearList();
        contractsDestination.clearList();

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty())
                    continue;
                String[] d = line.split(";");
                if (d.length < 9)
                    continue;

                Client c = findClientById(clients, d[3]);
                Property p = pm.findByCode(d[4]);
                Advisor adv = findAdvisorById(advisors, d[7]);

                if (c != null && p != null) {
                    // El constructor registra el contrato automáticamente en el contractRegistry
                    Contract ct = new Contract(d[0], d[1], d[2], c, p, LocalDate.parse(d[5]), LocalDate.parse(d[6]),
                            adv);
                    
                    // Sincronizamos el estado exacto recuperado del archivo de texto
                    ct.setStatus(ContractStatus.valueOf(d[8].toUpperCase().trim()));
                    
                    // Lo inyectamos a la lista del contexto para sincronizar las TableView
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
                String[] d = sc.nextLine().split(";");
                if (d.length < 5)
                    continue;
                Client c = findClientById(clients, d[0]);
                Property p = pm.findByCode(d[1]);
                if (c != null && p != null) {
                    Visit v = new Visit(c, p, LocalDate.parse(d[2]), LocalTime.parse(d[3]), p.getResponsibleAdvisor());
                    v.setVisitStatus(VisitStatus.valueOf(d[4].toUpperCase().trim()));
                    if (d.length > 5)
                        v.setPostObservations(d[5]);
                    vm.getVisitHistory().add(v);
                }
            }
        } catch (Exception e) {
            System.err.println("Error carga historial: " + e.getMessage());
        }
    }

    private static void loadActiveVisits(VisitManager vm, PropertyManager pm, SimpleLinkedList<Client> clients) {
        File file = new File(ACTIVE_VISITS_FILE);
        if (!file.exists())
            return;
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] d = sc.nextLine().split(";");
                if (d.length < 5)
                    continue;
                Client c = findClientById(clients, d[0]);
                Property p = pm.findByCode(d[1]);
                if (c != null && p != null) {
                    vm.scheduleVisit(c, p, LocalDate.parse(d[2]), LocalTime.parse(d[3]));
                }
            }
        } catch (Exception e) {
            System.err.println("Error carga activas: " + e.getMessage());
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private static Advisor findAdvisorById(SimpleLinkedList<Advisor> advisors, String id) {
        if (id == null || "NONE".equalsIgnoreCase(id.trim()))
            return null;
        for (Advisor a : advisors)
            if (a.getId().equals(id.trim()))
                return a;
        return null;
    }

    private static Client findClientById(SimpleLinkedList<Client> clients, String id) {
        if (id == null)
            return null;
        for (Client c : clients)
            if (c.getId().equals(id.trim()))
                return c;
        return null;
    }
}