package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.application.Platform;

import proyectofinal.Main;
// IMPORTANTE: Asegúrate de importar tu gestor de persistencia e hilos de contexto
import proyectofinal.SistemaGestion.Persistencia.PersistenceManager;

public class RegisterController {

    @FXML private TextField campoNombre;
    @FXML private TextField campoId;
    @FXML private TextField campoCorreo;
    @FXML private TextField campoTelefono;
    @FXML private PasswordField campoContrasena;
    @FXML private PasswordField campoConfirmarContrasena;
    @FXML private Label mensajeFeedback;

    @FXML
    public void manejarRegistro() {
        String nombre   = campoNombre.getText().trim();
        String id       = campoId.getText().trim();
        String correo   = campoCorreo.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String password = campoContrasena.getText();
        String confirm  = campoConfirmarContrasena.getText();

        if (estaVacio(nombre, id, correo, password, telefono)) {
            mostrarError("Por favor completa todos los campos.");
            return;
        }

        if (!password.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden.");
            campoConfirmarContrasena.requestFocus();
            return;
        }

        try {
            AppContext context = AppContext.getInstance();

            // 2. Registramos el cliente en la memoria (Estructura de Datos)
            context.getClientManager().registerBasic(
                id, nombre, correo, password, telefono
            );

            PersistenceManager.saveAll(
                context.getPropertyManager(),
                context.getClientManager(),
                context.getAdvisors(), 
                context.getVisitManager(),
                context.getContracts()
            );

            mostrarExito("¡Cuenta creada exitosamente! Redirigiendo...");
            bloquearFormulario(true);
            navegarAlLoginConRetraso();

        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    private boolean estaVacio(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.isEmpty()) return true;
        }
        return false;
    }

    private void navegarAlLoginConRetraso() {
        new Thread(() -> {
            try {
                Thread.sleep(1500); 
                Platform.runLater(() -> {
                    try {
                        Main.cargarLogin();
                    } catch (Exception ignored) {}
                });
            } catch (InterruptedException ignored) {}
        }).start();
    }

    private void bloquearFormulario(boolean bloquear) {
        campoNombre.setDisable(bloquear);
        campoId.setDisable(bloquear);
        campoCorreo.setDisable(bloquear);
        campoTelefono.setDisable(bloquear);
        campoContrasena.setDisable(bloquear);
        campoConfirmarContrasena.setDisable(bloquear);
    }

    @FXML
    public void volverAlLogin() {
        try {
            Main.cargarLogin();
        } catch (Exception e) {
            mostrarError("Error al intentar volver al login.");
        }
    }

    private void mostrarError(String msg) {
        mensajeFeedback.setText(msg);
        mensajeFeedback.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
    }

    private void mostrarExito(String msg) {
        mensajeFeedback.setText(msg);
        mensajeFeedback.setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
    }
}