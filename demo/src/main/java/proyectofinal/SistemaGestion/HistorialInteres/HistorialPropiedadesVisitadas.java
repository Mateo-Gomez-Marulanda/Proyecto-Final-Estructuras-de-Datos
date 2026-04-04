package proyectofinal.SistemaGestion.HistorialInteres;

import proyectofinal.SistemaGestion.AgendamientoVisitas.Visita;

public class HistorialPropiedadesVisitadas extends Historial<Visita> {
    public HistorialPropiedadesVisitadas() {
        super();
    }

    @Override
    public Visita consultarElemento(String codigo) {
        for (Visita visita : getHistorial()) {
            if (visita.getCodigo().equals(codigo)) {
                return visita;
            }
        }
        return null;
    }
}