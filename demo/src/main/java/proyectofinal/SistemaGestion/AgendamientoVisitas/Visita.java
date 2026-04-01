package proyectofinal.SistemaGestion.AgendamientoVisitas;

import java.time.LocalDate;
import java.time.LocalTime;

import proyectofinal.Inmueble.Inmueble;
import proyectofinal.Personal.Asesor;
import proyectofinal.Personal.Cliente;

public class Visita {
    private Cliente cliente; // Cliente
    private Inmueble inmueble; // Inmueble
    private LocalDate fecha;
    private LocalTime hora;
    private Asesor asesorAsignado; // Asesor
    private EstadoVisita estadoVisita; // pendiente, confirmada, realizada, cancelada, reprogramada
    private String observacionesPosteriores;

    public Visita(Cliente cliente, Inmueble inmueble, LocalDate fecha, LocalTime hora, Asesor asesorAsignado,
            EstadoVisita estadoVisita, String observacionesPosteriores) {
        this.cliente = cliente;
        this.inmueble = inmueble;
        this.fecha = fecha;
        this.hora = hora;
        this.asesorAsignado = asesorAsignado;
        this.estadoVisita = estadoVisita;
        this.observacionesPosteriores = observacionesPosteriores;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Inmueble getInmueble() {
        return inmueble;
    }

    public void setInmueble(Inmueble inmueble) {
        this.inmueble = inmueble;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Asesor getAsesorAsignado() {
        return asesorAsignado;
    }

    public void setAsesorAsignado(Asesor asesorAsignado) {
        this.asesorAsignado = asesorAsignado;
    }

    public EstadoVisita getEstadoVisita() {
        return estadoVisita;
    }

    public void setEstadoVisita(EstadoVisita estadoVisita) {
        this.estadoVisita = estadoVisita;
    }

    public String getObservacionesPosteriores() {
        return observacionesPosteriores;
    }

    public void setObservacionesPosteriores(String observacionesPosteriores) {
        this.observacionesPosteriores = observacionesPosteriores;
    }

    @Override
    public String toString() {
        return "Cliente: " + cliente + " | Inmueble: " + inmueble + " | Fecha: " + fecha + " | Hora: " + hora
                + " | Asesor Asignado: " + asesorAsignado + " | Estado: " + estadoVisita + " | Observaciones: "
                + observacionesPosteriores;
    }
}