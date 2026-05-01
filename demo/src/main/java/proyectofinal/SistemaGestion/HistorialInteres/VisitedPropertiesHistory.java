package proyectofinal.SistemaGestion.HistorialInteres;

import proyectofinal.SistemaGestion.AgendamientoVisitas.Visit;

public class VisitedPropertiesHistory extends History<Visit> {
    public VisitedPropertiesHistory() {
        super();
    }

    @Override
    public Visit findElementById(String code) {
        for (Visit visit : getHistoryList()) {
            if (visit.getCode().equals(code)) {
                return visit;
            }
        }
        return null;
    }
}