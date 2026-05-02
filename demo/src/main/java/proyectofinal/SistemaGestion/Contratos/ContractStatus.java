package proyectofinal.SistemaGestion.Contratos;

public enum ContractStatus {
    PENDING_APPROVAL,  // Created but not yet approved by advisor
    ACTIVE,            // Approved and currently valid
    EXPIRED,           // Past expiration date
    CANCELLED          // Terminated before expiration
}