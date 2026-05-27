package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import proyectofinal.Main;
import proyectofinal.Personal.Client;

public class ClientShellController {

    @FXML
    private StackPane contenidoCentral;
    @FXML
    private Label labelNombreCliente;
    @FXML
    private Label labelBienvenida;
    @FXML
    private Label labelTipoCliente;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnCatalogo;
    @FXML
    private Button btnFavoritos;
    @FXML
    private Button btnVisitas;
    @FXML
    private Button btnPerfil;

    private Button activeButton;

    @FXML
    public void initialize() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client != null) {
            labelNombreCliente.setText(client.getName());
            labelBienvenida.setText("Bienvenido,");
            labelTipoCliente.setText("Tipo: " + client.getClientType());
        }
        navegarDashboard();
    }

    @FXML
    public void navegarDashboard() {
        cargarVista("/proyectofinal/views/client-dashboard.fxml", btnDashboard);
    }

    @FXML
    public void navegarCatalogo() {
        cargarVista("/proyectofinal/views/client-catalog.fxml", btnCatalogo);
    }

    @FXML
    public void navegarFavoritos() {
        cargarVista("/proyectofinal/views/client-favorites.fxml", btnFavoritos);
    }

    @FXML
    public void navegarVisitas() {
        cargarVista("/proyectofinal/views/client-visits.fxml", btnVisitas);
    }

    @FXML
    public void navegarPerfil() {
        cargarVista("/proyectofinal/views/client-profile.fxml", btnPerfil);
    }

    @FXML
    public void cerrarSesion() {
        try {
            AppContext.getInstance().getClientManager().logout();
            AppContext.getInstance().saveAll();
            Main.cargarLogin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarVista(String fxmlPath, Button button) {
        try {
            Node vista = FXMLLoader.load(getClass().getResource(fxmlPath));
            contenidoCentral.getChildren().setAll(vista);
            actualizarBotonActivo(button);
        } catch (Exception e) {
            System.err.println("Error loading client view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void actualizarBotonActivo(Button selected) {
        if (activeButton != null)
            activeButton.getStyleClass().remove("nav-button-activo");
        if (selected != null && !selected.getStyleClass().contains("nav-button-activo")) {
            selected.getStyleClass().add("nav-button-activo");
        }
        activeButton = selected;
    }
}
