package proyectofinal.SistemaGestion.GestionInmuebles;

import proyectofinal.EstructurasDeDatos.Pilas.Stack;
import proyectofinal.EstructurasDeDatos.Listas.SimpleLinkedList;
import proyectofinal.Inmueble.Inmueble;
import proyectofinal.SistemaGestion.GestionInmuebles.CambioInmueble.TipoCambio;

public class InmuebleManager {

    // Catálogo principal de inmuebles
    private SimpleLinkedList<Inmueble> inmuebles;

    // Pila 1: deshacer cambios en publicaciones (precio, área, habitaciones, etc.)
    private Stack<CambioInmueble> historialModificaciones;

    // Pila 2: revertir cambios de estado (estadoInmueble, disponibilidad)
    private Stack<CambioInmueble> historialEstados;

    // Pila 3: historial de acciones administrativas
    private Stack<CambioInmueble> historialAccionesAdmin;

    public InmuebleManager() {
        this.inmuebles = new SimpleLinkedList<>();
        this.historialModificaciones = new Stack<>();
        this.historialEstados = new Stack<>();
        this.historialAccionesAdmin = new Stack<>();
    }

    // CRUD básico
    public void registrarInmueble(Inmueble inmueble, String responsable) {
        inmuebles.addLast(inmueble);
        registrarAccionAdmin(inmueble.getCodigo(),
                "Inmueble registrado en el sistema", responsable);
    }

    public Inmueble buscarPorCodigo(String codigo) {
        for (Inmueble i : inmuebles) {
            if (i.getCodigo().equals(codigo)) return i;
        }
        return null;
    }

    // Pila 1: modificaciones de publicación (precio, área, habitaciones, etc.)
    public void modificarPrecio(String codigo, double nuevoPrecio, String responsable) {
        Inmueble inmueble = buscarPorCodigo(codigo);
        if (inmueble == null) throw new RuntimeException("Inmueble no encontrado: " + codigo);

        CambioInmueble cambio = new CambioInmueble(
                codigo, TipoCambio.MODIFICACION_CAMPO,
                "precio", inmueble.getPrecio(), nuevoPrecio, responsable);

        inmueble.setPrecio(nuevoPrecio);
        historialModificaciones.push(cambio);
    }

    public void modificarArea(String codigo, double nuevaArea, String responsable) {
        Inmueble inmueble = buscarPorCodigo(codigo);
        if (inmueble == null) throw new RuntimeException("Inmueble no encontrado: " + codigo);

        CambioInmueble cambio = new CambioInmueble(
                codigo, TipoCambio.MODIFICACION_CAMPO,
                "area", inmueble.getArea(), nuevaArea, responsable);

        inmueble.setArea(nuevaArea);
        historialModificaciones.push(cambio);
    }

    public void modificarHabitaciones(String codigo, int nuevaCantidad, String responsable) {
        Inmueble inmueble = buscarPorCodigo(codigo);
        if (inmueble == null) throw new RuntimeException("Inmueble no encontrado: " + codigo);

        CambioInmueble cambio = new CambioInmueble(
                codigo, TipoCambio.MODIFICACION_CAMPO,
                "numeroHabitaciones", inmueble.getNumeroHabitaciones(), nuevaCantidad, responsable);

        inmueble.setNumeroHabitaciones(nuevaCantidad);
        historialModificaciones.push(cambio);
    }

    /**
     * Deshace el último cambio realizado en publicaciones.
     * Restaura el valor anterior del campo modificado.
     */
    public CambioInmueble deshacerUltimaModificacion() {
        if (historialModificaciones.isEmpty()) {
            throw new RuntimeException("No hay modificaciones para deshacer");
        }

        CambioInmueble ultimo = historialModificaciones.pop();
        Inmueble inmueble = buscarPorCodigo(ultimo.getCodigoInmueble());
        if (inmueble == null) return ultimo; // ya no existe, solo se retorna el registro

        switch (ultimo.getCampoModificado()) {
            case "precio":
                inmueble.setPrecio((double) ultimo.getValorAnterior());
                break;
            case "area":
                inmueble.setArea((double) ultimo.getValorAnterior());
                break;
            case "numeroHabitaciones":
                inmueble.setNumeroHabitaciones((int) ultimo.getValorAnterior());
                break;
        }
        return ultimo;
    }

    // Pila 2: cambios de estado de la propiedad
    public void cambiarEstadoInmueble(String codigo, String nuevoEstado, String responsable) {
        Inmueble inmueble = buscarPorCodigo(codigo);
        if (inmueble == null) throw new RuntimeException("Inmueble no encontrado: " + codigo);

        CambioInmueble cambio = new CambioInmueble(
                codigo, TipoCambio.CAMBIO_ESTADO,
                "estadoInmueble", inmueble.getEstadoInmueble(), nuevoEstado, responsable);

        inmueble.setEstadoInmueble(nuevoEstado);
        historialEstados.push(cambio);
    }

    public void cambiarDisponibilidad(String codigo, boolean nuevaDisponibilidad, String responsable) {
        Inmueble inmueble = buscarPorCodigo(codigo);
        if (inmueble == null) throw new RuntimeException("Inmueble no encontrado: " + codigo);

        CambioInmueble cambio = new CambioInmueble(
                codigo, TipoCambio.CAMBIO_ESTADO,
                "disponibilidad", inmueble.isDisponibilidad(), nuevaDisponibilidad, responsable);

        inmueble.setDisponibilidad(nuevaDisponibilidad);
        historialEstados.push(cambio);
    }

    /**
     * Revierte el último cambio de estado de cualquier propiedad.
     */
    public CambioInmueble revertirUltimoCambioEstado() {
        if (historialEstados.isEmpty()) {
            throw new RuntimeException("No hay cambios de estado para revertir");
        }

        CambioInmueble ultimo = historialEstados.pop();
        Inmueble inmueble = buscarPorCodigo(ultimo.getCodigoInmueble());
        if (inmueble == null) return ultimo;

        switch (ultimo.getCampoModificado()) {
            case "estadoInmueble":
                inmueble.setEstadoInmueble((String) ultimo.getValorAnterior());
                break;
            case "disponibilidad":
                inmueble.setDisponibilidad((boolean) ultimo.getValorAnterior());
                break;
        }
        return ultimo;
    }

    // Pila 3: historial de acciones administrativas
    public void registrarAccionAdmin(String codigoInmueble, String descripcion, String responsable) {
        CambioInmueble accion = new CambioInmueble(codigoInmueble, descripcion, responsable);
        historialAccionesAdmin.push(accion);
    }

    public void imprimirHistorialAcciones() {
        if (historialAccionesAdmin.isEmpty()) {
            System.out.println("No hay acciones administrativas registradas.");
            return;
        }
        System.out.println("=== HISTORIAL DE ACCIONES ADMINISTRATIVAS (más reciente primero) ===");
        for (CambioInmueble accion : historialAccionesAdmin) {
            System.out.println(accion);
        }
    }

    // Utilidades de consulta
    public CambioInmueble verUltimaModificacion() {
        return historialModificaciones.isEmpty() ? null : historialModificaciones.peek();
    }

    public CambioInmueble verUltimoCambioEstado() {
        return historialEstados.isEmpty() ? null : historialEstados.peek();
    }

    public int totalModificacionesPendientes() {
        return historialModificaciones.size();
    }

    public SimpleLinkedList<Inmueble> getInmuebles() {
        return inmuebles;
    }
}