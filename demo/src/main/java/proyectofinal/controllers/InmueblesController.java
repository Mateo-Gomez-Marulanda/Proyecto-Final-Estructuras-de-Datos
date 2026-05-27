package proyectofinal.controllers;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import proyectofinal.Inmueble.Property;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.SistemaGestion.GestionInmuebles.PropertyManager;

public class InmueblesController {

    // ─── Table ───────────────────────────────────────────────
    @FXML
    private TableView<Property> tablaInmuebles;
    @FXML
    private TableColumn<Property, String> colCodigo;
    @FXML
    private TableColumn<Property, String> colDireccion;
    @FXML
    private TableColumn<Property, String> colCiudad;
    @FXML
    private TableColumn<Property, String> colTipo;
    @FXML
    private TableColumn<Property, String> colFinalidad;
    @FXML
    private TableColumn<Property, String> colPrecio;
    @FXML
    private TableColumn<Property, String> colArea;
    @FXML
    private TableColumn<Property, String> colEstado;
    @FXML
    private TableColumn<Property, String> colDisponible;
    @FXML
    private TableColumn<Property, String> colAsesor;

    // ─── Filters ─────────────────────────────────────────────
    @FXML
    private TextField campoBusqueda;
    @FXML
    private ComboBox<TypeProperty> filtroTipo;
    @FXML
    private ComboBox<String> filtroFinalidad;
    @FXML
    private ComboBox<String> filtroDisponibilidad;

    // ─── Action buttons ──────────────────────────────────────
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnDeshacerCambio;
    @FXML
    private Button btnRevertirEstado;

    private ObservableList<Property> masterList;
    private FilteredList<Property> filteredList;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
        cargarDatos();
        configurarSeleccion();
    }

    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colCiudad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCity()));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().toString()));
        colFinalidad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPurpose()));
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getPrice())));
        colArea.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getArea() + " m²"));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getPropertyStatus()));
        colDisponible.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().isAvailable() ? "Sí" : "No"));
        colAsesor.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getResponsibleAdvisor() != null
                        ? d.getValue().getResponsibleAdvisor().getName()
                        : "—"));
    }

    private void configurarFiltros() {
        filtroTipo.getItems().setAll(TypeProperty.values());
        filtroFinalidad.getItems().setAll("Venta", "Arriendo");
        filtroDisponibilidad.getItems().setAll("Disponible", "No disponible");
    }

    private void cargarDatos() {
        masterList = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(masterList, p -> true);

        for (Property p : AppContext.getInstance().getPropertyManager().getProperties()) {
            masterList.add(p);
        }
        tablaInmuebles.setItems(filteredList);
    }

    private void configurarSeleccion() {
        tablaInmuebles.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    boolean haySeleccion = selected != null;
                    btnEditar.setDisable(!haySeleccion);
                    btnEliminar.setDisable(!haySeleccion);
                });
    }

    // ─────────────────────────────────────────────────────────
    // Filters
    // ─────────────────────────────────────────────────────────

    @FXML
    public void filtrarTabla() {
        String texto = campoBusqueda.getText().toLowerCase();
        TypeProperty tipo = filtroTipo.getValue();
        String finalidad = filtroFinalidad.getValue();
        String disponibilidad = filtroDisponibilidad.getValue();

        filteredList.setPredicate(p -> {
            boolean matchTexto = texto.isEmpty()
                    || p.getCode().toLowerCase().contains(texto)
                    || p.getAddress().toLowerCase().contains(texto)
                    || p.getCity().toLowerCase().contains(texto);

            boolean matchTipo = tipo == null || p.getType() == tipo;

            boolean matchFinalidad = finalidad == null
                    || p.getPurpose().equalsIgnoreCase(finalidad);

            boolean matchDisp = disponibilidad == null
                    || (disponibilidad.equals("Disponible") && p.isAvailable())
                    || (disponibilidad.equals("No disponible") && !p.isAvailable());

            return matchTexto && matchTipo && matchFinalidad && matchDisp;
        });
    }

    @FXML
    public void limpiarFiltros() {
        campoBusqueda.clear();
        filtroTipo.setValue(null);
        filtroFinalidad.setValue(null);
        filtroDisponibilidad.setValue(null);
        filteredList.setPredicate(p -> true);
    }

    // ─────────────────────────────────────────────────────────
    // CRUD actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void abrirFormularioRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/registro-inmueble.fxml"));
            Parent root = loader.load();

            RegistroInmuebleController ctrl = loader.getController();
            ctrl.setOnRegistroExitoso(nuevaProperty -> {
                try {
                    String responsable = AppContext.getInstance().getClientManager().getCurrent() != null
                            ? AppContext.getInstance().getClientManager().getCurrent().getName()
                            : "Admin";
                    AppContext.getInstance().getPropertyManager()
                            .registerProperty(nuevaProperty, responsable);
                    masterList.add(nuevaProperty);
                } catch (RuntimeException e) {
                    mostrarError("No se pudo registrar: " + e.getMessage());
                }
            });

            Stage dialog = new Stage();
            dialog.setTitle("Registrar inmueble");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaInmuebles.getScene().getWindow());
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            if (!tablaInmuebles.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaInmuebles.getScene().getStylesheets());
            }

            dialog.setScene(scene);
            dialog.showAndWait();

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de registro:\n" + e.getMessage());
        }
    }

    @FXML
    public void editarInmuebleSeleccionado() {
        Property selected = tablaInmuebles.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/editar-inmueble.fxml"));
            Parent root = loader.load();

            EdicionInmuebleController ctrl = loader.getController();
            ctrl.setPropertyToEdit(selected);
            // Callback: refresca la tabla para mostrar los valores actualizados
            ctrl.setOnEdicionExitosa(this::refrescarTabla);

            Stage dialog = new Stage();
            dialog.setTitle("Editar inmueble — " + selected.getCode());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaInmuebles.getScene().getWindow());
            dialog.setResizable(false);

            Scene scene = new Scene(root);
            if (!tablaInmuebles.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaInmuebles.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.showAndWait();

        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario de edición:\n" + e.getMessage());
        }
    }

    @FXML
    public void eliminarInmuebleSeleccionado() {
        Property selected = tablaInmuebles.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar el inmueble " + selected.getCode() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                AppContext.getInstance().getPropertyManager().getProperties().remove(selected);
                masterList.remove(selected);
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // Stack actions (undo / revert)
    // ─────────────────────────────────────────────────────────

    @FXML
    public void deshacerUltimoCambio() {
        try {
            PropertyManager pm = AppContext.getInstance().getPropertyManager();
            var change = pm.undoLastModification();
            mostrarInfo("Cambio deshecho: " + change.getModifiedField()
                    + " en " + change.getPropertyCode());
            refrescarTabla();
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void revertirUltimoCambioEstado() {
        try {
            PropertyManager pm = AppContext.getInstance().getPropertyManager();
            var change = pm.revertLastStatusChange();
            mostrarInfo("Estado revertido en: " + change.getPropertyCode());
            refrescarTabla();
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void verHistorialAcciones() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/historial-inmuebles.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Historial de cambios — Inmuebles");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaInmuebles.getScene().getWindow());

            Scene scene = new Scene(root);
            if (!tablaInmuebles.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaInmuebles.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.show();

        } catch (IOException e) {
            mostrarError("No se pudo abrir el historial:\n" + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────

    private void refrescarTabla() {
        masterList.clear();
        for (Property p : AppContext.getInstance().getPropertyManager().getProperties()) {
            masterList.add(p);
        }
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
