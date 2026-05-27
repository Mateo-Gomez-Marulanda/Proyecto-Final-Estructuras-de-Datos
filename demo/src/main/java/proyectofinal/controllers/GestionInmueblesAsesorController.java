package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Advisor;

public class GestionInmueblesAsesorController {

    // ── Header ───────────────────────────────────────────────
    @FXML
    private Label labelTitulo;
    @FXML
    private Label labelResumen;

    // ── Tabla asignados ──────────────────────────────────────
    @FXML
    private TableView<Property> tablaAsignados;
    @FXML
    private TableColumn<Property, String> colAsigCodigo;
    @FXML
    private TableColumn<Property, String> colAsigDireccion;
    @FXML
    private TableColumn<Property, String> colAsigTipo;
    @FXML
    private TableColumn<Property, String> colAsigPrecio;
    @FXML
    private Button btnQuitar;

    // ── Tabla disponibles ────────────────────────────────────
    @FXML
    private TableView<Property> tablaDisponibles;
    @FXML
    private TableColumn<Property, String> colDispCodigo;
    @FXML
    private TableColumn<Property, String> colDispDireccion;
    @FXML
    private TableColumn<Property, String> colDispTipo;
    @FXML
    private TableColumn<Property, String> colDispPrecio;
    @FXML
    private Button btnAsignar;
    @FXML
    private TextField campoBusqueda;

    private Advisor advisor;
    private Runnable onCambios;

    private ObservableList<Property> asignadosList;
    private ObservableList<Property> disponiblesMaster;
    private FilteredList<Property> disponiblesFiltrados;

    // ─────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────

    public void setAdvisor(Advisor advisor) {
        this.advisor = advisor;
        labelTitulo.setText("Inmuebles de: " + advisor.getName());
        cargarDatos();
        configurarSelecciones();
    }

    public void setOnCambios(Runnable callback) {
        this.onCambios = callback;
    }

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarColumnas();
    }

    private void configurarColumnas() {
        // Asignados
        colAsigCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colAsigDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colAsigTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().toString()));
        colAsigPrecio.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getPrice())));

        // Disponibles
        colDispCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colDispDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colDispTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().toString()));
        colDispPrecio.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getPrice())));
    }

    private void cargarDatos() {
        // ── Asignados: los que ya tiene el asesor ────────────
        asignadosList = FXCollections.observableArrayList();
        for (Property p : advisor.getAssignedPropertiesList()) {
            asignadosList.add(p);
        }
        tablaAsignados.setItems(asignadosList);

        // ── Disponibles: todos los del catálogo que NO están asignados a este asesor
        disponiblesMaster = FXCollections.observableArrayList();
        for (Property p : AppContext.getInstance().getPropertyManager().getProperties()) {
            if (!estaAsignado(p)) {
                disponiblesMaster.add(p);
            }
        }
        disponiblesFiltrados = new FilteredList<>(disponiblesMaster, p -> true);
        tablaDisponibles.setItems(disponiblesFiltrados);

        actualizarResumen();
    }

    private void configurarSelecciones() {
        tablaAsignados.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> btnQuitar.setDisable(sel == null));
        tablaDisponibles.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> btnAsignar.setDisable(sel == null));
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void asignarInmueble() {
        Property selected = tablaDisponibles.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        // Quitar del asesor anterior si tenía uno
        if (selected.getResponsibleAdvisor() != null) {
            selected.getResponsibleAdvisor().removeProperty(selected);
        }

        // Asignar al asesor actual
        advisor.assignProperty(selected);
        selected.setResponsibleAdvisor(advisor);

        // Mover entre tablas
        disponiblesMaster.remove(selected);
        asignadosList.add(selected);

        tablaDisponibles.getSelectionModel().clearSelection();
        actualizarResumen();
        notificarCambios();
    }

    @FXML
    public void quitarInmueble() {
        Property selected = tablaAsignados.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        advisor.removeProperty(selected);
        selected.setResponsibleAdvisor(null);

        // Mover entre tablas
        asignadosList.remove(selected);
        disponiblesMaster.add(selected);

        tablaAsignados.getSelectionModel().clearSelection();
        actualizarResumen();
        notificarCambios();
    }

    @FXML
    public void filtrar() {
        String texto = campoBusqueda.getText().toLowerCase();
        disponiblesFiltrados.setPredicate(p -> texto.isEmpty()
                || p.getCode().toLowerCase().contains(texto)
                || p.getAddress().toLowerCase().contains(texto)
                || p.getCity().toLowerCase().contains(texto));
    }

    @FXML
    public void cerrar() {
        ((Stage) labelTitulo.getScene().getWindow()).close();
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────

    private boolean estaAsignado(Property p) {
        for (Property asig : advisor.getAssignedPropertiesList()) {
            if (asig.getCode().equals(p.getCode()))
                return true;
        }
        return false;
    }

    private void actualizarResumen() {
        labelResumen.setText("Asignados: " + asignadosList.size()
                + "  ·  Disponibles en catálogo: " + disponiblesMaster.size());
    }

    private void notificarCambios() {
        if (onCambios != null)
            onCambios.run();
    }
}