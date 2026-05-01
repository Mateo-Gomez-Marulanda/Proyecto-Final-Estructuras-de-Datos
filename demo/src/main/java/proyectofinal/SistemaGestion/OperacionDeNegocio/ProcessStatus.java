package proyectofinal.SistemaGestion.OperacionDeNegocio;

public enum ProcessStatus {
    IN_PROGRESS,       // Operation started, documents being processed
    PENDING_SIGNATURE, // Awaiting client/advisor signatures
    COMPLETED,         // Operation successfully closed
    CANCELLED          // Operation cancelled
}