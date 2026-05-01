package proyectofinal.SistemaGestion.HistorialInteres;

import proyectofinal.Inmueble.Property;

public class PropertyConsultationHistory extends History<Property> {
    public PropertyConsultationHistory() {
        super();
    }

    @Override
    public Property findElementById(String code) {
        for (Property property : getHistoryList()) {
            if (property.getCode().equals(code)) {
                return property;
            }
        }
        return null; // No encontrado
    }
}