package proyectofinal.SistemaGestion.HistorialInteres;

import proyectofinal.Inmueble.Inmueble;

public class HistorialInmueblesConsultados extends Historial<Inmueble> {
    public HistorialInmueblesConsultados() {
        super();
    }

    @Override
    public Inmueble consultarElemento(String codigo) {
        for (Inmueble inmueble : getHistorial()) {
            if (inmueble.getCodigo().equals(codigo)) {
                return inmueble;
            }
        }
        return null; 
    }
}