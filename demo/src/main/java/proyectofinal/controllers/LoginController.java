package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import proyectofinal.Main;

public class LoginController {

    @FXML private TextField     campoUsuario;
    @FXML private PasswordField campoContrasena;
    @FXML private Label         mensajeError;

    @FXML
    public void manejarLogin() {
        String id       = campoUsuario.getText().trim();
        String password = campoContrasena.getText();

        if (id.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor completa todos los campos.");
            return;
        }

        try {
            AppContext.getInstance().getClientManager().login(id, password);
            Main.cargarShellPrincipal();
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado al iniciar sesión.");
        }
    }

    @FXML
    public void irARegistro() {
        try {
            Main.cargarRegistro();
        } catch (Exception e) {
            mostrarError("No se pudo abrir la pantalla de registro.");
        }
    }

    private void mostrarError(String mensaje) {
        mensajeError.setText(mensaje);
        mensajeError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
    }
}
