package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import proyectofinal.Personal.Advisor;

import java.util.function.Consumer;

public class RegistroAsesorController {

    @FXML
    private TextField campoId;
    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoContacto;
    @FXML
    private TextField campoZona;
    @FXML
    private Spinner<Integer> campoCierres;
    @FXML
    private Label labelError;

    private Consumer<Advisor> onRegistroExitoso;

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        campoCierres.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9999, 0));
    }

    // ─────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────

    public void setOnRegistroExitoso(Consumer<Advisor> callback) {
        this.onRegistroExitoso = callback;
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void registrarAsesor() {
        ocultarError();

        String id = campoId.getText().trim();
        String nombre = campoNombre.getText().trim();
        String contacto = campoContacto.getText().trim();
        String zona = campoZona.getText().trim();

        if (id.isEmpty()) {
            mostrarError("La identificación es obligatoria.");
            return;
        }
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            return;
        }
        if (contacto.isEmpty()) {
            mostrarError("El contacto es obligatorio.");
            return;
        }
        if (zona.isEmpty()) {
            mostrarError("La zona es obligatoria.");
            return;
        }

        // Verificar ID duplicado
        for (Advisor a : AppContext.getInstance().getAdvisors()) {
            if (a.getId().equalsIgnoreCase(id)) {
                mostrarError("Ya existe un asesor con la identificación \"" + id + "\".");
                return;
            }
        }

        Advisor nuevo = new Advisor(
                id, nombre, contacto, zona,
                null,
                campoCierres.getValue());

        AppContext.getInstance().getAdvisors().add(nuevo);

        if (onRegistroExitoso != null)
            onRegistroExitoso.accept(nuevo);
        cerrarVentana();
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    // ─────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────

    private void cerrarVentana() {
        ((Stage) campoId.getScene().getWindow()).close();
    }

    private void mostrarError(String msg) {
        labelError.setText("AVERTENCIA  " + msg);
        labelError.setVisible(true);
        labelError.setManaged(true);
    }

    private void ocultarError() {
        labelError.setVisible(false);
        labelError.setManaged(false);
    }
}