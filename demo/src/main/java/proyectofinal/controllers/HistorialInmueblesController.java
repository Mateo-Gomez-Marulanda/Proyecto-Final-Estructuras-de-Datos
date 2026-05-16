package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import proyectofinal.SistemaGestion.GestionInmuebles.PropertyChange;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HistorialInmueblesController {

    @FXML private TableView<PropertyChange>               tablaHistorial;
    @FXML private TableColumn<PropertyChange, String>     colFecha;
    @FXML private TableColumn<PropertyChange, String>     colTipo;
    @FXML private TableColumn<PropertyChange, String>     colPropiedad;
    @FXML private TableColumn<PropertyChange, String>     colCampo;
    @FXML private TableColumn<PropertyChange, String>     colAnterior;
    @FXML private TableColumn<PropertyChange, String>     colNuevo;
    @FXML private TableColumn<PropertyChange, String>     colResponsable;
    @FXML private Label                                   labelTotal;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarHistorial();
    }

    private void configurarColumnas() {
        colFecha.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDateTime().format(FMT)));

        colTipo.setCellValueFactory(d ->
                new SimpleStringProperty(tipoLegible(d.getValue())));

        colPropiedad.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getPropertyCode()));

        colCampo.setCellValueFactory(d -> {
            PropertyChange c = d.getValue();
            // Las acciones admin llevan descripción; las modificaciones llevan campo
            String texto = c.getDescription() != null
                    ? c.getDescription()
                    : campoLegible(c.getModifiedField());
            return new SimpleStringProperty(texto);
        });

        colAnterior.setCellValueFactory(d -> {
            Object v = d.getValue().getPreviousValue();
            return new SimpleStringProperty(v != null ? formatearValor(v) : "—");
        });

        colNuevo.setCellValueFactory(d -> {
            Object v = d.getValue().getNewValue();
            return new SimpleStringProperty(v != null ? formatearValor(v) : "—");
        });

        colResponsable.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getResponsiblePerson()));
    }

    private void cargarHistorial() {
        var pm = AppContext.getInstance().getPropertyManager();
        List<PropertyChange> todos = new ArrayList<>();

        // Recorrer los tres stacks (todos implementan Iterable via Stack)
        for (PropertyChange c : pm.getModificationHistory())  todos.add(c);
        for (PropertyChange c : pm.getStatusHistory())        todos.add(c);
        for (PropertyChange c : pm.getAdminActionsHistory())  todos.add(c);

        // Ordenar de más reciente a más antiguo
        todos.sort(Comparator.comparing(PropertyChange::getDateTime).reversed());

        ObservableList<PropertyChange> items = FXCollections.observableArrayList(todos);
        tablaHistorial.setItems(items);

        labelTotal.setText(todos.size() + " registro" + (todos.size() != 1 ? "s" : ""));
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void cerrar() {
        ((Stage) tablaHistorial.getScene().getWindow()).close();
    }

    // ─────────────────────────────────────────────────────────
    // Helpers de formato
    // ─────────────────────────────────────────────────────────

    private String tipoLegible(PropertyChange c) {
        return switch (c.getChangeType()) {
            case FIELD_MODIFICATION    -> "📝 Modificación";
            case STATUS_CHANGE         -> "🔄 Estado";
            case ADMINISTRATIVE_ACTION -> "🛠  Acción admin";
        };
    }

    private String campoLegible(String field) {
        if (field == null) return "—";
        return switch (field) {
            case "price"          -> "Precio";
            case "area"           -> "Área (m²)";
            case "rooms"          -> "Habitaciones";
            case "propertyStatus" -> "Estado";
            case "isAvailable"    -> "Disponibilidad";
            default               -> field;
        };
    }

    private String formatearValor(Object v) {
        if (v instanceof Double d) return String.format("%,.2f", d);
        return v.toString();
    }
}