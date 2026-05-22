package proyectofinal.SistemaGestion.Alertas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import proyectofinal.controllers.AppContext;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;
import proyectofinal.SistemaGestion.Contratos.Contract;

public class AlertEngine {

    // Umbrales de tiempo y negocio configurables
    private static final int DAYS_NO_VISITS = 30;         
    private static final int HIGH_DEMAND_THRESHOLD = 5;   
    private static final int DAYS_RESERVATION_STALE = 15; 
    private static final int DAYS_NO_FOLLOWUP = 20;       
    private static final int CONTRACT_EXPIRING_DAYS = 30; // Contratos que venzan en menos de 30 días

    /**
     * Versión estándar para controladores de la UI.
     * Utiliza la instancia global ya construida de manera segura.
     */
    public static void checkAndGenerateAlerts() {
        checkAndGenerateAlerts(AppContext.getInstance());
    }

    /**
     * Versión interna de arranque para evitar bucles de recursión infinita (StackOverflowError).
     * Recibe el contexto directamente como parámetro durante la inicialización.
     */
    public static void checkAndGenerateAlerts(AppContext context) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // 1. Evaluación de Contratos Próximos a Vencer
        for (Contract contract : Contract.getContractRegistry()) {
            
            // Forzamos la actualización de estado por si ya expiró hoy
            contract.checkExpiration();

            // Evaluamos si el contrato está activo y próximo a vencer (dentro de 30 días)
            if (contract.isExpiringSoon(CONTRACT_EXPIRING_DAYS)) {
                long daysLeft = ChronoUnit.DAYS.between(today, contract.getExpirationDate());
                
                enqueueUniqueAlert(context, new Alert(
                    generateId(), 
                    AlertType.CONTRACT_EXPIRING, 
                    "El contrato '" + contract.getName() + "' vencerá en " + daysLeft + " días (Expira: " + contract.getExpirationDateFormatted() + ").",
                    contract.getId(), 
                    "CONTRACT"
                ));
            }
        }

        // 2. Evaluación de Propiedades (Visitas, Estancamiento, Alta Demanda)
        for (Property p : context.getPropertyManager().getProperties()) {
            
            int visitCount = 0;
            LocalDateTime lastVisitDate = null;

            for (VisitRequest v : context.getVisitManager().getVisitHistory()) {
                if (v.getProperty().getCode().equals(p.getCode())) {
                    visitCount++;
                    if (lastVisitDate == null || v.getDateTime().isAfter(lastVisitDate)) {
                        lastVisitDate = v.getDateTime();
                    }
                }
            }

            // Inmuebles reservados por mucho tiempo sin cierre
            if ("RESERVED".equalsIgnoreCase(p.getPropertyStatus()) && lastVisitDate != null) {
                long daysReserved = ChronoUnit.DAYS.between(lastVisitDate, now);
                if (daysReserved >= DAYS_RESERVATION_STALE) {
                    enqueueUniqueAlert(context, new Alert(
                        generateId(), AlertType.RESERVATION_STALE,
                        "El inmueble está RESERVADO y no registra movimientos ni cierres desde hace " + daysReserved + " días.",
                        p.getCode(), "PROPERTY"
                    ));
                }
            }

            // Inmuebles sin visitas en mucho tiempo
            if (lastVisitDate != null) {
                long daysSinceLastVisit = ChronoUnit.DAYS.between(lastVisitDate, now);
                if (daysSinceLastVisit >= DAYS_NO_VISITS && "AVAILABLE".equalsIgnoreCase(p.getPropertyStatus())) {
                    enqueueUniqueAlert(context, new Alert(
                        generateId(), AlertType.PROPERTY_NO_VISITS,
                        "Alerta de estancamiento: Inmueble disponible sin visitas desde hace " + daysSinceLastVisit + " días.",
                        p.getCode(), "PROPERTY"
                    ));
                }
            }

            // Propiedades con alta demanda
            if (visitCount >= HIGH_DEMAND_THRESHOLD) {
                enqueueUniqueAlert(context, new Alert(
                    generateId(), AlertType.PROPERTY_HIGH_DEMAND,
                    "Inmueble caliente: Registra un alto flujo de interés con " + visitCount + " visitas en total.",
                    p.getCode(), "PROPERTY"
                ));
            }
        }

        // 3. Evaluación de Visitas Pendientes por Confirmar
        for (VisitRequest v : context.getVisitManager().getVisitHistory()) {
            String status = v.getStatus().toUpperCase();
            if (status.equals("PENDING") || status.equals("PENDIENTE")) {
                if (v.getDateTime().isBefore(now.plusDays(1))) {
                    enqueueUniqueAlert(context, new Alert(
                        generateId(), AlertType.VISIT_PENDING_CONFIRM,
                        "Urgente: Visita programada para el " + v.getDateTime() + " sigue pendiente por confirmar.",
                        v.getClient().getId() + "-" + v.getProperty().getCode(), "VISIT"
                    ));
                }
            }
        }

        // 4. Evaluación de Clientes Desatendidos (Falta de Seguimiento)
        for (Client c : context.getClientManager().getAllClients()) {
            LocalDateTime lastInteraction = null;

            for (VisitRequest v : context.getVisitManager().getVisitHistory()) {
                if (v.getClient().getId().equals(c.getId())) {
                    if (lastInteraction == null || v.getDateTime().isAfter(lastInteraction)) {
                        lastInteraction = v.getDateTime();
                    }
                }
            }

            if (lastInteraction != null) {
                long daysWithoutFollowUp = ChronoUnit.DAYS.between(lastInteraction, now);
                if (daysWithoutFollowUp >= DAYS_NO_FOLLOWUP) {
                    enqueueUniqueAlert(context, new Alert(
                        generateId(), AlertType.CLIENT_NO_FOLLOWUP,
                        "Desatención: El cliente (" + c.getName() + ") no tiene interacciones ni agendamientos hace " + daysWithoutFollowUp + " días.",
                        c.getId(), "CLIENT"
                    ));
                }
            }
        }
    }

    /**
     * Encola una alerta asegurándose de evitar duplicados activos.
     * Utiliza explícitamente el contexto inyectado en lugar de llamar al Singleton.
     */
    private static void enqueueUniqueAlert(AppContext context, Alert newAlert) {
        boolean isDuplicate = false;

        proyectofinal.EstructurasDeDatos.Colas.Queue<Alert> tempQueue = new proyectofinal.EstructurasDeDatos.Colas.Queue<>();
        
        // Vaciamos temporalmente la cola para buscar repetidos
        while (!context.getPendingAlerts().isEmpty()) {
            Alert current = context.getPendingAlerts().dequeue();
            if (current.getRelatedEntityCode().equals(newAlert.getRelatedEntityCode()) &&
                current.getAlertType() == newAlert.getAlertType() && current.isPending()) {
                isDuplicate = true;
            }
            tempQueue.enqueue(current);
        }

        // Restauramos los elementos a la cola original en el orden correcto
        while (!tempQueue.isEmpty()) {
            context.getPendingAlerts().enqueue(tempQueue.dequeue());
        }

        // Si no existe un duplicado idéntico pendiente, agregamos la nueva alerta
        if (!isDuplicate) {
            context.getPendingAlerts().enqueue(newAlert);
        }
    }

    private static String generateId() {
        return "ALT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}