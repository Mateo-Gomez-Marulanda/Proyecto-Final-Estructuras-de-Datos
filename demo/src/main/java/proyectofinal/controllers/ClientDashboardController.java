package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;

public class ClientDashboardController {

    @FXML private Label lblBienvenida;
    @FXML private Label numFavoritos;
    @FXML private Label numVisitas;
    @FXML private Label numVisitadas;

    @FXML private TableView<Property>             tablaRecomendaciones;
    @FXML private TableColumn<Property, String>   colRecCodigo;
    @FXML private TableColumn<Property, String>   colRecDireccion;
    @FXML private TableColumn<Property, String>   colRecCiudad;
    @FXML private TableColumn<Property, String>   colRecTipo;
    @FXML private TableColumn<Property, String>   colRecPrecio;
    @FXML private TableColumn<Property, String>   colRecHab;
    @FXML private TableColumn<Property, String>   colRecArea;

    @FXML private Button btnGuardarFavorito;
    @FXML private Button btnAgendarVisita;

    private ObservableList<Property> recomendaciones;

    @FXML
    public void initialize() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client != null) lblBienvenida.setText("¡Bienvenido, " + client.getName() + "!");

        configurarColumnas();
        cargarDatos();
        configurarSeleccion();
    }

    private void configurarColumnas() {
        colRecCodigo.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getCode()));
        colRecDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colRecCiudad.setCellValueFactory(d ->    new SimpleStringProperty(d.getValue().getCity()));
        colRecTipo.setCellValueFactory(d ->      new SimpleStringProperty(d.getValue().getType().toString()));
        colRecPrecio.setCellValueFactory(d ->    new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getPrice())));
        colRecHab.setCellValueFactory(d ->       new SimpleStringProperty(
                String.valueOf(d.getValue().getRooms())));
        colRecArea.setCellValueFactory(d ->      new SimpleStringProperty(
                d.getValue().getArea() + " m²"));
    }

    private void cargarDatos() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client == null) return;

        numFavoritos.setText(String.valueOf(client.getFavoriteProperties().size()));
        numVisitadas.setText(String.valueOf(client.getVisitedPropertiesHistory().size()));

        // Count active visits from history
        int visitas = 0;
        for (var v : AppContext.getInstance().getVisitManager().getVisitHistory()) {
            if (v.getClient().getId().equals(client.getId())
                    && (v.getStatus().equals("PENDING") || v.getStatus().equals("CONFIRM"))) {
                visitas++;
            }
        }
        numVisitas.setText(String.valueOf(visitas));

        // Load recommendations
        recomendaciones = FXCollections.observableArrayList();
        var recs = AppContext.getInstance().getClientManager()
                .getRecommendations(AppContext.getInstance().getPropertyManager().getProperties());
        for (Property p : recs) recomendaciones.add(p);
        tablaRecomendaciones.setItems(recomendaciones);
    }

    private void configurarSeleccion() {
        tablaRecomendaciones.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> {
                    boolean hay = sel != null;
                    btnGuardarFavorito.setDisable(!hay);
                    btnAgendarVisita.setDisable(!hay);
                });
    }

    @FXML
    public void guardarFavorito() {
        Property selected = tablaRecomendaciones.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        AppContext.getInstance().getClientManager().markAsFavorite(selected);
        mostrarInfo("✅ Guardado en favoritos: " + selected.getCode());
    }

    @FXML
    public void agendarVisitaDesdeRecomendacion() {
        mostrarInfo("Para agendar una visita ve al módulo 'Mis visitas'.");
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
