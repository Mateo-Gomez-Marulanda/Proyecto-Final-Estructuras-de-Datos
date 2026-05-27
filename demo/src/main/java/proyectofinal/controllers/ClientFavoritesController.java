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

public class ClientFavoritesController {

    @FXML
    private TableView<Property> tablaFavoritos;
    @FXML
    private TableColumn<Property, String> colCodigo;
    @FXML
    private TableColumn<Property, String> colDireccion;
    @FXML
    private TableColumn<Property, String> colCiudad;
    @FXML
    private TableColumn<Property, String> colTipo;
    @FXML
    private TableColumn<Property, String> colPrecio;
    @FXML
    private TableColumn<Property, String> colHab;
    @FXML
    private TableColumn<Property, String> colDisponible;

    @FXML
    private Label lblTotal;
    @FXML
    private Button btnAgendar;
    @FXML
    private Button btnQuitar;

    private ObservableList<Property> favoritos;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatos();
        configurarSeleccion();
    }

    private void configurarColumnas() {
        colCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colCiudad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCity()));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().toString()));
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty(
                String.format("$%,.0f", d.getValue().getPrice())));
        colHab.setCellValueFactory(d -> new SimpleStringProperty(
                String.valueOf(d.getValue().getRooms())));
        colDisponible.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().isAvailable() ? "Sí" : "No"));
    }

    private void cargarDatos() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        favoritos = FXCollections.observableArrayList();

        if (client != null) {
            for (Property p : client.getFavoriteProperties())
                favoritos.add(p);
        }

        tablaFavoritos.setItems(favoritos);
        lblTotal.setText(favoritos.size() + " propiedad(es)");
    }

    private void configurarSeleccion() {
        tablaFavoritos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, sel) -> {
                    boolean hay = sel != null;
                    btnAgendar.setDisable(!hay);
                    btnQuitar.setDisable(!hay);
                });
    }

    @FXML
    public void agendarVisita() {
        Property propSeleccionada = tablaFavoritos.getSelectionModel().getSelectedItem();
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

    @FXML
    public void quitarFavorito() {
        Property selected = tablaFavoritos.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client == null)
            return;

        client.getFavoriteProperties().remove(selected);
        favoritos.remove(selected);
        lblTotal.setText(favoritos.size() + " propiedad(es)");
    }

    private void mostrarInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
