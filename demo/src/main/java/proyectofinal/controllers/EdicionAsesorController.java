package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import proyectofinal.Personal.Advisor;

public class EdicionAsesorController {

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

    private Advisor advisorToEdit;
    private Runnable onEdicionExitosa;

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

    public void setAdvisorToEdit(Advisor advisor) {
        this.advisorToEdit = advisor;
        preCargarCampos();
    }

    public void setOnEdicionExitosa(Runnable callback) {
        this.onEdicionExitosa = callback;
    }

    private void preCargarCampos() {
        campoId.setText(advisorToEdit.getId());
        campoNombre.setText(advisorToEdit.getName());
        campoContacto.setText(advisorToEdit.getContactInfo() != null
                ? advisorToEdit.getContactInfo()
                : "");
        campoZona.setText(advisorToEdit.getZoneSpecialty() != null
                ? advisorToEdit.getZoneSpecialty()
                : "");
        campoCierres.getValueFactory().setValue(advisorToEdit.getCompletedClosings());
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void guardarCambios() {
        ocultarError();

        String nombre = campoNombre.getText().trim();
        String contacto = campoContacto.getText().trim();
        String zona = campoZona.getText().trim();

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

        advisorToEdit.setName(nombre);
        advisorToEdit.setContactInfo(contacto);
        advisorToEdit.setZoneSpecialty(zona);
        advisorToEdit.setCompletedClosings(campoCierres.getValue());

        if (onEdicionExitosa != null)
            onEdicionExitosa.run();
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
        labelError.setText("⚠  " + msg);
        labelError.setVisible(true);
        labelError.setManaged(true);
    }

    private void ocultarError() {
        labelError.setVisible(false);
        labelError.setManaged(false);
    }
}