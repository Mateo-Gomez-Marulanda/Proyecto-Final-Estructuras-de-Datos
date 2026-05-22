package proyectofinal.SistemaGestion.Alertas;

public enum AlertType {
    PROPERTY_NO_VISITS,       // Inmuebles sin visitas en mucho tiempo
    PROPERTY_HIGH_DEMAND,     // Propiedades con alta demanda
    VISIT_PENDING_CONFIRM,    // Visitas pendientes por confirmar
    RESERVATION_STALE,        // Inmuebles reservados por mucho tiempo sin cierre
    CLIENT_NO_FOLLOWUP,        // Clientes sin seguimiento reciente
    CONTRACT_EXPIRING
}