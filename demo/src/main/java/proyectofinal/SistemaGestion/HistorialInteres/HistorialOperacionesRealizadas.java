package proyectofinal.SistemaGestion.HistorialInteres;

import proyectofinal.SistemaGestion.OperacionDeNegocio.OperacionNegocio;

public class HistorialOperacionesRealizadas extends Historial<OperacionNegocio>{
    public HistorialOperacionesRealizadas() {
        super();
    }

    @Override
    public OperacionNegocio consultarElemento(String codigo) {
        for (OperacionNegocio operacion : this.getHistorial()) {
            if (operacion.getIdentificador().equals(codigo)) {
                return operacion;
            }
        }
        return null; 
    }    
}