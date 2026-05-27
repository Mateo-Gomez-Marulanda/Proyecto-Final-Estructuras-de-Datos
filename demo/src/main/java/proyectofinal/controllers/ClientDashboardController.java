package proyectofinal.controllers;

import java.io.IOException;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;

public class ClientDashboardController {

    @FXML
    private Label lblBienvenida;
    @FXML
    private Label numFavoritos;
    @FXML
    private Label numVisitas;
    @FXML
    private Label numVisitadas;

    @FXML
    private TableView<Property> tablaRecomendaciones;
    @FXML
    private TableColumn<Property, String> colRecCodigo;
    @FXML
    private TableColumn<Property, String> colRecDireccion;
    @FXML
    private TableColumn<Property, String> colRecCiudad;
    @FXML
    private TableColumn<Property, String> colRecTipo;
    @FXML
    private TableColumn<Property, String> colRecPrecio;
    @FXML
    private TableColumn<Property, String> colRecHab;
    @FXML
    private TableColumn<Property, String> colRecArea;

    @FXML
    private Button btnGuardarFavorito;
    @FXML
    private Button btnAgendarVisita;

    private ObservableList<Property> recomendaciones;

    @FXML
    public void initialize() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client != null)
            lblBienvenida.setText("¡Bienvenido, " + client.getName() + "!");

        configurarColumnas();
        cargarDatos();
        configurarSeleccion();
    }

    private void configurarColumnas() {
        colRecCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colRecDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colRecCiudad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCity()));
        colRecTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().toString()));
        colRecPrecio.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getPrice())));
        colRecHab.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getRooms())));
        colRecArea.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getArea() + " m²"));
    }

    private void cargarDatos() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client == null)
            return;

        numFavoritos.setText(String.valueOf(client.getFavoriteProperties().size()));
        numVisitadas.setText(String.valueOf(client.getVisitedPropertiesHistory().size()));

        int visitas = 0;
        var visitasActivasYPendientes = AppContext.getInstance().getVisitManager().getAllPendingAndActiveVisits();

        for (int i = 0; i < visitasActivasYPendientes.size(); i++) {
            var v = visitasActivasYPendientes.get(i);

            // Validamos que pertenezca al cliente logueado
            if (v.getClient().getId().equals(client.getId())) {
                // Comparamos usando el Enum directamente, o convirtiéndolo a String de forma
                // segura
                String estado = String.valueOf(v.getVisitStatus());
                if (estado.equalsIgnoreCase("PENDING") || estado.equalsIgnoreCase("CONFIRM")) {
                    visitas++;
                }
            }
        }
        numVisitas.setText(String.valueOf(visitas));

        // Load recommendations
        recomendaciones = FXCollections.observableArrayList();
        var recs = AppContext.getInstance().getClientManager()
                .getRecommendations(AppContext.getInstance().getPropertyManager().getProperties());
        for (Property p : recs)
            recomendaciones.add(p);
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
        if (selected == null)
            return;
        AppContext.getInstance().getClientManager().markAsFavorite(selected);
        mostrarInfo("Guardado en favoritos: " + selected.getCode());
    }

    @FXML
    public void agendarVisitaDesdeRecomendacion() {
        Property propSeleccionada = tablaRecomendaciones.getSelectionModel().getSelectedItem();
        if (propSeleccionada == null) {
            mostrarInfo("Por favor, selecciona un inmueble.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/proyectofinal/views/agendar-visita-cliente.fxml"));
            Parent root = loader.load();

            AgendarVisitaCLIController controller = loader.getController();
            controller.setDatosIniciales(propSeleccionada, AppContext.getInstance().getClientManager().getCurrent());

            Stage dialog = new Stage();
            dialog.setTitle("Agendar Visita");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            cargarDatos(); // Refrescar tabla al cerrar
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
