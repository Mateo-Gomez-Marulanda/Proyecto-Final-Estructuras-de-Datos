package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Advisor;

public class AsesoresController {

    // ─── Table ───────────────────────────────────────────────
    @FXML private TableView<Advisor>             tablaAsesores;
    @FXML private TableColumn<Advisor, String>   colIdentificacion;
    @FXML private TableColumn<Advisor, String>   colNombre;
    @FXML private TableColumn<Advisor, String>   colContacto;
    @FXML private TableColumn<Advisor, String>   colZona;
    @FXML private TableColumn<Advisor, String>   colCierres;
    @FXML private TableColumn<Advisor, String>   colInmuebles;

    // ─── Filters ─────────────────────────────────────────────
    @FXML private TextField        campoBusqueda;
    @FXML private ComboBox<String> filtroZona;

    // ─── Detail panel ─────────────────────────────────────────
    @FXML private VBox   panelDetalle;
    @FXML private Label  detalleNombre;
    @FXML private Label  detalleContacto;
    @FXML private Label  detalleZona;
    @FXML private Label  detalleCierres;
    @FXML private ListView<String> listaInmueblesAsesor;

    // ─── Buttons ─────────────────────────────────────────────
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGestionarInmuebles;

    private ObservableList<Advisor> masterList;
    private FilteredList<Advisor>   filteredList;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatos();
        configurarSeleccion();
    }

    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnas() {
        colIdentificacion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getId()));
        colNombre.setCellValueFactory(d ->         new SimpleStringProperty(d.getValue().getName()));
        colContacto.setCellValueFactory(d ->       new SimpleStringProperty(d.getValue().getContactInfo()));
        colZona.setCellValueFactory(d ->           new SimpleStringProperty(d.getValue().getZoneSpecialty()));
        colCierres.setCellValueFactory(d ->        new SimpleStringProperty(
                String.valueOf(d.getValue().getCompletedClosings())));
        colInmuebles.setCellValueFactory(d -> {
            // Count assigned properties
            int count = 0;
            Advisor a = d.getValue();
            for (Property ignored : a.getAssignedPropertiesList()) count++;
            return new SimpleStringProperty(String.valueOf(count));
        });
    }

    private void cargarDatos() {
        masterList   = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(masterList, a -> true);

        // Populate zone filter from loaded advisors
        for (Advisor a : AppContext.getInstance().getAdvisors()) {
            masterList.add(a);
            if (a.getZoneSpecialty() != null && !filtroZona.getItems().contains(a.getZoneSpecialty())) {
                filtroZona.getItems().add(a.getZoneSpecialty());
            }
        }
        tablaAsesores.setItems(filteredList);
    }

    private void configurarSeleccion() {
        tablaAsesores.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    boolean hay = selected != null;
                    btnEditar.setDisable(!hay);
                    btnEliminar.setDisable(!hay);
                    btnGestionarInmuebles.setDisable(!hay);
                    panelDetalle.setVisible(hay);
                    if (hay) mostrarDetalle(selected);
                });
    }

    // ─────────────────────────────────────────────────────────
    // Detail panel
    // ─────────────────────────────────────────────────────────

    private void mostrarDetalle(Advisor advisor) {
        detalleNombre.setText(advisor.getName());
        detalleContacto.setText(advisor.getContactInfo());
        detalleZona.setText("Zona: " + advisor.getZoneSpecialty());
        detalleCierres.setText("Cierres: " + advisor.getCompletedClosings());

        ObservableList<String> propsList = FXCollections.observableArrayList();
        for (Property p : advisor.getAssignedPropertiesList()) {
            propsList.add(p.getCode() + " — " + p.getAddress());
        }
        listaInmueblesAsesor.setItems(propsList);
    }

    // ─────────────────────────────────────────────────────────
    // Filters
    // ─────────────────────────────────────────────────────────

    @FXML
    public void filtrarTabla() {
        String texto = campoBusqueda.getText().toLowerCase();
        String zona  = filtroZona.getValue();

        filteredList.setPredicate(a -> {
            boolean matchTexto = texto.isEmpty()
                    || a.getId().toLowerCase().contains(texto)
                    || a.getName().toLowerCase().contains(texto)
                    || a.getZoneSpecialty().toLowerCase().contains(texto);
            boolean matchZona = zona == null || a.getZoneSpecialty().equalsIgnoreCase(zona);
            return matchTexto && matchZona;
        });
    }

    @FXML
    public void limpiarFiltros() {
        campoBusqueda.clear();
        filtroZona.setValue(null);
        filteredList.setPredicate(a -> true);
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML public void abrirFormularioRegistro()  { mostrarInfo("Formulario de registro próximamente."); }
    @FXML public void editarAsesorSeleccionado() { mostrarInfo("Edición próximamente."); }
    @FXML public void gestionarInmueblesAsesor() { mostrarInfo("Gestión de inmuebles próximamente."); }

    @FXML
    public void eliminarAsesorSeleccionado() {
        Advisor selected = tablaAsesores.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al asesor " + selected.getName() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                AppContext.getInstance().getAdvisors().remove(selected);
                masterList.remove(selected);
                panelDetalle.setVisible(false);
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    private void mostrarInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
}
