package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Advisor;

public class AsesoresController {

    // ─── Table ───────────────────────────────────────────────
    @FXML
    private TableView<Advisor> tablaAsesores;
    @FXML
    private TableColumn<Advisor, String> colIdentificacion;
    @FXML
    private TableColumn<Advisor, String> colNombre;
    @FXML
    private TableColumn<Advisor, String> colContacto;
    @FXML
    private TableColumn<Advisor, String> colZona;
    @FXML
    private TableColumn<Advisor, String> colCierres;
    @FXML
    private TableColumn<Advisor, String> colInmuebles;

    // ─── Filters ─────────────────────────────────────────────
    @FXML
    private TextField campoBusqueda;
    @FXML
    private ComboBox<String> filtroZona;

    // ─── Detail panel ─────────────────────────────────────────
    @FXML
    private VBox panelDetalle;
    @FXML
    private Label detalleNombre;
    @FXML
    private Label detalleContacto;
    @FXML
    private Label detalleZona;
    @FXML
    private Label detalleCierres;
    @FXML
    private ListView<String> listaInmueblesAsesor;

    // ─── Buttons ─────────────────────────────────────────────
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnGestionarInmuebles;

    private ObservableList<Advisor> masterList;
    private FilteredList<Advisor> filteredList;

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
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getName()));
        colContacto.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getContactInfo()));
        colZona.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getZoneSpecialty()));
        colCierres.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getCompletedClosings())));
        colInmuebles.setCellValueFactory(d -> {
            int count = 0;
            Advisor a = d.getValue();
            for (Property ignored : a.getAssignedPropertiesList())
                count++;
            return new SimpleStringProperty(String.valueOf(count));
        });
    }

    private void cargarDatos() {
        masterList = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(masterList, a -> true);

        // filtro de zonas populares
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
                    if (hay)
                        mostrarDetalle(selected);
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
        String zona = filtroZona.getValue();

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

    @FXML
    public void abrirFormularioRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/registro-asesor.fxml"));
            Parent root = loader.load();

            RegistroAsesorController ctrl = loader.getController();
            ctrl.setOnRegistroExitoso(nuevoAsesor -> {
                masterList.add(nuevoAsesor);
                if (!filtroZona.getItems().contains(nuevoAsesor.getZoneSpecialty())) {
                    filtroZona.getItems().add(nuevoAsesor.getZoneSpecialty());
                }
            });

            Stage dialog = new Stage();
            dialog.setTitle("Registrar asesor");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaAsesores.getScene().getWindow());
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            if (!tablaAsesores.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaAsesores.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.showAndWait();

        } catch (IOException e) {
            mostrarInfo("No se pudo abrir el formulario:\n" + e.getMessage());
        }
    }

    @FXML
    public void editarAsesorSeleccionado() {
        Advisor selected = tablaAsesores.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/editar-asesor.fxml"));
            Parent root = loader.load();

            EdicionAsesorController ctrl = loader.getController();
            ctrl.setAdvisorToEdit(selected);
            ctrl.setOnEdicionExitosa(() -> {
                tablaAsesores.refresh();
                mostrarDetalle(selected);
            });

            Stage dialog = new Stage();
            dialog.setTitle("Editar asesor — " + selected.getName());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaAsesores.getScene().getWindow());
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            if (!tablaAsesores.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaAsesores.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.showAndWait();

        } catch (IOException e) {
            mostrarInfo("No se pudo abrir el formulario:\n" + e.getMessage());
        }
    }

    @FXML
    public void gestionarInmueblesAsesor() {
        Advisor selected = tablaAsesores.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/gestionar-inmuebles-asesor.fxml"));
            Parent root = loader.load();

            GestionInmueblesAsesorController ctrl = loader.getController();
            ctrl.setAdvisor(selected);
            ctrl.setOnCambios(() -> {
                tablaAsesores.refresh();
                mostrarDetalle(selected); // actualiza panel lateral
            });

            Stage dialog = new Stage();
            dialog.setTitle("Gestionar inmuebles — " + selected.getName());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaAsesores.getScene().getWindow());

            Scene scene = new Scene(root);
            if (!tablaAsesores.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaAsesores.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.show();

        } catch (IOException e) {
            mostrarInfo("No se pudo abrir el gestor:\n" + e.getMessage());
        }
    }

    @FXML
    public void eliminarAsesorSeleccionado() {
        Advisor selected = tablaAsesores.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar al asesor " + selected.getName() + "?"
                        + "\nSus inmuebles asignados quedarán sin asesor responsable.",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                for (Property p : AppContext.getInstance().getPropertyManager().getProperties()) {
                    if (p.getResponsibleAdvisor() != null
                            && p.getResponsibleAdvisor().getId().equals(selected.getId())) {
                        p.setResponsibleAdvisor(null);
                    }
                }
                AppContext.getInstance().getAdvisors().remove(selected);
                masterList.remove(selected);
                panelDetalle.setVisible(false);
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
