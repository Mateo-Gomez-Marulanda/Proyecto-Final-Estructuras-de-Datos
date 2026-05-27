package proyectofinal.controllers;

import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import proyectofinal.SistemaGestion.Observer.OperationEvent;
import proyectofinal.SistemaGestion.Observer.OperationObserver;
import proyectofinal.SistemaGestion.Observer.OperationPublisher;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;
import proyectofinal.SistemaGestion.OperacionDeNegocio.ProcessStatus;

public class OperacionesController implements OperationObserver {

    // ─── Summary cards ───────────────────────────────────────
    @FXML
    private Label numArriendos;
    @FXML
    private Label numVentas;
    @FXML
    private Label numRenovaciones;
    @FXML
    private Label numCancelaciones;

    // ─── Filters ─────────────────────────────────────────────
    @FXML
    private TextField campoBusqueda;
    @FXML
    private ComboBox<OperationType> filtroTipoOperacion;
    @FXML
    private ComboBox<String> filtroEstado;

    // ─── Table ───────────────────────────────────────────────
    @FXML
    private TableView<BusinessOperation> tablaOperaciones;
    @FXML
    private TableColumn<BusinessOperation, String> colId;
    @FXML
    private TableColumn<BusinessOperation, String> colTipo;
    @FXML
    private TableColumn<BusinessOperation, String> colInmueble;
    @FXML
    private TableColumn<BusinessOperation, String> colCliente;
    @FXML
    private TableColumn<BusinessOperation, String> colAsesor;
    @FXML
    private TableColumn<BusinessOperation, String> colValor;
    @FXML
    private TableColumn<BusinessOperation, String> colComision;
    @FXML
    private TableColumn<BusinessOperation, String> colFecha;
    @FXML
    private TableColumn<BusinessOperation, String> colEstadoProceso;

    // ─── Buttons ─────────────────────────────────────────────
    @FXML
    private Button btnVerDetalles;
    @FXML
    private Button btnEditarEstado;
    @FXML
    private Button btnGenerarContrato;

    // ─── Data ────────────────────────────────────────────────
    private ObservableList<BusinessOperation> masterObservableList;
    private FilteredList<BusinessOperation> filteredList;

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatos();
        configurarFiltros();
        configurarSeleccion();

        OperationPublisher.getInstance().subscribe(this);
    }

    private void configurarFiltros() {
        filtroTipoOperacion.getItems().setAll(OperationType.values());

        filtroEstado.getItems().clear();
        for (BusinessOperation op : masterObservableList) {
            String estado = op.getProcessStatus().toString();
            if (!filtroEstado.getItems().contains(estado)) {
                filtroEstado.getItems().add(estado);
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // OperationObserver
    // ─────────────────────────────────────────────────────────

    @Override
    public void onOperationEvent(OperationEvent event) {
        Platform.runLater(() -> {
            if (event.getEventType() == OperationEvent.EventType.OPERATION_CREATED) {
                BusinessOperation nuevaOp = event.getOperation();

                // Si no hay lista se agrega
                if (!masterObservableList.contains(nuevaOp)) {
                    masterObservableList.add(nuevaOp);
                }

                // escanea la lista interana de AppContext
                if (filteredList != null) {
                    filteredList.setPredicate(op -> true); // Resetea el filtro para incluir lo nuevo
                }

                // Asegurar que el nuevo estado aparezca en el ComboBox de filtros
                String estado = nuevaOp.getProcessStatus().toString();
                if (!filtroEstado.getItems().contains(estado)) {
                    filtroEstado.getItems().add(estado);
                }
            }

            // Refrescar componentes visuales y contadores de las tarjetas
            tablaOperaciones.refresh();
            actualizarContadores();
        });
    }
    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnas() {
        colId.setCellValueFactory(d -> d.getValue().identifierProperty());
        colTipo.setCellValueFactory(d -> d.getValue().operationTypeProperty().asString());
        colInmueble.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRelatedProperty() != null ? d.getValue().getRelatedProperty().getCode() : "—"));
        colCliente.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getClient() != null ? d.getValue().getClient().getName() : "—"));
        colAsesor.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getAdvisor() != null ? d.getValue().getAdvisor().getName() : "—"));
        colValor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAgreedValueFormatted()));
        colComision.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCommissionFormatted()));
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDateFormatted()));
        colEstadoProceso.setCellValueFactory(d -> d.getValue().processStatusProperty().asString());
    }

    private void cargarDatos() {
        // Vinculamos de manera directa el master list a la lista observable reactiva de
        // tu AppContext
        masterObservableList = AppContext.getInstance().getOperations();

        filteredList = new FilteredList<>(masterObservableList, op -> true);
        tablaOperaciones.setItems(filteredList);
        actualizarContadores();
    }

    private void configurarSeleccion() {
        btnVerDetalles.setDisable(true);
        btnEditarEstado.setDisable(true);
        btnGenerarContrato.setDisable(true);

        tablaOperaciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected == null) {
                        btnVerDetalles.setDisable(true);
                        btnEditarEstado.setDisable(true);
                        btnGenerarContrato.setDisable(true);
                    } else {
                        btnVerDetalles.setDisable(false);

                        // Si ya está completada o cancelada, se bloquean las acciones de cambio
                        boolean finalizada = selected.getProcessStatus() == ProcessStatus.COMPLETED
                                || selected.getProcessStatus() == ProcessStatus.CANCELLED;

                        btnEditarEstado.setDisable(finalizada);

                        // El botón de contrato solo se habilita si está lista para
                        // firmar
                        // o en proceso de cierre (PENDING_SIGNATURE o el estado inicial enviado por el
                        // cliente)
                        btnGenerarContrato.setDisable(finalizada);
                    }
                });
    }

    private void actualizarContadores() {
        int arriendos = 0, ventas = 0, renovaciones = 0, cancelaciones = 0;
        for (BusinessOperation op : masterObservableList) {
            if (op.getOperationType() == null)
                continue;
            switch (op.getOperationType()) {
                case RENTAL -> arriendos++;
                case SALE -> ventas++;
                case LEASE_RENEWAL -> renovaciones++;
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
        String texto = campoBusqueda.getText().toLowerCase();
        OperationType tipo = filtroTipoOperacion.getValue();
        String estado = filtroEstado.getValue();

        filteredList.setPredicate(op -> {
            boolean matchTexto = texto.isEmpty()
                    || op.getIdentifier().toLowerCase().contains(texto)
                    || (op.getClient() != null && op.getClient().getName().toLowerCase().contains(texto))
                    || (op.getRelatedProperty() != null
                            && op.getRelatedProperty().getCode().toLowerCase().contains(texto));

            boolean matchTipo = tipo == null || op.getOperationType() == tipo;
            boolean matchEstado = estado == null || op.getProcessStatus().toString().equals(estado);

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
        if (op == null)
            return;
        mostrarInfo(op.toString());
    }

    /**
     * Modifica el estado intermedio de la operación.
     * PROTECCIÓN: Si el siguiente estado es COMPLETED, este método detiene el flujo
     * y obliga a usar el botón de Generar Contrato.
     */
    @FXML
    public void editarEstadoOperacion() {
        BusinessOperation op = tablaOperaciones.getSelectionModel().getSelectedItem();
        if (op == null)
            return;

        if (op.getProcessStatus() == ProcessStatus.COMPLETED || op.getProcessStatus() == ProcessStatus.CANCELLED) {
            mostrarInfo("No se puede modificar una operación en estado finalizado: " + op.getProcessStatus());
            return;
        }

        // CONTROL DE FLUJO DIRECTO:
        // Si el estado actual es PENDING_SIGNATURE, avanzar significa pasar a
        // COMPLETED.
        // Detenemos al Admin para que use el flujo legal del contrato.
        if (op.getProcessStatus() == ProcessStatus.PENDING_SIGNATURE) {
            mostrarInfo(
                    "Para pasar esta operación a COMPLETADA debe generar el soporte legal.\nPor favor use el botón 'Generar Contrato'.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Avanzar el estado de la operación " + op.getIdentifier() + " de forma manual?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try {
                    // Avanza el estado intermedio de manera segura (ej: IN_PROGRESS ->
                    // PENDING_SIGNATURE)
                    op.advanceStatus();

                    tablaOperaciones.refresh();
                    actualizarContadores();

                    // Sincronización en caliente del disco (.txt)
                    AppContext.getInstance().saveAll();

                } catch (RuntimeException e) {
                    mostrarError(e.getMessage());
                }
            }
        });
    }

    /**
     * 📄 ACCIÓN NUEVA: Abre el modal de formalización de contratos.
     * Es el único disparador autorizado para cambiar el estado a COMPLETED.
     */
    @FXML
    public void abrirFormularioContrato() {
        BusinessOperation operacionSeleccionada = tablaOperaciones.getSelectionModel().getSelectedItem();

        if (operacionSeleccionada == null) {
            mostrarError("Por favor, seleccione una operación transaccional de la tabla.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectofinal/views/CrearContratoModal.fxml"));
            Parent root = loader.load();

            // Inyectamos la operación seleccionada al controlador de la ventana emergente
            CrearContratoController modalController = loader.getController();
            modalController.setOperacionBase(operacionSeleccionada);

            Stage stage = new Stage();
            stage.setTitle("Formalizar Contrato - Op: " + operacionSeleccionada.getIdentifier());
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la interacción con la ventana de atrás
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Al retornar del modal, refrescamos la UI de control
            tablaOperaciones.refresh();
            actualizarContadores();

        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista del contrato: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}