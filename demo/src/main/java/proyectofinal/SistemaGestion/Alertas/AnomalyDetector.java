package proyectofinal.SistemaGestion.Alertas;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;
import proyectofinal.Inmueble.Property;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;
import proyectofinal.Personal.Client;
import proyectofinal.Personal.ClientManager;

public class AnomalyDetector {

    /**
     * Analiza el estado del sistema en busca de comportamientos inusuales.
     * Utiliza el iterador nativo de SimpleLinkedList.
     */
    public static SimpleLinkedList<String> detectAnomalies(VisitManager vm, PropertyManager pm, ClientManager cm) {
        SimpleLinkedList<String> anomalies = new SimpleLinkedList<>();

        // 1. Detección: Cliente con >= 3 cancelaciones consecutivas
        for (Client c : cm.getAllClients()) {
            int cancelCount = 0;
            for (VisitRequest v : vm.getVisitHistory()) {
                if (v.getClient().getId().equals(c.getId()) && 
                    v.getStatus().equalsIgnoreCase("CANCELLED")) {
                    cancelCount++;
                }
            }
            if (cancelCount >= 3) {
                anomalies.add("SEGURIDAD: Cliente " + c.getName() + " (ID: " + c.getId() + ") ha cancelado " + cancelCount + " visitas.");
            }
        }

        // 2. Detección: Inmueble con cambios de precio (>=3) pero sin visitas registradas
        for (Property p : pm.getProperties()) {
            if (p.getPriceChangeCount() >= 3 && countVisitsForProperty(p.getCode(), vm) == 0) {
                anomalies.add("NEGOCIO: Inmueble " + p.getCode() + " ha bajado de precio 3 veces y no registra visitas.");
            }
        }

        return anomalies;
    }

    /**
     * Método auxiliar que utiliza el iterador de tu lista para contar visitas.
     */
    private static int countVisitsForProperty(String propertyCode, VisitManager vm) {
        int count = 0;
        // SimpleLinkedList<VisitRequest> history = vm.getVisitHistory();
        for (VisitRequest v : vm.getVisitHistory()) {
            if (v.getProperty().getCode().equals(propertyCode)) {
                count++;
            }
        }
        return count;
    }
}