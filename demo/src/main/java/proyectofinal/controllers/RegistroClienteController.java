package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Personal.Client;

import java.util.function.Consumer;

public class RegistroClienteController {

    // ── Datos personales ──────────────────────────────────────
    @FXML
    private TextField campoId;
    @FXML
    private TextField campoNombre;
    @FXML
    private TextField campoCorreo;
    @FXML
    private TextField campoTelefono;
    @FXML
    private PasswordField campoContrasena;
    @FXML
    private PasswordField campoConfirmarContrasena;

    // ── Preferencias ──────────────────────────────────────────
    @FXML
    private ComboBox<String> campoTipoCliente;
    @FXML
    private ComboBox<TypeProperty> campoTipoInmueble;
    @FXML
    private TextField campoPresupuesto;
    @FXML
    private Spinner<Integer> campoHabitaciones;
    @FXML
    private ComboBox<String> campoZona;
    @FXML
    private TextField campoCiudadInteres;

    // ── Feedback ─────────────────────────────────────────────
    @FXML
    private Label labelError;

    private Consumer<Client> onRegistroExitoso;

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        campoTipoCliente.getItems().setAll("POTENTIAL", "Regular", "Frecuente", "Premium");
        campoTipoCliente.setValue("POTENTIAL");
        campoZona.getItems().setAll("Norte", "Centro", "Sur");

        campoTipoInmueble.setConverter(new StringConverter<>() {
            @Override
            public String toString(TypeProperty t) {
                return t == null ? "Sin definir" : t.toString();
            }

            @Override
            public TypeProperty fromString(String s) {
                return null;
            }
        });
        campoTipoInmueble.getItems().add(null);
        campoTipoInmueble.getItems().addAll(TypeProperty.values());

        campoHabitaciones.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 1));
    }

    // ─────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────

    public void setOnRegistroExitoso(Consumer<Client> callback) {
        this.onRegistroExitoso = callback;
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void registrarCliente() {
        ocultarError();

        String id = campoId.getText().trim();
        String nombre = campoNombre.getText().trim();
        String correo = campoCorreo.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String password = campoContrasena.getText();
        String confirm = campoConfirmarContrasena.getText();

        if (id.isEmpty()) {
            mostrarError("La identificación es obligatoria.");
            return;
        }
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            return;
        }
        if (correo.isEmpty()) {
            mostrarError("El correo es obligatorio.");
            return;
        }
        if (telefono.isEmpty()) {
            mostrarError("El teléfono es obligatorio.");
            return;
        }
        if (password.isEmpty()) {
            mostrarError("La contraseña es obligatoria.");
            return;
        }

        if (!password.equals(confirm)) {
            mostrarError("Las contraseñas no coinciden.");
            return;
        }

        double presupuesto = 0;
        String presupuestoTxt = campoPresupuesto.getText().trim();
        if (!presupuestoTxt.isEmpty()) {
            try {
                presupuesto = Double.parseDouble(
                        presupuestoTxt.replace(",", "").replace("$", ""));
                if (presupuesto < 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarError("El presupuesto debe ser un número positivo.");
                return;
            }
        }

        if (AppContext.getInstance().getClientManager().getClientTable().containsKey(id)) {
            mostrarError("Ya existe un cliente con la identificación \"" + id + "\".");
            return;
        }

        try {
            String tipo = campoTipoCliente.getValue() != null
                    ? campoTipoCliente.getValue()
                    : "POTENTIAL";
            String zona = (campoZona.getValue() == null) ? "Sin definir" : campoZona.getValue();
            String ciudad = campoCiudadInteres.getText().trim().isEmpty()
                    ? "Sin definir"
                    : campoCiudadInteres.getText().trim();

            AppContext.getInstance().getClientManager().registerFull(
                    id, nombre, correo, telefono,
                    tipo, presupuesto,
                    zona, ciudad,
                    campoTipoInmueble.getValue(),
                    campoHabitaciones.getValue(),
                    "INACTIVE", password);

            Client registrado = AppContext.getInstance()
                    .getClientManager().getClientTable().get(id);

            if (onRegistroExitoso != null && registrado != null) {
                onRegistroExitoso.accept(registrado);
            }
            cerrarVentana();

        } catch (RuntimeException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    // Helpers
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