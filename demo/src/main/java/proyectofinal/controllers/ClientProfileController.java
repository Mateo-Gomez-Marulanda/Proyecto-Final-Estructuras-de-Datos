package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Personal.Client;

public class ClientProfileController {

    @FXML private TextField  campoNombre;
    @FXML private TextField  campoCorreo;
    @FXML private TextField  campoTelefono;
    @FXML private PasswordField campoContrasena;
    @FXML private TextField  campoPresupuesto;
    @FXML private TextField  campoZona;
    @FXML private ComboBox<TypeProperty> comboTipoInmueble;
    @FXML private Spinner<Integer>       spinnerHab;
    @FXML private Label      mensajeFeedback;

    @FXML
    public void initialize() {
        comboTipoInmueble.getItems().setAll(TypeProperty.values());
        cargarDatosActuales();
    }

    private void cargarDatosActuales() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client == null) return;

        campoNombre.setText(client.getName());
        campoCorreo.setText(client.getEmail());
        campoTelefono.setText(client.getPhoneNumber() != null ? client.getPhoneNumber() : "");
        campoPresupuesto.setText(String.valueOf((int) client.getBudget()));
        campoZona.setText(client.getInterestZones() != null ? client.getInterestZones() : "");
        comboTipoInmueble.setValue(client.getDesiredPropertyType());

        if (spinnerHab.getValueFactory() != null) {
            spinnerHab.getValueFactory().setValue(client.getMinRooms());
        }
    }

    @FXML
    public void guardarCambios() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client == null) return;

        String nombre      = campoNombre.getText().trim();
        String correo      = campoCorreo.getText().trim();
        String telefono    = campoTelefono.getText().trim();
        String presupuesto = campoPresupuesto.getText().trim();
        String zona        = campoZona.getText().trim();
        String password    = campoContrasena.getText();

        if (nombre.isEmpty() || correo.isEmpty()) {
            mostrarError("Nombre y correo son obligatorios.");
            return;
        }

        double budget;
        try {
            budget = Double.parseDouble(presupuesto.replace(",", "").replace("$", ""));
        } catch (NumberFormatException e) {
            mostrarError("El presupuesto debe ser un número válido.");
            return;
        }

        try {
            AppContext.getInstance().getClientManager().updateClient(
                    correo, telefono, budget, zona,
                    comboTipoInmueble.getValue(),
                    spinnerHab.getValue()
            );
            client.setName(nombre);

            // Update password only if a new one was typed
            if (!password.isEmpty()) {
                client.setPassword(password);
            }

            mostrarExito("✅ Perfil actualizado correctamente.");
        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void cancelarEdicion() {
        cargarDatosActuales();
        mensajeFeedback.setText("");
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
