package proyectofinal.Personal;

public class Asesor {
    private String identificacion;
    private String nombre;
    private String contacto;
    private String especialidadZona;
    private Object inmuebleAsignados; // estructura de datos a definir
    private Object visitasAgendadas; // estructura de datos a definir
    private int cierresRealizados;

    public Asesor(String identificacion, String nombre, String contacto, String especialidadZona,
            Object inmuebleAsignados, Object visitasAgendadas, int cierresRealizados) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.contacto = contacto;
        this.especialidadZona = especialidadZona;
        this.inmuebleAsignados = inmuebleAsignados;
        this.visitasAgendadas = visitasAgendadas;
        this.cierresRealizados = cierresRealizados;
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

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getEspecialidadZona() {
        return especialidadZona;
    }

    public void setEspecialidadZona(String especialidadZona) {
        this.especialidadZona = especialidadZona;
    }

    public Object getInmuebleAsignados() {
        return inmuebleAsignados;
    }

    public void setInmuebleAsignados(Object inmuebleAsignados) {
        this.inmuebleAsignados = inmuebleAsignados;
    }

    public Object getVisitasAgendadas() {
        return visitasAgendadas;
    }

    public void setVisitasAgendadas(Object visitasAgendadas) {
        this.visitasAgendadas = visitasAgendadas;
    }

    public int getCierresRealizados() {
        return cierresRealizados;
    }

    public void setCierresRealizados(int cierresRealizados) {
        this.cierresRealizados = cierresRealizados;
    }

    @Override
    public String toString() {
        return "Identificación: " + identificacion + " | Nombre: " + nombre + " | Contacto: " + contacto
                + " | Especialidad/Zona: " + especialidadZona + " | Inmuebles Asignados: " + inmuebleAsignados
                + " | Visitas Agendadas: " + visitasAgendadas + " | Cierres Realizados: " + cierresRealizados;
    }
}
