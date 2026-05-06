package proyectofinal.SistemaGestion.Contratos;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Advisor;
import proyectofinal.Personal.Client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Contract {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    // JavaFX observable properties
    private final SimpleStringProperty id;
    private final SimpleStringProperty name;
    private final SimpleStringProperty address;
    private final SimpleObjectProperty<Client>         owner;          // registered client
    private final SimpleObjectProperty<Property>       relatedProperty;
    private final SimpleObjectProperty<LocalDate>      creationDate;
    private final SimpleObjectProperty<LocalDate>      expirationDate;
    private final SimpleObjectProperty<Advisor>        approvingAdvisor;
    private final SimpleObjectProperty<ContractStatus> status;

    // Static contract registry (managed by a list
    // as required — future Observer hook point)
    private static final SimpleLinkedList<Contract> contractRegistry = new SimpleLinkedList<>();

    // Constructor
    /**
     * Creates a new contract and automatically registers it in the system list.
     * Status always starts as PENDING_APPROVAL until an advisor approves it.
     *
     * NOTE: Auto-registration here is the hook point for the future Observer pattern.
     * When implemented, the Observer notification will go right after contractRegistry.addLast(this).
     */
    public Contract(String id, String name, String address, Client owner,
                    Property relatedProperty, LocalDate creationDate,
                    LocalDate expirationDate, Advisor approvingAdvisor) {

        this.id               = new SimpleStringProperty(id);
        this.name             = new SimpleStringProperty(name);
        this.address          = new SimpleStringProperty(address);
        this.owner            = new SimpleObjectProperty<>(owner);
        this.relatedProperty  = new SimpleObjectProperty<>(relatedProperty);
        this.creationDate     = new SimpleObjectProperty<>(creationDate);
        this.expirationDate   = new SimpleObjectProperty<>(expirationDate);
        this.approvingAdvisor = new SimpleObjectProperty<>(approvingAdvisor);
        this.status           = new SimpleObjectProperty<>(ContractStatus.PENDING_APPROVAL);

        // Auto-register in the system list
        // FUTURE OBSERVER: notify listeners here
        contractRegistry.add(this);
    }

    // ─────────────────────────────────────────────
    // Business logic
    // ─────────────────────────────────────────────

    /**
     * Approves the contract. Only valid when PENDING_APPROVAL.
     */
    public void approve() {
        if (status.get() != ContractStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Only contracts pending approval can be approved.");
        }
        status.set(ContractStatus.ACTIVE);
    }

    /**
     * Cancels the contract. Not allowed if already expired or cancelled.
     */
    public void cancel() {
        if (status.get() == ContractStatus.EXPIRED || status.get() == ContractStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel a contract that is already " + status.get() + ".");
        }
        status.set(ContractStatus.CANCELLED);
    }

    /**
     * Checks if the contract has passed its expiration date and updates status if needed.
     * Call this on system load or periodically to keep statuses current.
     *
     * FUTURE OBSERVER: when this triggers EXPIRED, notify listeners to generate
     * an Alert of type CONTRACT_EXPIRING_SOON.
     */
    public void checkExpiration() {
        if (status.get() == ContractStatus.ACTIVE && LocalDate.now().isAfter(expirationDate.get())) {
            status.set(ContractStatus.EXPIRED);
            // FUTURE OBSERVER: notify here
        }
    }

    /**
     * Returns true if the contract expires within the given number of days.
     * Useful for the alert system (AlertType.CONTRACT_EXPIRING_SOON).
     */
    public boolean isExpiringSoon(int withinDays) {
        if (status.get() != ContractStatus.ACTIVE) return false;
        LocalDate threshold = LocalDate.now().plusDays(withinDays);
        return !expirationDate.get().isAfter(threshold);
    }

    public boolean isActive()         { return status.get() == ContractStatus.ACTIVE; }
    public boolean isPendingApproval(){ return status.get() == ContractStatus.PENDING_APPROVAL; }
    public boolean isExpired()        { return status.get() == ContractStatus.EXPIRED; }
    public boolean isCancelled()      { return status.get() == ContractStatus.CANCELLED; }

    // ─────────────────────────────────────────────
    // Static registry access
    // ─────────────────────────────────────────────

    public static SimpleLinkedList<Contract> getContractRegistry() {
        return contractRegistry;
    }

    public static Contract findById(String id) {
        for (Contract c : contractRegistry) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    // ─────────────────────────────────────────────
    // Property getters (for TableView binding)
    // ─────────────────────────────────────────────

    public SimpleStringProperty idProperty()                            { return id; }
    public SimpleStringProperty nameProperty()                          { return name; }
    public SimpleStringProperty addressProperty()                       { return address; }
    public SimpleObjectProperty<Client> ownerProperty()                 { return owner; }
    public SimpleObjectProperty<Property> relatedPropertyProperty()     { return relatedProperty; }
    public SimpleObjectProperty<LocalDate> creationDateProperty()       { return creationDate; }
    public SimpleObjectProperty<LocalDate> expirationDateProperty()     { return expirationDate; }
    public SimpleObjectProperty<Advisor> approvingAdvisorProperty()     { return approvingAdvisor; }
    public SimpleObjectProperty<ContractStatus> statusProperty()        { return status; }

    // ─────────────────────────────────────────────
    // Value getters
    // ─────────────────────────────────────────────

    public String getId()                  { return id.get(); }
    public String getName()                { return name.get(); }
    public String getAddress()             { return address.get(); }
    public Client getOwner()               { return owner.get(); }
    public Property getRelatedProperty()   { return relatedProperty.get(); }
    public LocalDate getCreationDate()     { return creationDate.get(); }
    public LocalDate getExpirationDate()   { return expirationDate.get(); }
    public Advisor getApprovingAdvisor()   { return approvingAdvisor.get(); }
    public ContractStatus getStatus()      { return status.get(); }

    // ─────────────────────────────────────────────
    // Value setters
    // ─────────────────────────────────────────────

    public void setId(String id)                         { this.id.set(id); }
    public void setName(String name)                     { this.name.set(name); }
    public void setAddress(String address)               { this.address.set(address); }
    public void setOwner(Client owner)                   { this.owner.set(owner); }
    public void setRelatedProperty(Property p)           { this.relatedProperty.set(p); }
    public void setCreationDate(LocalDate date)          { this.creationDate.set(date); }
    public void setExpirationDate(LocalDate date)        { this.expirationDate.set(date); }
    public void setApprovingAdvisor(Advisor advisor)     { this.approvingAdvisor.set(advisor); }

    // Formatted strings — convenient for TableView cells
    public String getCreationDateFormatted()   {
        return creationDate.get() != null ? creationDate.get().format(FORMATTER) : "—";
    }

    public String getExpirationDateFormatted() {
        return expirationDate.get() != null ? expirationDate.get().format(FORMATTER) : "—";
    }

    // ─────────────────────────────────────────────
    // toString
    // ─────────────────────────────────────────────

    @Override
    public String toString() {
        return "ID: " + getId()
                + " | Name: " + getName()
                + " | Address: " + getAddress()
                + " | Owner: " + (getOwner() != null ? getOwner().getName() : "—")
                + " | Property: " + (getRelatedProperty() != null ? getRelatedProperty().getCode() : "—")
                + " | Created: " + getCreationDateFormatted()
                + " | Expires: " + getExpirationDateFormatted()
                + " | Advisor: " + (getApprovingAdvisor() != null ? getApprovingAdvisor().getName() : "—")
                + " | Status: " + getStatus();
    }
}