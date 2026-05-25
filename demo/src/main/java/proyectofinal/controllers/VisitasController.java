package proyectofinal.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private ObservableList<VisitRequest> historyList = FXCollections.observableArrayList();
    private FilteredList<VisitRequest>   filteredHistory;
    private ObservableList<VisitRequest> colaSnapshot = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Inicializar listas primero para evitar fallos de puntero nulo en celfactory
        filteredHistory = new FilteredList<>(historyList, v -> true);
        
        configurarColumnasCola();
        configurarColumnasTodas();
        configurarFiltros();
        
        // Enlazar de forma definitiva los elementos a las tablas
        tablaCola.setItems(colaSnapshot);
        tablaTodasVisitas.setItems(filteredHistory);
        
        refrescarTodo();
    }

    // ─────────────────────────────────────────────────────────
    // Setup
    // ─────────────────────────────────────────────────────────

    private void configurarColumnasCola() {
        // Cambiado para evitar buscar dinámicamente en una lista que cambia en caliente
        colColaPosicion.setCellFactory(col -> new TableCell<VisitRequest, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });
        
        colColaCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode())); 
        colColaCliente.setCellValueFactory(d ->  new SimpleStringProperty(d.getValue().getClient().getName()));
        colColaInmueble.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProperty().getCode()));
        colColaFecha.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getDateTime().toLocalDate().toString()));
        colColaHora.setCellValueFactory(d ->     new SimpleStringProperty(d.getValue().getDateTime().toLocalTime().toString().substring(0, 5)));
        colColaAsesor.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProperty().getResponsibleAdvisor() != null ? d.getValue().getProperty().getResponsibleAdvisor().getName() : "Sin asignar"));
    }

    private void configurarColumnasTodas() {
        colTCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colTCliente.setCellValueFactory(d ->  new SimpleStringProperty(d.getValue().getClient().getName()));
        colTInmueble.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getProperty().getCode()));
        colTFecha.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getDateTime().toLocalDate().toString()));
        colTHora.setCellValueFactory(d ->     new SimpleStringProperty(d.getValue().getDateTime().toLocalTime().toString().substring(0, 5)));
        colTAsesor.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProperty().getResponsibleAdvisor() != null ? d.getValue().getProperty().getResponsibleAdvisor().getName() : "Sin asignar"));
        colTEstado.setCellValueFactory(d ->   new SimpleStringProperty(d.getValue().getStatus()));
    }

    private void configurarFiltros() {
        filtroEstadoVisita.getItems().setAll("PENDIENTE", "CONFIRMADA", "REALIZADA", "CANCELADA", "REPROGRAMADA");
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
        colaSnapshot.clear(); // Limpiar la lista existente, no instanciar una nueva
        VisitManager vm = AppContext.getInstance().getVisitManager();

        for (VisitRequest v : vm.getAllPendingAndActiveVisits()) {
            colaSnapshot.add(v);
        }
    }

    private void refrescarHistorial() {
        historyList.clear(); // Limpiar la lista existente para mantener el binding del filtro
        VisitManager vm = AppContext.getInstance().getVisitManager();

        // 1. Mostrar las procesadas (Confirmadas, Canceladas, Reprogramadas, Realizadas)
        for (VisitRequest v : vm.getVisitHistory()) {
            historyList.add(v);
        }
        // 2. Mostrar las que sigan pendientes de procesamiento
        for (VisitRequest v : vm.getAllPendingAndActiveVisits()) {
            historyList.add(v);
        }
        
        // Volver a aplicar el predicado de filtrado actual
        filtrarTodasVisitas();
    }

    private void actualizarContadores() {
        int total = colaSnapshot.size();
        lblTotalCola.setText(String.valueOf(total));

        if (!colaSnapshot.isEmpty()) {
            VisitRequest next = colaSnapshot.get(0);
            lblProximaVisita.setText(next.getClient().getName() + " — " + next.getProperty().getCode());
        } else {
            lblProximaVisita.setText("Sin visitas pendientes");
        }
    }

    // ─────────────────────────────────────────────────────────
    // Queue actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void confirmarSiguienteVisita() {
        VisitRequest seleccionada = tablaCola.getSelectionModel().getSelectedItem();
        if (seleccionada == null && !colaSnapshot.isEmpty()) {
            seleccionada = colaSnapshot.get(0);
        }

        if (seleccionada == null) { mostrarInfo("No hay visitas para confirmar."); return; }

        AppContext.getInstance().getVisitManager().confirmVisit(seleccionada);
        mostrarInfo("¡Visita confirmada exitosamente! Se ha trasladado al historial.");
        refrescarTodo();
    }

    @FXML
    public void cancelarSiguienteVisita() {
        VisitRequest seleccionada = tablaCola.getSelectionModel().getSelectedItem();
        if (seleccionada == null && !colaSnapshot.isEmpty()) {
            seleccionada = colaSnapshot.get(0);
        }

        if (seleccionada == null) { mostrarInfo("No hay visitas para cancelar."); return; }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar visita");
        dialog.setHeaderText("Motivo de cancelación (Observaciones):");
    
        final VisitRequest finalSelected = seleccionada;
        dialog.showAndWait().ifPresent(reason -> {
            AppContext.getInstance().getVisitManager().cancelVisit(finalSelected, reason);
            mostrarInfo("Visita cancelada y enviada al historial.");
            refrescarTodo();
        });
    }

    @FXML
    public void reprogramarSiguienteVisita() {
        VisitRequest seleccionada = tablaCola.getSelectionModel().getSelectedItem();
        if (seleccionada == null && !colaSnapshot.isEmpty()) {
            seleccionada = colaSnapshot.get(0);
        }

        if (seleccionada == null) { mostrarInfo("No hay visitas en cola para reprogramar."); return; }
 
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Reprogramar visita");
        dialog.setHeaderText("Nueva fecha para: " + seleccionada.getClient().getName()
                + " — " + seleccionada.getProperty().getCode());
 
        ButtonType confirmarBtn = new ButtonType("Reprogramar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmarBtn, ButtonType.CANCEL);
 
        DatePicker picker = new DatePicker(LocalDate.now().plusDays(1));
        Spinner<Integer> hora = new Spinner<>(0, 23, seleccionada.getDateTime().getHour());
        Spinner<Integer> min = new Spinner<>(0, 59, seleccionada.getDateTime().getMinute());
 
        javafx.scene.layout.HBox content = new javafx.scene.layout.HBox(12,
                new Label("Fecha:"), picker,
                new Label("Hora:"), hora,
                new Label("Min:"), min);
        content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        content.setPadding(new javafx.geometry.Insets(16));
        dialog.getDialogPane().setContent(content);
 
        dialog.setResultConverter(bt -> {
            if (bt == confirmarBtn && picker.getValue() != null) {
                return LocalDateTime.of(picker.getValue(),
                        java.time.LocalTime.of(hora.getValue(), min.getValue()));
            }
            return null;
        });
 
        final VisitRequest finalSelected = seleccionada;
        dialog.showAndWait().ifPresent(nuevaFecha -> {
            if (nuevaFecha.isBefore(LocalDateTime.now())) {
                mostrarError("La nueva fecha no puede ser en el pasado.");
                return;
            }
            AppContext.getInstance().getVisitManager().rescheduleVisit(finalSelected, nuevaFecha);
            mostrarInfo("Visita reprogramada correctamente y registrada en el historial.");
            refrescarTodo();
        });
    }

    @FXML
    public void abrirFormularioAgendar() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/agendar-visita.fxml"));
            Parent root = loader.load();
 
            AgendarVisitaController ctrl = loader.getController();
            ctrl.setOnAgendadoExitoso(v -> refrescarTodo());
 
            Stage dialog = new Stage();
            dialog.setTitle("Agendar visita");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(tablaCola.getScene().getWindow());
            dialog.setResizable(false);
 
            Scene scene = new Scene(root);
            if (!tablaCola.getScene().getStylesheets().isEmpty()) {
                scene.getStylesheets().addAll(tablaCola.getScene().getStylesheets());
            }
            dialog.setScene(scene);
            dialog.showAndWait();
            refrescarTodo(); // Forzar refresco al cerrar modal por si acaso
 
        } catch (IOException e) {
            mostrarError("No se pudo abrir el formulario:\n" + e.getMessage());
        }
    }

    @FXML
    public void filtrarTodasVisitas() {
        if (filteredHistory == null) return;
        
        String texto  = campoBusquedaTodas.getText() != null ? campoBusquedaTodas.getText().toLowerCase() : "";
        String estado = filtroEstadoVisita.getValue();

        filteredHistory.setPredicate(v -> {
            boolean matchTexto = texto.isEmpty()
                    || v.getClient().getName().toLowerCase().contains(texto)
                    || v.getProperty().getCode().toLowerCase().contains(texto);
            boolean matchEstado = estado == null || v.getStatus().equalsIgnoreCase(estado);
            return matchTexto && matchEstado;
        });
    }

    @FXML
    public void limpiarFiltrosVisitas() {
        campoBusquedaTodas.clear();
        filtroEstadoVisita.setValue(null);
        if (filteredHistory != null) {
            filteredHistory.setPredicate(v -> true);
        }
    }

    private void mostrarInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void mostrarError(String msg) { new Alert(Alert.AlertType.ERROR,       msg, ButtonType.OK).showAndWait(); }
}