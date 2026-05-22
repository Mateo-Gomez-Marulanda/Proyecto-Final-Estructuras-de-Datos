package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import proyectofinal.SistemaGestion.Alertas.Alert;
import proyectofinal.SistemaGestion.Alertas.AlertStatus;
import proyectofinal.SistemaGestion.Alertas.AlertType;
import proyectofinal.SistemaGestion.Alertas.AlertEngine; // Importamos el motor
import proyectofinal.EstructurasDeDatos.Colas.Queue;     // Estructura de datos propia

public class AlertasController {

    // ─── Summary cards ───────────────────────────────────────
    @FXML private Label numPendientes;
    @FXML private Label numRevisadas;
    @FXML private Label numDescartadas;

    // ─── Pending alerts tab ──────────────────────────────────
    @FXML private TableView<Alert>             tablaPendientes;
    @FXML private TableColumn<Alert, String>   colPendId;
    @FXML private TableColumn<Alert, String>   colPendTipo;
    @FXML private TableColumn<Alert, String>   colPendDescripcion;
    @FXML private TableColumn<Alert, String>   colPendEntidad;
    @FXML private TableColumn<Alert, String>   colPendCodigo;
    @FXML private TableColumn<Alert, String>   colPendFecha;

    @FXML private Button btnRevisar;
    @FXML private Button btnDescartar;

    // ─── History tab ─────────────────────────────────────────
    @FXML private TableView<Alert>             tablaHistorial;
    @FXML private TableColumn<Alert, String>   colHistId;
    @FXML private TableColumn<Alert, String>   colHistTipo;
    @FXML private TableColumn<Alert, String>   colHistDescripcion;
    @FXML private TableColumn<Alert, String>   colHistEstado;
    @FXML private TableColumn<Alert, String>   colHistGenerada;
    @FXML private TableColumn<Alert, String>   colHistGestionada;
    @FXML private TableColumn<Alert, String>   colHistObservaciones;

    @FXML private ComboBox<AlertStatus> filtroEstadoHistorial;
    @FXML private ComboBox<AlertType>   filtroTipoHistorial;

    private ObservableList<Alert> pendingSnapshot;
    private ObservableList<Alert> historyList;
    private FilteredList<Alert>   filteredHistory;

    @FXML
    public void initialize() {
        configurarColumnasPendientes();
        configurarColumnasHistorial();
        configurarFiltros();
        configurarSeleccion();
        refrescarTodo();
    }

    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnasPendientes() {
        colPendId.setCellValueFactory(d ->          d.getValue().idProperty());
        colPendTipo.setCellValueFactory(d ->         d.getValue().alertTypeProperty().asString());
        colPendDescripcion.setCellValueFactory(d ->  d.getValue().descriptionProperty());
        colPendEntidad.setCellValueFactory(d ->      d.getValue().relatedEntityTypeProperty());
        colPendCodigo.setCellValueFactory(d ->       d.getValue().relatedEntityCodeProperty());
        colPendFecha.setCellValueFactory(d ->        new SimpleStringProperty(d.getValue().getGeneratedAtFormatted()));
    }

    private void configurarColumnasHistorial() {
        colHistId.setCellValueFactory(d ->           d.getValue().idProperty());
        colHistTipo.setCellValueFactory(d ->          d.getValue().alertTypeProperty().asString());
        colHistDescripcion.setCellValueFactory(d ->   d.getValue().descriptionProperty());
        colHistEstado.setCellValueFactory(d ->        d.getValue().alertStatusProperty().asString());
        colHistGenerada.setCellValueFactory(d ->      new SimpleStringProperty(d.getValue().getGeneratedAtFormatted()));
        colHistGestionada.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getResolvedAtFormatted()));
        colHistObservaciones.setCellValueFactory(d -> d.getValue().observationsProperty());
    }

    private void configurarFiltros() {
        filtroEstadoHistorial.getItems().setAll(AlertStatus.REVIEWED, AlertStatus.DISMISSED);
        filtroTipoHistorial.getItems().setAll(AlertType.values());
    }

    private void configurarSeleccion() {
        tablaPendientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    boolean hay = selected != null;
                    btnRevisar.setDisable(!hay);
                    btnDescartar.setDisable(!hay);
                });
    }

    // ─────────────────────────────────────────────────────────
    // Data loading
    // ─────────────────────────────────────────────────────────

    private void refrescarTodo() {
        refrescarPendientes();
        refrescarHistorial();
        actualizarContadores();
    }

    private void refrescarPendientes() {
        pendingSnapshot = FXCollections.observableArrayList();
        var queue = AppContext.getInstance().getPendingAlerts();
        for (Alert a : queue) {
            pendingSnapshot.add(a);
        }
        tablaPendientes.setItems(pendingSnapshot);
    }

    private void refrescarHistorial() {
        historyList     = FXCollections.observableArrayList();
        filteredHistory = new FilteredList<>(historyList, a -> true);
        for (Alert a : AppContext.getInstance().getAlertHistory()) {
            historyList.add(a);
        }
        tablaHistorial.setItems(filteredHistory);
    }

    private void actualizarContadores() {
        int revisadas = 0, descartadas = 0;
        for (Alert a : AppContext.getInstance().getAlertHistory()) {
            if (a.getAlertStatus() == AlertStatus.REVIEWED)  revisadas++;
            if (a.getAlertStatus() == AlertStatus.DISMISSED) descartadas++;
        }
        int pendientes = AppContext.getInstance().getPendingAlerts().size();
        numPendientes.setText(String.valueOf(pendientes));
        numRevisadas.setText(String.valueOf(revisadas));
        numDescartadas.setText(String.valueOf(descartadas));
    }

    // ─────────────────────────────────────────────────────────
    // Alert actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void marcarAlertaRevisada() {
        Alert selected = tablaPendientes.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Marcar como revisada");
        dialog.setHeaderText("Observaciones (opcional):");
        dialog.showAndWait().ifPresent(obs -> {
            selected.markAsReviewed(obs.isEmpty() ? "Sin observaciones" : obs);
            // EFECTO COLATERAL SOLUCIONADO: Eliminar de la cola de pendientes primero
            removerDeColaPendientes(selected);
            moverAlHistorial(selected);
        });
    }

    @FXML
    public void descartarAlerta() {
        Alert selected = tablaPendientes.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Descartar alerta");
        dialog.setHeaderText("Motivo del descarte:");
        dialog.showAndWait().ifPresent(motivo -> {
            selected.markAsDismissed(motivo.isEmpty() ? "Sin motivo" : motivo);
            // EFECTO COLATERAL SOLUCIONADO: Eliminar de la cola de pendientes primero
            removerDeColaPendientes(selected);
            moverAlHistorial(selected);
        });
    }

    @FXML
    public void procesarSiguienteAlerta() {
        var queue = AppContext.getInstance().getPendingAlerts();
        if (queue.isEmpty()) { 
            mostrarInfo("No hay alertas pendientes."); 
            return; 
        }
        // Atendemos de forma nativa el frente de la estructura FIFO
        Alert next = queue.dequeue();
        
        mostrarInfo("Procesando la alerta más antigua en cola:\n" 
                + "ID: " + next.getId() + "\n"
                + "Descripción: " + next.getDescription() + "\n"
                + "Entidad: " + next.getRelatedEntityType() + " — " + next.getRelatedEntityCode());
        
        // Al ser procesada directamente mediante descolamiento rápido, pasa al historial como REVISADA por defecto
        next.markAsReviewed("Procesada secuencialmente por el asesor.");
        AppContext.getInstance().getAlertHistory().add(next);
        
        refrescarTodo();
    }

    @FXML
    public void generarAlertasAutomaticas() {
        // Obtenemos el tamaño de la cola antes de ejecutar el análisis matemático del motor
        int inicial = AppContext.getInstance().getPendingAlerts().size();
        
        // Delegamos TODAS las reglas de negocio (Contratos, Inmuebles, Clientes, Visitas) al motor centralizado
        AlertEngine.checkAndGenerateAlerts();
        
        int finalAlerts = AppContext.getInstance().getPendingAlerts().size();
        int creadas = finalAlerts - inicial;

        if (creadas > 0) {
            mostrarInfo("¡Análisis completado! Se detectaron y agregaron " + creadas + " alertas nuevas a la cola.");
        } else {
            mostrarInfo("El sistema se encuentra al día. No se detectaron situaciones de riesgo urgentes.");
        }
        refrescarTodo();
    }

    // ─────────────────────────────────────────────────────────
    // Filters (history tab)
    // ─────────────────────────────────────────────────────────

    @FXML
    public void filtrarHistorial() {
        AlertStatus estado = filtroEstadoHistorial.getValue();
        AlertType tipo     = filtroTipoHistorial.getValue();

        filteredHistory.setPredicate(a -> {
            boolean matchEstado = estado == null || a.getAlertStatus() == estado;
            boolean matchTipo   = tipo == null   || a.getAlertType() == tipo;
            return matchEstado && matchTipo;
        });
    }

    @FXML
    public void limpiarFiltros() {
        filtroEstadoHistorial.setValue(null);
        filtroTipoHistorial.setValue(null);
        filteredHistory.setPredicate(a -> true);
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────

    private void removerDeColaPendientes(Alert target) {
        Queue<Alert> queue = AppContext.getInstance().getPendingAlerts();
        Queue<Alert> aux = new Queue<>();

        // Ciclo no destructivo para filtrar el elemento seleccionado usando una cola auxiliar
        while (!queue.isEmpty()) {
            Alert actual = queue.dequeue();
            if (!actual.getId().equals(target.getId())) {
                aux.enqueue(actual);
            }
        }
        // Reconstruimos la cola original sin la alerta procesada
        while (!aux.isEmpty()) {
            queue.enqueue(aux.dequeue());
        }
    }

    private void moverAlHistorial(Alert alerta) {
        AppContext.getInstance().getAlertHistory().add(alerta);
        refrescarTodo();
    }

    private void mostrarInfo(String msg) {
        new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION, msg, ButtonType.OK
        ).showAndWait();
    }
}