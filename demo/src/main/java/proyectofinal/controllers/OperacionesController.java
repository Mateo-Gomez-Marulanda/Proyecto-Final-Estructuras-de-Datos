package proyectofinal.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import proyectofinal.SistemaGestion.Observer.OperationEvent;
import proyectofinal.SistemaGestion.Observer.OperationObserver;
import proyectofinal.SistemaGestion.Observer.OperationPublisher;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;

public class OperacionesController implements OperationObserver {

    // ─── Summary cards ───────────────────────────────────────
    @FXML private Label numArriendos;
    @FXML private Label numVentas;
    @FXML private Label numRenovaciones;
    @FXML private Label numCancelaciones;

    // ─── Filters ─────────────────────────────────────────────
    @FXML private TextField               campoBusqueda;
    @FXML private ComboBox<OperationType> filtroTipoOperacion;
    @FXML private ComboBox<String>        filtroEstado;

    // ─── Table ───────────────────────────────────────────────
    @FXML private TableView<BusinessOperation>           tablaOperaciones;
    @FXML private TableColumn<BusinessOperation, String> colId;
    @FXML private TableColumn<BusinessOperation, String> colTipo;
    @FXML private TableColumn<BusinessOperation, String> colInmueble;
    @FXML private TableColumn<BusinessOperation, String> colCliente;
    @FXML private TableColumn<BusinessOperation, String> colAsesor;
    @FXML private TableColumn<BusinessOperation, String> colValor;
    @FXML private TableColumn<BusinessOperation, String> colComision;
    @FXML private TableColumn<BusinessOperation, String> colFecha;
    @FXML private TableColumn<BusinessOperation, String> colEstadoProceso;

    // ─── Buttons ─────────────────────────────────────────────
    @FXML private Button btnVerDetalles;
    @FXML private Button btnEditarEstado;

    // ─── Data ────────────────────────────────────────────────
    private FilteredList<BusinessOperation> filteredList;

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
        cargarDatos();
        configurarSeleccion();

        OperationPublisher.getInstance().subscribe(this);
    }

    private void configurarFiltros() {
        filtroTipoOperacion.getItems().setAll(OperationType.values());
        // Los estados se cargan desde las operaciones existentes para no hardcodear
        AppContext.getInstance().getOperations().forEach(op -> {
            String estado = op.getProcessStatus().toString();
            if (!filtroEstado.getItems().contains(estado)) {
                filtroEstado.getItems().add(estado);
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    // OperationObserver
    // ─────────────────────────────────────────────────────────

    @Override
    public void onOperationEvent(OperationEvent event) {
        Platform.runLater(() -> {
            if (event.getEventType() == OperationEvent.EventType.OPERATION_CREATED) {
                AppContext.getInstance().getOperations().add(event.getOperation());
                // Añadir el estado al combo si es nuevo
                String estado = event.getOperation().getProcessStatus().toString();
                if (!filtroEstado.getItems().contains(estado)) {
                    filtroEstado.getItems().add(estado);
                }
            }
            actualizarContadores();
        });
    }

    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnas() {
        colId.setCellValueFactory(d ->
                d.getValue().identifierProperty());
        colTipo.setCellValueFactory(d ->
                d.getValue().operationTypeProperty().asString());
        colInmueble.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getRelatedProperty() != null
                        ? d.getValue().getRelatedProperty().getCode() : "—"));
        colCliente.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getClient() != null
                        ? d.getValue().getClient().getName() : "—"));
        colAsesor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getAdvisor() != null
                        ? d.getValue().getAdvisor().getName() : "—"));
        colValor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getAgreedValueFormatted()));
        colComision.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCommissionFormatted()));
        colFecha.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDateFormatted()));
        colEstadoProceso.setCellValueFactory(d ->
                d.getValue().processStatusProperty().asString());
    }

    private void cargarDatos() {
        filteredList = new FilteredList<>(
                AppContext.getInstance().getOperations(), op -> true);
        tablaOperaciones.setItems(filteredList);
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
                case RENTAL               -> arriendos++;
                case SALE                 -> ventas++;
                case LEASE_RENEWAL        -> renovaciones++;
                case BUSINESS_CANCELLATION -> cancelaciones++;
            }
        }
        numArriendos.setText(String.valueOf(arriendos));
        numVentas.setText(String.valueOf(ventas));
        numRenovaciones.setText(String.valueOf(renovaciones));
        numCancelaciones.setText(String.valueOf(cancelaciones));
    }

    // ─────────────────────────────────────────────────────────
    // Filters
    // ─────────────────────────────────────────────────────────

    @FXML
    public void filtrarTabla() {
        String texto       = campoBusqueda.getText().toLowerCase();
        OperationType tipo = filtroTipoOperacion.getValue();
        String estado      = filtroEstado.getValue();

        filteredList.setPredicate(op -> {
            boolean matchTexto = texto.isEmpty()
                    || op.getIdentifier().toLowerCase().contains(texto)
                    || (op.getClient() != null
                        && op.getClient().getName().toLowerCase().contains(texto))
                    || (op.getRelatedProperty() != null
                        && op.getRelatedProperty().getCode().toLowerCase().contains(texto));

            boolean matchTipo = tipo == null
                    || op.getOperationType() == tipo;

            boolean matchEstado = estado == null
                    || op.getProcessStatus().toString().equals(estado);

            return matchTexto && matchTipo && matchEstado;
        });
    }

    @FXML
    public void limpiarFiltros() {
        campoBusqueda.clear();
        filtroTipoOperacion.setValue(null);
        filtroEstado.setValue(null);
        filteredList.setPredicate(op -> true);
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
                    
                    AppContext.getInstance().saveAll();
                    
                } catch (RuntimeException e) {
                    mostrarError(e.getMessage());
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────
    private void mostrarInfo(String msg)  {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}