package proyectofinal.Inmueble;

import proyectofinal.Personal.Asesor;

public class Inmueble {
    private String codigo;
    private String direccion;
    private String ciudad;
    private String barrioZona;
    private TipoInmueble tipoInmueble;
    private String finalidad; // venta o arriendo
    private double precio;
    private double area;
    private int numeroHabitaciones;
    private int numeroBanios;
    private String estadoInmueble;
    private boolean disponibilidad;
    private Asesor asesorResponsable;

    public Inmueble(String codigo, String direccion, String ciudad, String barrioZona, TipoInmueble tipoInmueble,
            String finalidad, double precio, double area, int numeroHabitaciones, int numeroBanios,
            String estadoInmueble, boolean disponibilidad, Asesor asesorResponsable) {
        this.codigo = codigo;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.barrioZona = barrioZona;
        this.tipoInmueble = tipoInmueble;
        this.finalidad = finalidad;
        this.precio = precio;
        this.area = area;
        this.numeroHabitaciones = numeroHabitaciones;
        this.numeroBanios = numeroBanios;
        this.estadoInmueble = estadoInmueble;
        this.disponibilidad = disponibilidad;
        this.asesorResponsable = asesorResponsable;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getBarrioZona() {
        return barrioZona;
    }

    public void setBarrioZona(String barrioZona) {
        this.barrioZona = barrioZona;
    }

    public TipoInmueble getTipoInmueble() {
        return tipoInmueble;
    }

    public void setTipoInmueble(TipoInmueble tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }

    public String getFinalidad() {
        return finalidad;
    }

    public void setFinalidad(String finalidad) {
        this.finalidad = finalidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getNumeroHabitaciones() {
        return numeroHabitaciones;
    }

    public void setNumeroHabitaciones(int numeroHabitaciones) {
        this.numeroHabitaciones = numeroHabitaciones;
    }

    public int getNumeroBanios() {
        return numeroBanios;
    }

    public void setNumeroBanios(int numeroBanios) {
        this.numeroBanios = numeroBanios;
    }

    public String getEstadoInmueble() {
        return estadoInmueble;
    }

    public void setEstadoInmueble(String estadoInmueble) {
        this.estadoInmueble = estadoInmueble;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }

    public Asesor getAsesorResponsable() {
        return asesorResponsable;
    }

    public void setAsesorResponsable(Asesor asesorResponsable) {
        this.asesorResponsable = asesorResponsable;
    }

    @Override
    public String toString() {
        return "Código: " + codigo + " | Dirección: " + direccion + " | Ciudad: " + ciudad + " | Barrio/Zona: "
                + barrioZona + " | Tipo de Inmueble: " + tipoInmueble + " | Finalidad: " + finalidad + " | Precio: $"
                + precio + " | Área: " + area + " m² | Habitaciones: " + numeroHabitaciones + " | Baños: "
                + numeroBanios + " | Estado: " + estadoInmueble + " | Disponible: " + (disponibilidad ? "Sí" : "No")
                + " | Asesor Responsable: " + asesorResponsable;
    }
}
