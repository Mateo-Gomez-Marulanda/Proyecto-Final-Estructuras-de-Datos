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
    @FXML private ComboBox<String> campoZona;
    @FXML private TextField      campoCiudadInteres;
    @FXML private ComboBox<TypeProperty> comboTipoInmueble;
    @FXML private Spinner<Integer>       spinnerHab;
    @FXML private Label      mensajeFeedback;

    @FXML
    public void initialize() {
        comboTipoInmueble.getItems().setAll(TypeProperty.values());
        campoZona.getItems().setAll("Norte", "Centro", "Sur");
        cargarDatosActuales();
    }

    private void cargarDatosActuales() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client == null) return;

        campoNombre.setText(client.getName());
        campoCorreo.setText(client.getEmail());
        campoTelefono.setText(client.getPhoneNumber() != null ? client.getPhoneNumber() : "");
        campoPresupuesto.setText(String.valueOf((int) client.getBudget()));
        comboTipoInmueble.setValue(client.getDesiredPropertyType());
        campoCiudadInteres.setText(client.getInterestCity() != null ? client.getInterestCity() : "");

        if (spinnerHab.getValueFactory() != null) {
            spinnerHab.getValueFactory().setValue(client.getMinRooms());
        }

        String zonaGuardada = client.getInterestZones();
        if (zonaGuardada != null && !zonaGuardada.trim().isEmpty()) {
            String zonaNormalizada = zonaGuardada.trim().substring(0, 1).toUpperCase() 
                                   + zonaGuardada.trim().substring(1).toLowerCase();
            
            if (campoZona.getItems().contains(zonaNormalizada)) {
                campoZona.setValue(zonaNormalizada);
            } else {
                if (!campoZona.getItems().contains(zonaGuardada)) {
                    campoZona.getItems().add(zonaGuardada);
                }
                campoZona.setValue(zonaGuardada);
            }
        } else {
            campoZona.setValue(null);
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
        String zona = (campoZona.getValue() == null || campoZona.getValue().isEmpty()) 
                      ? "Sin definir" : campoZona.getValue();
        String password    = campoContrasena.getText();
        String ciudad = campoCiudadInteres.getText().trim().isEmpty() 
                    ? "Sin definir" : campoCiudadInteres.getText().trim();

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
                    correo, telefono, budget, zona, ciudad,
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
