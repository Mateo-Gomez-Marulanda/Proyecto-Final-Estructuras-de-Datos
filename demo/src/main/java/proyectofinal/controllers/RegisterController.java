package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Main;

public class RegisterController {

    @FXML private TextField     campoNombre;
    @FXML private TextField     campoId;
    @FXML private TextField     campoCorreo;
    @FXML private TextField     campoTelefono;
    @FXML private PasswordField campoContrasena;
    @FXML private PasswordField campoConfirmarContrasena;
    @FXML private Label         mensajeFeedback;

    @FXML
    public void manejarRegistro() {
        String nombre    = campoNombre.getText().trim();
        String id        = campoId.getText().trim();
        String correo    = campoCorreo.getText().trim();
        String telefono  = campoTelefono.getText().trim();
        String password  = campoContrasena.getText();
        String confirm   = campoConfirmarContrasena.getText();

        // Validaciones básicas
        if (nombre.isEmpty() || id.isEmpty() || correo.isEmpty()
                || telefono.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor completa todos los campos.");
            return;
        }

        if (!password.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        if (password.length() < 6) {
            mostrarError("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        try {
            AppContext.getInstance().getClientManager().registerClient(
                    id, nombre, correo, telefono,
                    "Regular",          // tipo de cliente por defecto
                    0.0,                // presupuesto inicial
                    "Sin definir",      // zonas de interés
                    TypeProperty.APARTAMENTO, // tipo inmueble deseado por defecto
                    1,                  // mínimo de habitaciones
                    "Buscando",         // estado de búsqueda
                    password
            );

            mostrarExito("¡Cuenta creada exitosamente! Redirigiendo...");

            // Breve pausa visual y navegar al login
            new Thread(() -> {
                try {
                    Thread.sleep(1200);
                    javafx.application.Platform.runLater(() -> {
                        try { Main.cargarLogin(); } catch (Exception ignored) {}
                    });
                } catch (InterruptedException ignored) {}
            }).start();

        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void volverAlLogin() {
        try {
            Main.cargarLogin();
        } catch (Exception e) {
            mostrarError("No se pudo volver al login.");
        }
    }

    private void mostrarError(String msg) {
        mensajeFeedback.setText(msg);
        mensajeFeedback.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");
    }

    private void mostrarExito(String msg) {
        mensajeFeedback.setText(msg);
        mensajeFeedback.setStyle("-fx-text-fill: #16a34a; -fx-font-size: 12px;");
    }
}
