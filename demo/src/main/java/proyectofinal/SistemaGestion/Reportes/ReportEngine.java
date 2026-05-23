package proyectofinal.SistemaGestion.Reportes;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.ZoneProperty;
import proyectofinal.Personal.Advisor;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;
import proyectofinal.controllers.AppContext;

public class ReportEngine {

    /**
     * Calcula el monto total de ventas (cierres) en una zona específica.
     */
    public static double calculateTotalSalesByZone(ZoneProperty zone) {
        double totalSales = 0.0;
        
        for (BusinessOperation op : AppContext.getInstance().getOperations()) {
            // Verificamos que sea una venta, que esté completada y que pertenezca a la zona indicada
            if (op.getOperationType() == OperationType.SALE &&
                op.getProcessStatus().toString().equalsIgnoreCase("COMPLETED") &&
                op.getRelatedProperty() != null &&
                op.getRelatedProperty().getZone() == zone) {
                
                totalSales += op.getAgreedValue();
            }
        }
        return totalSales;
    }

    /**
     * Determina cuál es el asesor con mayor número de cierres completados.
     */
    public static Advisor getTopClosingAdvisor(SimpleLinkedList<Advisor> advisors) {
        Advisor topAdvisor = null;
        int maxClosings = -1;

        for (Advisor adv : advisors) {
            if (adv.getCompletedClosings() > maxClosings) {
                maxClosings = adv.getCompletedClosings();
                topAdvisor = adv;
            }
        }
        return topAdvisor;
    }

    /**
     * Identifica el inmueble que ha recibido la mayor cantidad de visitas en el historial.
     */
    public static Property getMostVisitedProperty(SimpleLinkedList<Property> properties, VisitManager vm) {
        Property topProperty = null;
        int maxVisits = -1;

        for (Property p : properties) {
            int visits = 0;
            for (VisitRequest v : vm.getVisitHistory()) {
                if (v.getProperty().getCode().equals(p.getCode())) {
                    visits++;
                }
            }
            if (visits > maxVisits) {
                maxVisits = visits;
                topProperty = p;
            }
        }
        return topProperty;
    }

    /**
     * Calcula el precio promedio de todos los inmuebles disponibles (que no están vendidos).
     */
    public static double getAverageAvailablePropertyPrice(SimpleLinkedList<Property> properties) {
        double sum = 0.0;
        int count = 0;

        for (Property p : properties) {
            if (p.isAvailable()) {
                sum += p.getPrice();
                count++;
            }
        }
        return count == 0 ? 0.0 : (sum / count);
    }

    /**
     * Genera un reporte completo en formato texto (Ideal para imprimir en consola 
     * o inyectar directamente en un TextArea de JavaFX).
     */
    public static String generateFullSummaryReport(AppContext context) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========================================\n");
        sb.append("      REPORTE GERENCIAL DEL SISTEMA      \n");
        sb.append("=========================================\n\n");

        // 1. Rendimiento por Zona (Norte, Sur, etc. - iterando el Enum)
        sb.append("--- VENTAS POR ZONA ---\n");
        for (ZoneProperty zone : ZoneProperty.values()) {
            double sales = calculateTotalSalesByZone(zone);
            sb.append(String.format("Zona %s: $ %,.2f\n", zone.name(), sales));
        }
        sb.append("\n");

        // 2. Mejor Asesor
        Advisor topAd = getTopClosingAdvisor(context.getAdvisors());
        sb.append("--- RENDIMIENTO DE PERSONAL ---\n");
        if (topAd != null) {
            sb.append("Mejor Asesor: ").append(topAd.getName())
              .append(" (").append(topAd.getCompletedClosings()).append(" cierres)\n");
        } else {
            sb.append("Mejor Asesor: N/A\n");
        }
        sb.append("\n");

        // 3. Propiedad Caliente
        Property hotProp = getMostVisitedProperty(context.getPropertyManager().getProperties(), context.getVisitManager());
        sb.append("--- DEMANDA DE INMUEBLES ---\n");
        if (hotProp != null) {
            sb.append("Inmueble más visitado: ").append(hotProp.getCode())
              .append(" - ").append(hotProp.getAddress()).append("\n");
        } else {
            sb.append("Inmueble más visitado: N/A\n");
        }
        
        // 4. Promedio de precios
        double avgPrice = getAverageAvailablePropertyPrice(context.getPropertyManager().getProperties());
        sb.append(String.format("Precio Promedio Disp: $ %,.2f\n", avgPrice));

        sb.append("=========================================\n");
        
        return sb.toString();
    }
}