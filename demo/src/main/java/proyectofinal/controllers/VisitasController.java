package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitManager;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;

public class VisitasController {

    // ─── Summary labels ──────────────────────────────────────
    @FXML private Label lblTotalCola;
    @FXML private Label lblProximaVisita;

    // ─── Queue tab ───────────────────────────────────────────
    @FXML private TableView<VisitRequest>             tablaCola;
    @FXML private TableColumn<VisitRequest, String>   colColaPosicion;
    @FXML private TableColumn<VisitRequest, String>   colColaCodigo;
    @FXML private TableColumn<VisitRequest, String>   colColaCliente;
    @FXML private TableColumn<VisitRequest, String>   colColaInmueble;
    @FXML private TableColumn<VisitRequest, String>   colColaFecha;
    @FXML private TableColumn<VisitRequest, String>   colColaHora;
    @FXML private TableColumn<VisitRequest, String>   colColaAsesor;

    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;
    @FXML private Button btnReprogramar;

    // ─── All visits tab ──────────────────────────────────────
    @FXML private TableView<VisitRequest>             tablaTodasVisitas;
    @FXML private TableColumn<VisitRequest, String>   colTCodigo;
    @FXML private TableColumn<VisitRequest, String>   colTCliente;
    @FXML private TableColumn<VisitRequest, String>   colTInmueble;
    @FXML private TableColumn<VisitRequest, String>   colTFecha;
    @FXML private TableColumn<VisitRequest, String>   colTHora;
    @FXML private TableColumn<VisitRequest, String>   colTAsesor;
    @FXML private TableColumn<VisitRequest, String>   colTEstado;

    @FXML private TextField      campoBusquedaTodas;
    @FXML private ComboBox<String> filtroEstadoVisita;

    private ObservableList<VisitRequest> historyList;
    private FilteredList<VisitRequest>   filteredHistory;

    // Snapshot of the pending queue for display (queue itself is in VisitManager)
    private ObservableList<VisitRequest> colaSnapshot;

    @FXML
    public void initialize() {
        configurarColumnasCola();
        configurarColumnasTodas();
        configurarFiltros();
        refrescarTodo();
    }

    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnasCol() {
        // colColaPosicion uses index — handled in refresh
        colColaCliente.setCellValueFactory(d ->  new SimpleStringProperty(d.getValue().getClient().getName()));
        colColaInmueble.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProperty().getCode()));
        colColaFecha.setCellValueFactory(d ->    new SimpleStringProperty(
                d.getValue().getDateTime().toLocalDate().toString()));
        colColaHora.setCellValueFactory(d ->     new SimpleStringProperty(
                d.getValue().getDateTime().toLocalTime().toString().substring(0, 5)));
    }

    private void configurarColumnasQueue() {
        colColaCliente.setCellValueFactory(d ->  new SimpleStringProperty(d.getValue().getClient().getName()));
        colColaInmueble.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProperty().getCode()));
        colColaFecha.setCellValueFactory(d ->    new SimpleStringProperty(
                d.getValue().getDateTime().toLocalDate().toString()));
        colColaHora.setCellValueFactory(d ->     new SimpleStringProperty(
                d.getValue().getDateTime().toLocalTime().toString().substring(0, 5)));
    }

    private void configurarColumnasCola() {
        colColaPosicion.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(colaSnapshot != null ? colaSnapshot.indexOf(d.getValue()) + 1 : 0)));
        configurarColumnasQueue();
    }

    private void configurarColumnasTodas() {
        colTCliente.setCellValueFactory(d ->  new SimpleStringProperty(d.getValue().getClient().getName()));
        colTInmueble.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProperty().getCode()));
        colTFecha.setCellValueFactory(d ->    new SimpleStringProperty(
                d.getValue().getDateTime().toLocalDate().toString()));
        colTHora.setCellValueFactory(d ->     new SimpleStringProperty(
                d.getValue().getDateTime().toLocalTime().toString().substring(0, 5)));
        colTEstado.setCellValueFactory(d ->   new SimpleStringProperty(d.getValue().getStatus()));
    }

    private void configurarFiltros() {
        filtroEstadoVisita.getItems().setAll("PENDING", "CONFIRM", "COMPLETED", "CANCELLED", "RECHEDULED");
    }

    // ─────────────────────────────────────────────────────────
    // Data loading
    // ─────────────────────────────────────────────────────────

    private void refrescarTodo() {
        refrescarCola();
        refrescarHistorial();
        actualizarContadores();
    }

    private void refrescarCola() {
        // Build a display-only snapshot of pending visits from history with PENDING status
        colaSnapshot = FXCollections.observableArrayList();
        VisitManager vm = AppContext.getInstance().getVisitManager();

        for (VisitRequest v : vm.getVisitHistory()) {
            if (v.getStatus().equals("PENDING") || v.getStatus().equals("CONFIRM")) {
                colaSnapshot.add(v);
            }
        }
        tablaCola.setItems(colaSnapshot);
    }

    private void refrescarHistorial() {
        historyList     = FXCollections.observableArrayList();
        filteredHistory = new FilteredList<>(historyList, v -> true);

        for (VisitRequest v : AppContext.getInstance().getVisitManager().getVisitHistory()) {
            historyList.add(v);
        }
        tablaTodasVisitas.setItems(filteredHistory);
    }

    private void actualizarContadores() {
        int total = AppContext.getInstance().getVisitManager().getTotalPending();
        lblTotalCola.setText(String.valueOf(total));

        if (!colaSnapshot.isEmpty()) {
            VisitRequest next = colaSnapshot.get(0);
            lblProximaVisita.setText(next.getClient().getName()
                    + " — " + next.getProperty().getCode());
        } else {
            lblProximaVisita.setText("Sin visitas pendientes");
        }
    }

    // ─────────────────────────────────────────────────────────
    // Queue actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void confirmarSiguienteVisita() {
        VisitRequest next = AppContext.getInstance().getVisitManager().getNextVisitToAttend();
        if (next == null) { mostrarInfo("No hay visitas en cola."); return; }
        AppContext.getInstance().getVisitManager().confirmVisit(next);
        mostrarInfo("Visita confirmada para: " + next.getClient().getName());
        refrescarTodo();
    }

    @FXML
    public void cancelarSiguienteVisita() {
        VisitRequest next = AppContext.getInstance().getVisitManager().getNextVisitToAttend();
        if (next == null) { mostrarInfo("No hay visitas en cola."); return; }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar visita");
        dialog.setHeaderText("Motivo de cancelación:");
        dialog.showAndWait().ifPresent(reason -> {
            AppContext.getInstance().getVisitManager().cancelVisit(next, reason);
            mostrarInfo("Visita cancelada.");
            refrescarTodo();
        });
    }

    @FXML
    public void reprogramarSiguienteVisita() {
        mostrarInfo("Reprogramación: selecciona nueva fecha desde el formulario (próximamente).");
    }

    @FXML
    public void abrirFormularioAgendar() {
        mostrarInfo("Formulario de agendamiento próximamente.");
    }

    // ─────────────────────────────────────────────────────────
    // Filters (all visits tab)
    // ─────────────────────────────────────────────────────────

    @FXML
    public void filtrarTodasVisitas() {
        String texto  = campoBusquedaTodas.getText().toLowerCase();
        String estado = filtroEstadoVisita.getValue();

        filteredHistory.setPredicate(v -> {
            boolean matchTexto = texto.isEmpty()
                    || v.getClient().getName().toLowerCase().contains(texto)
                    || v.getProperty().getCode().toLowerCase().contains(texto);
            boolean matchEstado = estado == null || v.getStatus().equals(estado);
            return matchTexto && matchEstado;
        });
    }

    @FXML
    public void limpiarFiltrosVisitas() {
        campoBusquedaTodas.clear();
        filtroEstadoVisita.setValue(null);
        filteredHistory.setPredicate(v -> true);
    }

    // ─────────────────────────────────────────────────────────
    private void mostrarInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
}
