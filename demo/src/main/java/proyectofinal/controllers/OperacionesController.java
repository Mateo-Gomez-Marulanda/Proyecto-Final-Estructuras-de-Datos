package proyectofinal.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import proyectofinal.SistemaGestion.Observer.OperationEvent;
import proyectofinal.SistemaGestion.Observer.OperationObserver;
import proyectofinal.SistemaGestion.Observer.OperationPublisher;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;

/**
 * Manages the operations view AND implements OperationObserver.
 * When any BusinessOperation is published anywhere in the system,
 * this controller receives it and adds it to the table automatically.
 */
public class OperacionesController implements OperationObserver {

    // ─── Summary cards ───────────────────────────────────────
    @FXML private Label numArriendos;
    @FXML private Label numVentas;
    @FXML private Label numRenovaciones;
    @FXML private Label numCancelaciones;

    // ─── Table ───────────────────────────────────────────────
    @FXML private TableView<BusinessOperation>             tablaOperaciones;
    @FXML private TableColumn<BusinessOperation, String>   colId;
    @FXML private TableColumn<BusinessOperation, String>   colTipo;
    @FXML private TableColumn<BusinessOperation, String>   colInmueble;
    @FXML private TableColumn<BusinessOperation, String>   colCliente;
    @FXML private TableColumn<BusinessOperation, String>   colAsesor;
    @FXML private TableColumn<BusinessOperation, String>   colValor;
    @FXML private TableColumn<BusinessOperation, String>   colComision;
    @FXML private TableColumn<BusinessOperation, String>   colFecha;
    @FXML private TableColumn<BusinessOperation, String>   colEstadoProceso;

    // ─── Buttons ─────────────────────────────────────────────
    @FXML private Button btnVerDetalles;
    @FXML private Button btnEditarEstado;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatos();
        configurarSeleccion();

        // Register this controller as an Observer so the table updates automatically
        OperationPublisher.getInstance().subscribe(this);
    }

    // ─────────────────────────────────────────────────────────
    // OperationObserver implementation
    // ─────────────────────────────────────────────────────────

    @Override
    public void onOperationEvent(OperationEvent event) {
        // Always update UI on the JavaFX thread
        Platform.runLater(() -> {
            if (event.getEventType() == OperationEvent.EventType.OPERATION_CREATED) {
                AppContext.getInstance().getOperations().add(event.getOperation());
            }
            actualizarContadores();
        });
    }

    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnas() {
        // BusinessOperation has JavaFX properties — use them directly
        colId.setCellValueFactory(d ->           d.getValue().identifierProperty());
        colTipo.setCellValueFactory(d ->          d.getValue().operationTypeProperty().asString());
        colInmueble.setCellValueFactory(d ->      d.getValue().relatedPropertyProperty().asString()
                .map(s -> d.getValue().getRelatedProperty() != null
                        ? d.getValue().getRelatedProperty().getCode() : "—"));
        colCliente.setCellValueFactory(d ->       d.getValue().clientProperty().asString()
                .map(s -> d.getValue().getClient() != null
                        ? d.getValue().getClient().getName() : "—"));
        colAsesor.setCellValueFactory(d ->        d.getValue().advisorProperty().asString()
                .map(s -> d.getValue().getAdvisor() != null
                        ? d.getValue().getAdvisor().getName() : "—"));
        colValor.setCellValueFactory(d ->         new javafx.beans.property.SimpleStringProperty(
                d.getValue().getAgreedValueFormatted()));
        colComision.setCellValueFactory(d ->      new javafx.beans.property.SimpleStringProperty(
                d.getValue().getCommissionFormatted()));
        colFecha.setCellValueFactory(d ->         new javafx.beans.property.SimpleStringProperty(
                d.getValue().getDateFormatted()));
        colEstadoProceso.setCellValueFactory(d -> d.getValue().processStatusProperty().asString());
    }

    private void cargarDatos() {
        // Bind directly to the shared ObservableList in AppContext
        tablaOperaciones.setItems(AppContext.getInstance().getOperations());
        actualizarContadores();
    }

    private void configurarSeleccion() {
        tablaOperaciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    boolean hay = selected != null;
                    btnVerDetalles.setDisable(!hay);
                    btnEditarEstado.setDisable(!hay);
                });
    }

    private void actualizarContadores() {
        int arriendos = 0, ventas = 0, renovaciones = 0, cancelaciones = 0;
        for (BusinessOperation op : AppContext.getInstance().getOperations()) {
            switch (op.getOperationType()) {
                case RENTAL              -> arriendos++;
                case SALE                -> ventas++;
                case LEASE_RENEWAL       -> renovaciones++;
                case BUSINESS_CANCELLATION -> cancelaciones++;
            }
        }
        numArriendos.setText(String.valueOf(arriendos));
        numVentas.setText(String.valueOf(ventas));
        numRenovaciones.setText(String.valueOf(renovaciones));
        numCancelaciones.setText(String.valueOf(cancelaciones));
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void verDetallesOperacion() {
        BusinessOperation op = tablaOperaciones.getSelectionModel().getSelectedItem();
        if (op == null) return;
        mostrarInfo(op.toString());
    }

    @FXML
    public void editarEstadoOperacion() {
        BusinessOperation op = tablaOperaciones.getSelectionModel().getSelectedItem();
        if (op == null) return;

        if (op.isCompleted() || op.isCancelled()) {
            mostrarInfo("No se puede modificar una operación en estado: " + op.getProcessStatus());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Avanzar el estado de la operación " + op.getIdentifier() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    op.advanceStatus();
                    tablaOperaciones.refresh();
                    actualizarContadores();
                } catch (RuntimeException e) {
                    mostrarError(e.getMessage());
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    private void mostrarInfo(String msg)  { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}
