package proyectofinal.SistemaGestion.OperacionDeNegocio;

import proyectofinal.Inmueble.Inmueble;
import proyectofinal.Personal.Asesor;
import proyectofinal.Personal.Cliente;
import java.time.LocalDate;

public class OperacionNegocio {
    private String identificador;
    private Inmueble inmuebleRelacionado;
    private Cliente cliente;
    private Asesor asesor;
    private LocalDate fecha;
    private TipoOperacion tipoOperacion;
    private double valorAcordado;
    private double comision;
    private String estadoProceso;

    public OperacionNegocio(String identificador, Inmueble inmuebleRelacionado, Cliente cliente, Asesor asesor,
            LocalDate fecha, TipoOperacion tipoOperacion, double valorAcordado, double comision, String estadoProceso) {
        this.identificador = identificador;
        this.inmuebleRelacionado = inmuebleRelacionado;
        this.cliente = cliente;
        this.asesor = asesor;
        this.fecha = fecha;
        this.tipoOperacion = tipoOperacion;
        this.valorAcordado = valorAcordado;
        this.comision = comision;
        this.estadoProceso = estadoProceso;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Inmueble getInmuebleRelacionado() {
        return inmuebleRelacionado;
    }

    public void setInmuebleRelacionado(Inmueble inmuebleRelacionado) {
        this.inmuebleRelacionado = inmuebleRelacionado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Asesor getAsesor() {
        return asesor;
    }

    public void setAsesor(Asesor asesor) {
        this.asesor = asesor;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public TipoOperacion getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(TipoOperacion tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public double getValorAcordado() {
        return valorAcordado;
    }

    public void setValorAcordado(double valorAcordado) {
        this.valorAcordado = valorAcordado;
    }

    public double getComision() {
        return comision;
    }

    public void setComision(double comision) {
        this.comision = comision;
    }

    public String getEstadoProceso() {
        return estadoProceso;
    }

    public void setEstadoProceso(String estadoProceso) {
        this.estadoProceso = estadoProceso;
    }

    @Override
    public String toString() {
        return "Identificador: " + identificador + " | Inmueble: " + inmuebleRelacionado + " | Cliente: " + cliente
                + " | Asesor: " + asesor + " | Fecha: " + fecha + " | Tipo de Operación: " + tipoOperacion
                + " | Valor Acordado: $" + valorAcordado + " | Comisión: $" + comision + " | Estado del Proceso: "
                + estadoProceso;
    }
}