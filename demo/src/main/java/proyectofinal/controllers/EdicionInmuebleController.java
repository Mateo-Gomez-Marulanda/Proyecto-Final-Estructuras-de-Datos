package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Inmueble.ZoneProperty; // <-- NUEVO: Importación del Enum de Zona
import proyectofinal.Personal.Advisor;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;

public class EdicionInmuebleController {

    // ── Mismos fx:id que registro-inmueble.fxml ───────────────
    @FXML private TextField                campoCodigo;
    @FXML private ComboBox<TypeProperty>   campoTipo;
    @FXML private TextField                campoDireccion;
    @FXML private TextField                campoCiudad;
    @FXML private ComboBox<ZoneProperty>   campoZona;     // <-- CORREGIDO: Ahora es ComboBox
    @FXML private TextField                campoArea;
    @FXML private TextField                campoPrecio;
    @FXML private Spinner<Integer>         campoHabitaciones;
    @FXML private Spinner<Integer>         campoBanos;
    @FXML private ComboBox<String>         campoFinalidad;
    @FXML private ComboBox<String>         campoEstado;
    @FXML private CheckBox                 campoDisponible;
    @FXML private ComboBox<Advisor>        campoAsesor;
    @FXML private Label                    labelError;

    private Property propertyToEdit;
    private Runnable onEdicionExitosa;

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        campoTipo.getItems().setAll(TypeProperty.values());
        campoZona.getItems().setAll(ZoneProperty.values()); // <-- NUEVO: Cargamos los valores NORTE, SUR, CENTRO
        campoFinalidad.getItems().setAll("Venta", "Arriendo");
        campoEstado.getItems().setAll(
                "Disponible", "En negociación", "Vendido",
                "Arrendado", "En mantenimiento", "Reservado");
        configurarComboAsesor();
        configurarSpinners();
    }

    private void configurarComboAsesor() {
        campoAsesor.setConverter(new StringConverter<>() {
            @Override public String toString(Advisor a) {
                return a == null ? "Sin asignar" : a.getName();
            }
            @Override public Advisor fromString(String s) { return null; }
        });
        campoAsesor.getItems().add(null);
        for (Advisor a : AppContext.getInstance().getAdvisors()) {
            campoAsesor.getItems().add(a);
        }
    }

    private void configurarSpinners() {
        campoHabitaciones.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0));
        campoBanos.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
    }

    // ─────────────────────────────────────────────────────────
    // API pública: InmueblesController inyecta propiedad + callback
    // ─────────────────────────────────────────────────────────

    public void setPropertyToEdit(Property property) {
        this.propertyToEdit = property;
        preCargarCampos();
    }

    public void setOnEdicionExitosa(Runnable callback) {
        this.onEdicionExitosa = callback;
    }

    private void preCargarCampos() {
        campoCodigo.setText(propertyToEdit.getCode());
        campoCodigo.setDisable(true);          // código inmutable

        campoDireccion.setText(propertyToEdit.getAddress());
        campoCiudad.setText(propertyToEdit.getCity());
        campoZona.setValue(propertyToEdit.getZone()); // <-- CORREGIDO: Usamos setValue para inyectar el Enum actual
        campoTipo.setValue(propertyToEdit.getType());
        campoFinalidad.setValue(propertyToEdit.getPurpose());
        campoEstado.setValue(propertyToEdit.getPropertyStatus());
        campoPrecio.setText(String.valueOf(propertyToEdit.getPrice()));
        campoArea.setText(String.valueOf(propertyToEdit.getArea()));
        campoHabitaciones.getValueFactory().setValue(propertyToEdit.getRooms());
        campoBanos.getValueFactory().setValue(propertyToEdit.getBathrooms());
        campoDisponible.setSelected(propertyToEdit.isAvailable());
        campoAsesor.setValue(propertyToEdit.getResponsibleAdvisor());
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void guardarCambios() {
        ocultarError();

        String       direccion = campoDireccion.getText().trim();
        String       ciudad    = campoCiudad.getText().trim();
        ZoneProperty zona      = campoZona.getValue(); // <-- CORREGIDO: Leemos el Enum seleccionado
        TypeProperty tipo      = campoTipo.getValue();
        String       finalidad = campoFinalidad.getValue();
        String       estado    = campoEstado.getValue();

        if (direccion.isEmpty()) { mostrarError("La dirección es obligatoria.");        return; }
        if (ciudad.isEmpty())    { mostrarError("La ciudad es obligatoria.");           return; }
        if (zona == null)        { mostrarError("Seleccione la zona del inmueble.");    return; } // <-- CORREGIDO
        if (tipo == null)        { mostrarError("Seleccione el tipo de inmueble.");     return; }
        if (finalidad == null)   { mostrarError("Seleccione la finalidad.");            return; }
        if (estado == null)      { mostrarError("Seleccione el estado del inmueble.");  return; }

        double precio, area;
        try {
            precio = Double.parseDouble(campoPrecio.getText().trim());
            if (precio <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número positivo."); return;
        }
        try {
            area = Double.parseDouble(campoArea.getText().trim());
            if (area <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarError("El área debe ser un número positivo."); return;
        }

        PropertyManager pm          = AppContext.getInstance().getPropertyManager();
        String          cod         = propertyToEdit.getCode();
        String          responsable = "Admin";

        if (Double.compare(precio, propertyToEdit.getPrice()) != 0)
            pm.modifyPrice(cod, precio, responsable);

        if (Double.compare(area, propertyToEdit.getArea()) != 0)
            pm.modifyArea(cod, area, responsable);

        if (campoHabitaciones.getValue() != propertyToEdit.getRooms())
            pm.modifyRooms(cod, campoHabitaciones.getValue(), responsable);

        if (!estado.equals(propertyToEdit.getPropertyStatus()))
            pm.changePropertyStatus(cod, estado, responsable);

        if (!direccion.equals(propertyToEdit.getAddress())) {
            pm.registerAdminAction(cod, "Dirección: '" + propertyToEdit.getAddress()
                    + "' → '" + direccion + "'", responsable);
            propertyToEdit.setAddress(direccion);
        }
        if (!ciudad.equals(propertyToEdit.getCity())) {
            pm.registerAdminAction(cod, "Ciudad: '" + propertyToEdit.getCity()
                    + "' → '" + ciudad + "'", responsable);
            propertyToEdit.setCity(ciudad);
        }
        // <-- CORREGIDO: Lógica de actualización para el Enum de la zona
        if (zona != propertyToEdit.getZone()) {
            pm.registerAdminAction(cod, "Zona: '" + propertyToEdit.getZone().name()
                    + "' → '" + zona.name() + "'", responsable);
            propertyToEdit.setZone(zona);
        }
        if (tipo != propertyToEdit.getType()) {
            pm.registerAdminAction(cod, "Tipo: " + propertyToEdit.getType()
                    + " → " + tipo, responsable);
            propertyToEdit.setType(tipo);
        }
        if (!finalidad.equals(propertyToEdit.getPurpose())) {
            pm.registerAdminAction(cod, "Finalidad: '" + propertyToEdit.getPurpose()
                    + "' → '" + finalidad + "'", responsable);
            propertyToEdit.setPurpose(finalidad);
        }
        if (campoBanos.getValue() != propertyToEdit.getBathrooms()) {
            pm.registerAdminAction(cod, "Baños: " + propertyToEdit.getBathrooms()
                    + " → " + campoBanos.getValue(), responsable);
            propertyToEdit.setBathrooms(campoBanos.getValue());
        }
        if (campoDisponible.isSelected() != propertyToEdit.isAvailable()) {
            pm.registerAdminAction(cod, "Disponibilidad: " + propertyToEdit.isAvailable()
                    + " → " + campoDisponible.isSelected(), responsable);
            propertyToEdit.setAvailable(campoDisponible.isSelected());
        }

        Advisor nuevoAsesor = campoAsesor.getValue();
        if (nuevoAsesor != propertyToEdit.getResponsibleAdvisor()) {
            if (propertyToEdit.getResponsibleAdvisor() != null) {
                propertyToEdit.getResponsibleAdvisor().removeProperty(propertyToEdit);
            }
            if (nuevoAsesor != null) {
                nuevoAsesor.assignProperty(propertyToEdit);
            }
            String oldNombre = propertyToEdit.getResponsibleAdvisor() != null
                    ? propertyToEdit.getResponsibleAdvisor().getName() : "Sin asignar";
            String newNombre = nuevoAsesor != null ? nuevoAsesor.getName() : "Sin asignar";
            pm.registerAdminAction(cod, "Asesor: '" + oldNombre
                    + "' → '" + newNombre + "'", responsable);
            propertyToEdit.setResponsibleAdvisor(nuevoAsesor);
        }

        if (onEdicionExitosa != null) onEdicionExitosa.run();
        cerrarVentana();
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────

    private void cerrarVentana() {
        ((Stage) campoCodigo.getScene().getWindow()).close();
    }

    private void mostrarError(String msg) {
        labelError.setText("⚠  " + msg);
        labelError.setVisible(true);
        labelError.setManaged(true);
    }

    private void ocultarError() {
        labelError.setVisible(false);
        labelError.setManaged(false);
    }
}