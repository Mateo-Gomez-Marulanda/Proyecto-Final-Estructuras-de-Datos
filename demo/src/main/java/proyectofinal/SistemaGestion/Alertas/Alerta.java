package proyectofinal.SistemaGestion.Alertas;

import java.time.LocalDateTime;

public class Alerta {
    private String id;
    private TipoAlerta tipoAlerta;
    private EstadoAlerta estadoAlerta;
    private String descripcion;

    // Referencia flexible al objeto que generó la alerta
    // (puede ser un código de inmueble, identificación de cliente, código de visita, etc.)
    private String codigoEntidadRelacionada;
    private String tipoEntidadRelacionada; // "INMUEBLE", "CLIENTE", "VISITA", "CONTRATO"

    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaGestion;
    private String observaciones;

    public Alerta(String id, TipoAlerta tipoAlerta, String descripcion,
                  String codigoEntidadRelacionada, String tipoEntidadRelacionada) {
        this.id = id;
        this.tipoAlerta = tipoAlerta;
        this.estadoAlerta = EstadoAlerta.PENDIENTE; // Al crearse siempre es PENDIENTE
        this.descripcion = descripcion;
        this.codigoEntidadRelacionada = codigoEntidadRelacionada;
        this.tipoEntidadRelacionada = tipoEntidadRelacionada;
        this.fechaGeneracion = LocalDateTime.now();
        this.fechaGestion = null;
        this.observaciones = null;
    }

    public void marcarRevisada(String observaciones) {
        if (this.estadoAlerta != EstadoAlerta.PENDIENTE) {
            throw new RuntimeException("Solo se pueden revisar alertas PENDIENTES.");
        }
        this.estadoAlerta = EstadoAlerta.REVISADA;
        this.fechaGestion = LocalDateTime.now();
        this.observaciones = observaciones;
    }

    public void marcarDescartada(String observaciones) {
        if (this.estadoAlerta != EstadoAlerta.PENDIENTE) {
            throw new RuntimeException("Solo se pueden descartar alertas PENDIENTES.");
        }
        this.estadoAlerta = EstadoAlerta.DESCARTADA;
        this.fechaGestion = LocalDateTime.now();
        this.observaciones = observaciones;
    }

    public boolean estaPendiente() {
        return this.estadoAlerta == EstadoAlerta.PENDIENTE;
    }

    public String getId() { return id; }

    public TipoAlerta getTipoAlerta() { return tipoAlerta; }

    public EstadoAlerta getEstadoAlerta() { return estadoAlerta; }

    public String getDescripcion() { return descripcion; }

    public String getCodigoEntidadRelacionada() { return codigoEntidadRelacionada; }

    public String getTipoEntidadRelacionada() { return tipoEntidadRelacionada; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }

    public LocalDateTime getFechaGestion() { return fechaGestion; }

    public String getObservaciones() { return observaciones; }

    @Override
    public String toString() {
        String base = "[" + id + "] " + tipoAlerta
                + " | Estado: " + estadoAlerta
                + " | Entidad: " + tipoEntidadRelacionada + " " + codigoEntidadRelacionada
                + " | Generada: " + fechaGeneracion
                + " | " + descripcion;

        if (fechaGestion != null) {
            base += " | Gestionada: " + fechaGestion;
        }
        if (observaciones != null) {
            base += " | Observaciones: " + observaciones;
        }
        return base;
    }
}