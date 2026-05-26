package proyectofinal.SistemaGestion.Alertas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import proyectofinal.controllers.AppContext;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;
import proyectofinal.SistemaGestion.Contratos.Contract;
import proyectofinal.SistemaGestion.AgendamientoVisitas.Visit;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitStatus;

public class AlertEngine {

    private static final int DAYS_NO_VISITS = 30;         
    private static final int HIGH_DEMAND_THRESHOLD = 5;   
    private static final int DAYS_RESERVATION_STALE = 15; 
    private static final int DAYS_NO_FOLLOWUP = 20;       
    private static final int CONTRACT_EXPIRING_DAYS = 30;

    public static void checkAndGenerateAlerts(AppContext context) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // 1. Evaluación de Contratos (Sin cambios, se mantiene igual)
        for (Contract contract : Contract.getContractRegistry()) {
            contract.checkExpiration();
            if (contract.isExpiringSoon(CONTRACT_EXPIRING_DAYS)) {
                long daysLeft = ChronoUnit.DAYS.between(today, contract.getExpirationDate());
                enqueueUniqueAlert(context, new Alert(generateId(), AlertType.CONTRACT_EXPIRING, 
                    "El contrato '" + contract.getName() + "' vence en " + daysLeft + " días.", contract.getId(), "CONTRACT"));
            }
        }

        // 2. Evaluación de Propiedades
        for (Property p : context.getPropertyManager().getProperties()) {
            int visitCount = 0;
            LocalDateTime lastVisitDateTime = null;

            // Iteramos sobre la clase Visit (fusionada)
            for (Visit v : context.getVisitManager().getVisitHistory()) {
                if (v.getProperty().getCode().equals(p.getCode())) {
                    visitCount++;
                    // Combinamos fecha y hora para el cálculo
                    LocalDateTime vDT = LocalDateTime.of(v.getDate(), v.getTime());
                    if (lastVisitDateTime == null || vDT.isAfter(lastVisitDateTime)) {
                        lastVisitDateTime = vDT;
                    }
                }
            }

            if ("RESERVED".equalsIgnoreCase(p.getPropertyStatus()) && lastVisitDateTime != null) {
                long daysReserved = ChronoUnit.DAYS.between(lastVisitDateTime, now);
                if (daysReserved >= DAYS_RESERVATION_STALE) {
                    enqueueUniqueAlert(context, new Alert(generateId(), AlertType.RESERVATION_STALE,
                        "Inmueble reservado sin movimientos hace " + daysReserved + " días.", p.getCode(), "PROPERTY"));
                }
            }

            if (lastVisitDateTime != null) {
                long daysSinceLastVisit = ChronoUnit.DAYS.between(lastVisitDateTime.toLocalDate(), today);
                if (daysSinceLastVisit >= DAYS_NO_VISITS && "AVAILABLE".equalsIgnoreCase(p.getPropertyStatus())) {
                    enqueueUniqueAlert(context, new Alert(generateId(), AlertType.PROPERTY_NO_VISITS,
                        "Inmueble estancado: sin visitas hace " + daysSinceLastVisit + " días.", p.getCode(), "PROPERTY"));
                }
            }

            if (visitCount >= HIGH_DEMAND_THRESHOLD) {
                enqueueUniqueAlert(context, new Alert(generateId(), AlertType.PROPERTY_HIGH_DEMAND,
                    "Inmueble caliente: " + visitCount + " visitas registradas.", p.getCode(), "PROPERTY"));
            }
        }

        // 3. Evaluación de Visitas Pendientes (Usando Enum VisitStatus)
        for (Visit v : context.getVisitManager().getAllPendingAndActiveVisits()) {
            if (v.getVisitStatus() == VisitStatus.PENDING) {
                LocalDateTime visitDT = LocalDateTime.of(v.getDate(), v.getTime());
                if (visitDT.isBefore(now.plusDays(1))) {
                    enqueueUniqueAlert(context, new Alert(generateId(), AlertType.VISIT_PENDING_CONFIRM,
                        "Urgente: Visita el " + v.getDate() + " pendiente de confirmar.", 
                        v.getClient().getId() + "-" + v.getProperty().getCode(), "VISIT"));
                }
            }
        }

        // 4. Evaluación de Clientes Desatendidos
        for (Client c : context.getClientManager().getAllClients()) {
            LocalDateTime lastInteraction = null;
            for (Visit v : context.getVisitManager().getVisitHistory()) {
                if (v.getClient().getId().equals(c.getId())) {
                    LocalDateTime vDT = LocalDateTime.of(v.getDate(), v.getTime());
                    if (lastInteraction == null || vDT.isAfter(lastInteraction)) {
                        lastInteraction = vDT;
                    }
                }
            }
            if (lastInteraction != null && ChronoUnit.DAYS.between(lastInteraction, now) >= DAYS_NO_FOLLOWUP) {
                enqueueUniqueAlert(context, new Alert(generateId(), AlertType.CLIENT_NO_FOLLOWUP,
                    "Desatención: Cliente " + c.getName() + " sin seguimiento reciente.", c.getId(), "CLIENT"));
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