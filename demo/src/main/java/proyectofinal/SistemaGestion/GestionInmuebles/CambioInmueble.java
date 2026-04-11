package proyectofinal.SistemaGestion.GestionInmuebles;

import java.time.LocalDateTime;

public class CambioInmueble {

    public enum TipoCambio {
        MODIFICACION_CAMPO,
        CAMBIO_ESTADO,
        ACCION_ADMINISTRATIVA
    }

    private String codigoInmueble;
    private TipoCambio tipoCambio;
    private String campoModificado;   // ej: "precio", "disponibilidad", "estadoInmueble"
    private Object valorAnterior;
    private Object valorNuevo;
    private String responsable;       // nombre del asesor que hizo el cambio
    private LocalDateTime fechaHora;
    private String descripcion;

    // Constructor para modificaciones de campo y cambios de estado
    public CambioInmueble(String codigoInmueble, TipoCambio tipoCambio,
                          String campoModificado, Object valorAnterior,
                          Object valorNuevo, String responsable) {
        this.codigoInmueble = codigoInmueble;
        this.tipoCambio = tipoCambio;
        this.campoModificado = campoModificado;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.responsable = responsable;
        this.fechaHora = LocalDateTime.now();
        this.descripcion = null;
    }

    // Constructor para acciones administrativas
    public CambioInmueble(String codigoInmueble, String descripcion, String responsable) {
        this.codigoInmueble = codigoInmueble;
        this.tipoCambio = TipoCambio.ACCION_ADMINISTRATIVA;
        this.campoModificado = null;
        this.valorAnterior = null;
        this.valorNuevo = null;
        this.responsable = responsable;
        this.fechaHora = LocalDateTime.now();
        this.descripcion = descripcion;
    }

    public String getCodigoInmueble() { return codigoInmueble; }
    public TipoCambio getTipoCambio() { return tipoCambio; }
    public String getCampoModificado() { return campoModificado; }
    public Object getValorAnterior() { return valorAnterior; }
    public Object getValorNuevo() { return valorNuevo; }
    public String getResponsable() { return responsable; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        if (tipoCambio == TipoCambio.ACCION_ADMINISTRATIVA) {
            return "[" + fechaHora + "] ACCIÓN ADMIN | Inmueble: " + codigoInmueble
                    + " | " + descripcion + " | Responsable: " + responsable;
        }
        return "[" + fechaHora + "] " + tipoCambio + " | Inmueble: " + codigoInmueble
                + " | Campo: " + campoModificado
                + " | Anterior: " + valorAnterior
                + " | Nuevo: " + valorNuevo
                + " | Responsable: " + responsable;
    }
}