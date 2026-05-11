package proyectofinal.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import proyectofinal.EstructurasDeDatos.Colas.Queue;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.ClientManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.Alertas.Alert;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;
import proyectofinal.SistemaGestion.Observer.ContractGeneratorObserver;
import proyectofinal.SistemaGestion.Observer.OperationPublisher;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.Persistencia.PersistenceManager;

/**
 * Central application state — Singleton.
 * Every controller gets its data from here instead of creating its own instances.
 */
public class AppContext {

    private static AppContext instance;

    // ─── Managers ────────────────────────────────────────────
    private final PropertyManager propertyManager;
    private final ClientManager   clientManager;
    private final VisitManager    visitManager;

    // ─── Collections without a dedicated manager ─────────────
    private final SimpleLinkedList<Advisor> advisors;

    // ObservableList for operations — JavaFX tables bind directly to this.
    // OperacionesController registers itself as an Observer so this list
    // updates automatically whenever a BusinessOperation is published.
    private final ObservableList<BusinessOperation> operations;

    // Alert queues
    private final Queue<Alert>               pendingAlerts;
    private final SimpleLinkedList<Alert>    alertHistory;

    // ─────────────────────────────────────────────────────────
    private AppContext() {
        propertyManager = new PropertyManager();
        clientManager   = new ClientManager();
        visitManager    = new VisitManager();
        advisors        = new SimpleLinkedList<>();
        operations      = FXCollections.observableArrayList();
        pendingAlerts   = new Queue<>();
        alertHistory    = new SimpleLinkedList<>();

        // Register the contract generator — reacts to every new operation
        OperationPublisher.getInstance().subscribe(new ContractGeneratorObserver());

        // Load persisted data
        loadPersistedData();
    }

    public static AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // ─────────────────────────────────────────────────────────
    // Data loading
    // ─────────────────────────────────────────────────────────

    /**
     * Loads all data from disk.
     * NOTE: PersistenceManager loads clients directly into the list but skips
     * the internal HashTable of ClientManager. We re-index here to fix that.
     */
    private void loadPersistedData() {
        PersistenceManager.loadAll(
                propertyManager,
                clientManager.getAllClients(),
                advisors,
                visitManager
        );

        // Re-index all loaded clients into the HashTable so lookups work
        for (var client : clientManager.getAllClients()) {
            try {
                clientManager.getClientTable().put(client.getId(), client);
            } catch (Exception ignored) {
                // Already indexed — skip
            }
        }
    }

    public void saveAll() {
        PersistenceManager.saveAll(
                propertyManager,
                clientManager.getAllClients(),
                advisors,
                visitManager
        );
    }

    // ─────────────────────────────────────────────────────────
    // Getters
    // ─────────────────────────────────────────────────────────

    public PropertyManager getPropertyManager()           { return propertyManager; }
    public ClientManager   getClientManager()             { return clientManager; }
    public VisitManager    getVisitManager()              { return visitManager; }
    public SimpleLinkedList<Advisor> getAdvisors()        { return advisors; }
    public ObservableList<BusinessOperation> getOperations() { return operations; }
    public Queue<Alert>              getPendingAlerts()   { return pendingAlerts; }
    public SimpleLinkedList<Alert>   getAlertHistory()    { return alertHistory; }
}
