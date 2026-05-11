package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import proyectofinal.Main;

public class ShellController {

    @FXML private StackPane contenidoCentral;
    @FXML private Label     labelUsuarioActivo;

    @FXML private Button btnDashboard;
    @FXML private Button btnInmuebles;
    @FXML private Button btnClientes;
    @FXML private Button btnAsesores;
    @FXML private Button btnVisitas;
    @FXML private Button btnOperaciones;
    @FXML private Button btnAlertas;

    private Button activeButton;

    @FXML
    public void initialize() {
        // Show current user name in sidebar footer
        var client = AppContext.getInstance().getClientManager().getCurrent();
        if (client != null) {
            labelUsuarioActivo.setText(client.getName());
        }

        // Load dashboard by default
        navegarDashboard();
    }

    // ─────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────

    @FXML public void navegarDashboard()    { cargarVista("/proyectofinal/views/dashboard-content.fxml",   btnDashboard); }
    @FXML public void navegarInmuebles()    { cargarVista("/proyectofinal/views/inmuebles-content.fxml",   btnInmuebles); }
    @FXML public void navegarClientes()     { cargarVista("/proyectofinal/views/clientes-content.fxml",    btnClientes); }
    @FXML public void navegarAsesores()     { cargarVista("/proyectofinal/views/asesores-content.fxml",    btnAsesores); }
    @FXML public void navegarVisitas()      { cargarVista("/proyectofinal/views/visitas-content.fxml",     btnVisitas); }
    @FXML public void navegarOperaciones()  { cargarVista("/proyectofinal/views/operaciones-content.fxml", btnOperaciones); }
    @FXML public void navegarAlertas()      { cargarVista("/proyectofinal/views/alertas-content.fxml",     btnAlertas); }

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

    // ─────────────────────────────────────────────
    // Internal
    // ─────────────────────────────────────────────

    private void cargarVista(String fxmlPath, Button button) {
        try {
            Node vista = FXMLLoader.load(getClass().getResource(fxmlPath));
            contenidoCentral.getChildren().setAll(vista);
            actualizarBotonActivo(button);
        } catch (Exception e) {
            System.err.println("Error loading view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void actualizarBotonActivo(Button selected) {
        // Remove active style from previous button
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-button-activo");
        }
        // Apply active style to new button
        if (selected != null && !selected.getStyleClass().contains("nav-button-activo")) {
            selected.getStyleClass().add("nav-button-activo");
        }
        activeButton = selected;
    }
}
