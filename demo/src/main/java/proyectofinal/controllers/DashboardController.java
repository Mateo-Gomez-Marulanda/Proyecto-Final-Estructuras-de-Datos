package proyectofinal.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;

import proyectofinal.SistemaGestion.GestionInmuebles.PropertyChange;

public class DashboardController {

    @FXML private Label numInmuebles;
    @FXML private Label numClientes;
    @FXML private Label numVisitas;
    @FXML private Label numAlertas;

    @FXML private TableView<PropertyChange>    tablaUltimosMovimientos;
    @FXML private TableColumn<PropertyChange, String> colAccionFecha;
    @FXML private TableColumn<PropertyChange, String> colAccionTipo;
    @FXML private TableColumn<PropertyChange, String> colAccionDetalle;
    @FXML private TableColumn<PropertyChange, String> colAccionResponsable;

    @FXML
    public void initialize() {
        cargarContadores();
        configurarTablaAcciones();
        cargarUltimasAcciones();
    }

    private void cargarContadores() {
        AppContext ctx = AppContext.getInstance();

        int totalInmuebles = ctx.getPropertyManager().getProperties().size();
        int totalClientes  = ctx.getClientManager().getAllClients().size();
        int totalVisitas   = ctx.getVisitManager().getTotalPending();
        int totalAlertas   = ctx.getPendingAlerts().size();

        numInmuebles.setText(String.valueOf(totalInmuebles));
        numClientes.setText(String.valueOf(totalClientes));
        numVisitas.setText(String.valueOf(totalVisitas));
        numAlertas.setText(String.valueOf(totalAlertas));
    }

    private void configurarTablaAcciones() {
        colAccionFecha.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getDateTime().toString()));
        colAccionTipo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getChangeType().toString()));
        colAccionDetalle.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getModifiedField() != null
                                ? data.getValue().getPropertyCode() + " → " + data.getValue().getModifiedField()
                                : data.getValue().getPropertyCode()));
        colAccionResponsable.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getResponsiblePerson()));
    }

    private void cargarUltimasAcciones() {
        // Not yet directly accessible from PropertyManager — placeholder
        ObservableList<PropertyChange> acciones = FXCollections.observableArrayList();
        tablaUltimosMovimientos.setItems(acciones);
    }

    // ─── Quick-access buttons from Dashboard ─────────────────

    @FXML public void irARegistrarInmueble() { navegarA("/proyectofinal/views/inmuebles-content.fxml"); }
    @FXML public void irARegistrarCliente()  { navegarA("/proyectofinal/views/clientes-content.fxml"); }
    @FXML public void irAAgendarVisita()     { navegarA("/proyectofinal/views/visitas-content.fxml"); }
    @FXML public void irARegistrarOperacion(){ navegarA("/proyectofinal/views/operaciones-content.fxml"); }
    @FXML public void irAAlertas()           { navegarA("/proyectofinal/views/alertas-content.fxml"); }

    private void navegarA(String path) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(path));
            javafx.scene.Node vista = loader.load();
            // Find the parent StackPane (contenidoCentral) and replace content
            javafx.scene.layout.StackPane parent =
                    (javafx.scene.layout.StackPane) numInmuebles.getScene().lookup("#contenidoCentral");
            if (parent != null) parent.getChildren().setAll(vista);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Levanta la vista modal con el reporte analítico y estadístico general.
     */
    @FXML
    public void abrirVentanaReportes() {
        try {
            // Se usa el prefijo /proyectofinal/views/ manteniendo consistencia con tus layouts
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/Reportes.fxml"));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Consola de Analítica y Reportes Gerenciales");
            dialog.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás para mantener el foco
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
            
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Error al abrir el módulo de analítica: " + e.getMessage(), ButtonType.OK).showAndWait();
            e.printStackTrace();
        }
    }
}
