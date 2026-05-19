package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;

public class ClientVisitsController {

    // ─── Activas tab ─────────────────────────────────────────
    @FXML private TableView<VisitRequest>             tablaActivas;
    @FXML private TableColumn<VisitRequest, String>   colACodigo;
    @FXML private TableColumn<VisitRequest, String>   colAInmueble;
    @FXML private TableColumn<VisitRequest, String>   colAFecha;
    @FXML private TableColumn<VisitRequest, String>   colAHora;
    @FXML private TableColumn<VisitRequest, String>   colAEstado;
    @FXML private TableColumn<VisitRequest, String>   colAAsesor;
    @FXML private Button btnCancelar;

    // ─── Historial tab ───────────────────────────────────────
    @FXML private TableView<Property>             tablaHistorial;
    @FXML private TableColumn<Property, String>   colHCodigo;
    @FXML private TableColumn<Property, String>   colHDireccion;
    @FXML private TableColumn<Property, String>   colHCiudad;
    @FXML private TableColumn<Property, String>   colHTipo;
    @FXML private TableColumn<Property, String>   colHPrecio;
    @FXML private Button btnFavoritoDesdeHistorial;

    private ObservableList<VisitRequest> activasList;
    private ObservableList<Property>     historialList;

    @FXML
    public void initialize() {
        configurarColumnasActivas();
        configurarColumnasHistorial();
        cargarDatos();
        configurarSelecciones();
    }

    private void configurarColumnasActivas() {
        colAInmueble.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProperty().getCode() + " — " + d.getValue().getProperty().getAddress()));
        colAFecha.setCellValueFactory(d ->    new SimpleStringProperty(
                d.getValue().getDateTime().toLocalDate().toString()));
        colAHora.setCellValueFactory(d ->     new SimpleStringProperty(
                d.getValue().getDateTime().toLocalTime().toString().substring(0, 5)));
        colAEstado.setCellValueFactory(d ->   new SimpleStringProperty(d.getValue().getStatus()));
    }

    private void configurarColumnasHistorial() {
        colHCodigo.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getCode()));
        colHDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colHCiudad.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getCity()));
        colHTipo.setCellValueFactory(d ->      new SimpleStringProperty(d.getValue().getType().toString()));
        colHPrecio.setCellValueFactory(d ->    new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getPrice())));
    }

    private void cargarDatos() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        activasList   = FXCollections.observableArrayList();
        historialList = FXCollections.observableArrayList();

        if (client == null) return;

        // Active visits (pending or confirmed) for this client
        for (VisitRequest v : AppContext.getInstance().getVisitManager().getVisitHistory()) {
            if (!v.getClient().getId().equals(client.getId())) continue;
            if (v.getStatus().equals("PENDING") || v.getStatus().equals("CONFIRM")) {
                activasList.add(v);
            }
        }

        // Visited properties history
        for (Property p : client.getVisitedPropertiesHistory()) {
            historialList.add(p);
        }

        tablaActivas.setItems(activasList);
        tablaHistorial.setItems(historialList);
    }

    private void configurarSelecciones() {
        tablaActivas.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> btnCancelar.setDisable(sel == null));
        tablaHistorial.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> btnFavoritoDesdeHistorial.setDisable(sel == null));
    }

    @FXML
    public void cancelarVisita() {
        VisitRequest selected = tablaActivas.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Solicitar cancelación");
        dialog.setHeaderText("Motivo de la cancelación:");
        dialog.showAndWait().ifPresent(motivo -> {
            AppContext.getInstance().getVisitManager().cancelVisit(selected, motivo);
            activasList.remove(selected);
            mostrarInfo("Visita cancelada correctamente.");
        });
    }

    @FXML
    public void guardarFavoritoDesdeHistorial() {
        Property selected = tablaHistorial.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        AppContext.getInstance().getClientManager().markAsFavorite(selected);
        mostrarInfo("⭐ Guardado en favoritos: " + selected.getCode());
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
