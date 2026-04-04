package proyectofinal.Personal;

import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Inmueble;
import proyectofinal.Inmueble.TipoInmueble;

public class Cliente {
    private String identificacion;
    private String nombre;
    private String correo;
    private String telefono;
    private String tipoCliente;
    private double presupuesto;
    private Object zonasInteres; // estructura de datos a definir
    private TipoInmueble tipoInmuebleDeseado;
    private int cantidadMinimaHabitaciones;
    private String estadoBusqueda;

    private SimpleLinkedList<Inmueble> inmueblesFavoritos = new SimpleLinkedList<>();

    public Cliente(String identificacion, String nombre, String correo, String telefono, String tipoCliente,
            double presupuesto, Object zonasInteres, TipoInmueble tipoInmuebleDeseado, int cantidadMinimaHabitaciones,
            String estadoBusqueda) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.tipoCliente = tipoCliente;
        this.presupuesto = presupuesto;
        this.zonasInteres = zonasInteres;
        this.tipoInmuebleDeseado = tipoInmuebleDeseado;
        this.cantidadMinimaHabitaciones = cantidadMinimaHabitaciones;
        this.estadoBusqueda = estadoBusqueda;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Object getZonasInteres() {
        return zonasInteres;
    }

    public void setZonasInteres(Object zonasInteres) {
        this.zonasInteres = zonasInteres;
    }

    public TipoInmueble getTipoInmuebleDeseado() {
        return tipoInmuebleDeseado;
    }

    public void setTipoInmuebleDeseado(TipoInmueble tipoInmuebleDeseado) {
        this.tipoInmuebleDeseado = tipoInmuebleDeseado;
    }

    public int getCantidadMinimaHabitaciones() {
        return cantidadMinimaHabitaciones;
    }

    public void setCantidadMinimaHabitaciones(int cantidadMinimaHabitaciones) {
        this.cantidadMinimaHabitaciones = cantidadMinimaHabitaciones;
    }

    public String getEstadoBusqueda() {
        return estadoBusqueda;
    }

    public void setEstadoBusqueda(String estadoBusqueda) {
        this.estadoBusqueda = estadoBusqueda;
    }

    @Override
    public String toString() {
        return "Identificación: " + identificacion + " | Nombre: " + nombre + " | Correo: " + correo + " | Teléfono: "
                + telefono + " | Tipo de Cliente: " + tipoCliente + " | Presupuesto: $" + presupuesto
                + " | Zonas de Interés: " + zonasInteres + " | Tipo de Inmueble Deseado: " + tipoInmuebleDeseado
                + " | Mínimo de Habitaciones: " + cantidadMinimaHabitaciones + " | Estado de Búsqueda: "
                + estadoBusqueda;
    }

    public void consultarInmuebles(Inmueble inmueble) {
        // Lógica para consultar inmuebles disponibles según los criterios del cliente

    }

    public void agendarVisita() {
        // Lógica para agendar una visita a un inmueble específico
    }

    public void marcarFavorito(Inmueble inmueble) {
        inmueblesFavoritos.addLast(inmueble);
    }

    public void registrarIntencionCompraArriendo() {
        // Lógica para registrar la intención de compra o arriendo de un inmueble
    }

    public void consultarHistorialInteracciones() {
        // Lógica para consultar el historial de interacciones del cliente con la
        // inmobiliaria
    }
}