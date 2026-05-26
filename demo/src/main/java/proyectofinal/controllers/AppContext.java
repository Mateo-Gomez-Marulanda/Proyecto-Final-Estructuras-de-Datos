package proyectofinal.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import proyectofinal.EstructurasDeDatos.Colas.Queue;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.ClientManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.Alertas.Alert;
import proyectofinal.SistemaGestion.Alertas.AlertEngine;
import proyectofinal.SistemaGestion.Contratos.Contract;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;
import proyectofinal.SistemaGestion.Grafos.GraphService;
import proyectofinal.SistemaGestion.Observer.ContractGeneratorObserver;
import proyectofinal.SistemaGestion.Observer.OperationPublisher;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.Persistencia.PersistenceManager;

public class AppContext {

    private static AppContext instance;

    private final PropertyManager propertyManager;
    private final ClientManager   clientManager;
    private final VisitManager    visitManager;
    private final SimpleLinkedList<Advisor>         advisors;
    private final SimpleLinkedList<Contract>        contracts; 
    private final ObservableList<BusinessOperation> operations;
    private final Queue<Alert>                      pendingAlerts;
    private final SimpleLinkedList<Alert>           alertHistory;

    private final GraphService graphService;

    private AppContext() {
        // 1. Inicialización de Managers
        propertyManager = new PropertyManager();
        clientManager   = new ClientManager();
        visitManager    = new VisitManager();
        advisors        = new SimpleLinkedList<>();
        contracts       = new SimpleLinkedList<>(); 
        operations      = FXCollections.observableArrayList();
        pendingAlerts   = new Queue<>();
        alertHistory    = new SimpleLinkedList<>();
        
        // 2. Inicializar el servicio de grafos (sin sincronizar aún)
        graphService = GraphService.getInstance();

        // 3. Cargar datos desde persistencia
        loadPersistedData();
        crearAdminSiNoExiste();

        // 4. Sincronizar el grafo DESPUÉS de tener los datos cargados
        graphService.synchronizeData(visitManager.getVisitHistory(), propertyManager.getProperties());

        OperationPublisher.getInstance().subscribe(new ContractGeneratorObserver());
    }

    public static AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    private void loadPersistedData() {
        SimpleLinkedList<BusinessOperation> tempOperationsList = new SimpleLinkedList<>();
        PersistenceManager.loadAll(propertyManager, clientManager, advisors, visitManager, contracts, tempOperationsList);
        
        operations.clear();
        for (BusinessOperation op : tempOperationsList) {
            operations.add(op);
        }
        
        AlertEngine.checkAndGenerateAlerts(this);
    }

    public void saveAll() {
        SimpleLinkedList<BusinessOperation> tempOperationsList = new SimpleLinkedList<>();
        for (BusinessOperation op : operations) {
            tempOperationsList.add(op);
        }

        PersistenceManager.saveAll(propertyManager, clientManager, advisors, visitManager, contracts, tempOperationsList);
    }

    // Getters
    public SimpleLinkedList<Contract> getContracts() { return contracts; }
    public PropertyManager getPropertyManager()      { return propertyManager; }
    public ClientManager   getClientManager()        { return clientManager; }
    public VisitManager    getVisitManager()         { return visitManager; }
    public SimpleLinkedList<Advisor> getAdvisors()   { return advisors; }
    public ObservableList<BusinessOperation> getOperations() { return operations; }
    public Queue<Alert>    getPendingAlerts()        { return pendingAlerts; }
    public SimpleLinkedList<Alert> getAlertHistory() { return alertHistory; }
    public GraphService getGraphService()            { return graphService; }

    private void crearAdminSiNoExiste() {
        if (clientManager.getClientTable().get("admin") != null) return;
        clientManager.registerFull("admin", "Administrador", "admin@proptech.com",
                "000-000-0000", "ADMIN", 0.0,"N/A", "N/A", null, 0, "ACTIVE", "Admin1234");
    }
}