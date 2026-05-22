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
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;
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
    private final ObservableList<BusinessOperation> operations;
    private final Queue<Alert>                      pendingAlerts;
    private final SimpleLinkedList<Alert>           alertHistory;

    private AppContext() {
        propertyManager = new PropertyManager();
        clientManager   = new ClientManager();
        visitManager    = new VisitManager();
        advisors        = new SimpleLinkedList<>();
        operations      = FXCollections.observableArrayList();
        pendingAlerts   = new Queue<>();
        alertHistory    = new SimpleLinkedList<>();

        OperationPublisher.getInstance().subscribe(new ContractGeneratorObserver());
        loadPersistedData();
        crearAdminSiNoExiste();
    }

    public static AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    private void loadPersistedData() {
        // ENTRADA SÍNCRONA: Pasamos 'clientManager' directamente. 
        // Desaparece el bucle 'for' porque la indexación en la HashTable ocurre abajo.
        PersistenceManager.loadAll(propertyManager, clientManager, advisors, visitManager);
        AlertEngine.checkAndGenerateAlerts(this);
    }

    private void crearAdminSiNoExiste() {
        if (clientManager.getClientTable().get("admin") != null) return;
        clientManager.registerFull("admin", "Administrador", "admin@proptech.com",
                "000-000-0000", "ADMIN", 0.0,"N/A", "N/A", null, 0, "ACTIVE", "Admin1234");
    }

    public void saveAll() {
        // Guardamos pasándole el manager para extraer la lista limpiamente
        PersistenceManager.saveAll(propertyManager, clientManager, advisors, visitManager);
    }

    public PropertyManager getPropertyManager()              { return propertyManager; }
    public ClientManager   getClientManager()                { return clientManager; }
    public VisitManager    getVisitManager()                 { return visitManager; }
    public SimpleLinkedList<Advisor> getAdvisors()           { return advisors; }
    public ObservableList<BusinessOperation> getOperations() { return operations; }
    public Queue<Alert>    getPendingAlerts()                { return pendingAlerts; }
    public SimpleLinkedList<Alert> getAlertHistory()         { return alertHistory; }
}