package proyectofinal.SistemaGestion.Reportes;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.ZoneProperty;
import proyectofinal.Personal.Advisor;
import proyectofinal.SistemaGestion.AgendamientoVisitas.Visit;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;
import proyectofinal.controllers.AppContext;
import proyectofinal.SistemaGestion.Grafos.GraphService;

public class ReportEngine {

    public static double calculateTotalSalesByZone(ZoneProperty zone) {
        double totalSales = 0.0;
        for (BusinessOperation op : AppContext.getInstance().getOperations()) {
            if (op.getOperationType() == OperationType.SALE &&
                    op.getProcessStatus().toString().equalsIgnoreCase("COMPLETED") &&
                    op.getRelatedProperty() != null && op.getRelatedProperty().getZone() == zone) {
                totalSales += op.getAgreedValue();
            }
        }
        return totalSales;
    }

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

    public static Property getMostVisitedProperty(SimpleLinkedList<Property> properties, VisitManager vm) {
        Property topProperty = null;
        int maxVisits = -1;
        for (Property p : properties) {
            int visits = 0;
            for (Visit v : vm.getVisitHistory()) {
                if (v.getProperty().getCode().equals(p.getCode()))
                    visits++;
            }
            if (visits > maxVisits) {
                maxVisits = visits;
                topProperty = p;
            }
        }
        return topProperty;
    }

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

    public static String generateFullSummaryReport(AppContext context) {
        StringBuilder sb = new StringBuilder();
        GraphService gs = context.getGraphService();

        sb.append("=========================================\n");
        sb.append("      REPORTE GERENCIAL AVANZADO         \n");
        sb.append("=========================================\n\n");

        sb.append("--- MÉTRICAS TRADICIONALES ---\n");
        sb.append("Ventas Totales por Zona:\n");
        for (ZoneProperty zone : ZoneProperty.values()) {
            sb.append(String.format(" > %s: $ %,.2f\n", zone.name(), calculateTotalSalesByZone(zone)));
        }

        Advisor topAd = getTopClosingAdvisor(context.getAdvisors());
        sb.append("\nMejor Asesor: ")
                .append(topAd != null ? topAd.getName() + " (" + topAd.getCompletedClosings() + " cierres)" : "N/A");

        Property hotProp = getMostVisitedProperty(context.getPropertyManager().getProperties(),
                context.getVisitManager());
        sb.append("\n--- INTELIGENCIA DE RED (ANALÍTICA) ---\n");
        sb.append("Cliente más activo (VIP): ").append(gs.getMostActiveClient()).append("\n");
        sb.append("Propiedades sin actividad (Frías): ").append(gs.getColdProperties()).append("\n");

        if (hotProp != null) {
            sb.append("Similitudes de la propiedad ").append(hotProp.getCode()).append(": ");

            // Obtenemos la lista
            SimpleLinkedList<String> similares = gs.getRelatedProperties(hotProp.getCode());

            if (similares.isEmpty()) {
                sb.append("Ninguna encontrada");
            } else {
                // Recorremos la lista para que se vea bien
                for (String id : similares) {
                    sb.append(id).append("  ");
                }
            }
            sb.append("\n");
        }
        sb.append("\nPrecio Promedio Disponibles: ")
                .append(String.format("$ %,.2f\n",
                        getAverageAvailablePropertyPrice(context.getPropertyManager().getProperties())));

        sb.append("=========================================\n");
        sb.append("Fecha de generación: ").append(java.time.LocalDate.now());

        return sb.toString();
    }
}