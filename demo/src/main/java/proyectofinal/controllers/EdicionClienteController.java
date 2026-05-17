package proyectofinal.controllers;
 
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
 
import proyectofinal.Inmueble.TypeProperty;
import proyectofinal.Personal.Client;
 
public class EdicionClienteController {
 
    // ── Datos personales ──────────────────────────────────────
    @FXML private TextField     campoId;
    @FXML private TextField     campoNombre;
    @FXML private TextField     campoCorreo;
    @FXML private TextField     campoTelefono;
    @FXML private PasswordField campoContrasena;
 
    // ── Preferencias ──────────────────────────────────────────
    @FXML private ComboBox<String>       campoTipoCliente;
    @FXML private ComboBox<String>       campoEstadoBusqueda;
    @FXML private ComboBox<TypeProperty> campoTipoInmueble;
    @FXML private TextField              campoPresupuesto;
    @FXML private Spinner<Integer>       campoHabitaciones;
    @FXML private TextField              campoZona;
 
    // ── Feedback ─────────────────────────────────────────────
    @FXML private Label labelError;
 
    private Client clientToEdit;
    private Runnable onEdicionExitosa;
 
    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────
 
    @FXML
    public void initialize() {
        campoTipoCliente.getItems().setAll("POTENTIAL", "Regular", "Frecuente", "Premium");
        campoEstadoBusqueda.getItems().setAll("INACTIVE", "Buscando", "Interesado",
                "En negociación", "Inactivo");
 
        campoTipoInmueble.setConverter(new StringConverter<>() {
            @Override public String toString(TypeProperty t) {
                return t == null ? "Sin definir" : t.toString();
            }
            @Override public TypeProperty fromString(String s) { return null; }
        });
        campoTipoInmueble.getItems().add(null);
        campoTipoInmueble.getItems().addAll(TypeProperty.values());
 
        campoHabitaciones.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 1));
    }
 
    // ─────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────
 
    public void setClientToEdit(Client client) {
        this.clientToEdit = client;
        preCargarCampos();
    }
 
    public void setOnEdicionExitosa(Runnable callback) {
        this.onEdicionExitosa = callback;
    }
 
    private void preCargarCampos() {
        campoId.setText(clientToEdit.getId());
 
        campoNombre.setText(clientToEdit.getName());
        campoCorreo.setText(clientToEdit.getEmail() != null ? clientToEdit.getEmail() : "");
        campoTelefono.setText(clientToEdit.getPhoneNumber() != null ? clientToEdit.getPhoneNumber() : "");
 
        campoTipoCliente.setValue(clientToEdit.getClientType());
        campoEstadoBusqueda.setValue(clientToEdit.getSearchStatus());
        campoTipoInmueble.setValue(clientToEdit.getDesiredPropertyType());
 
        campoPresupuesto.setText(String.valueOf((int) clientToEdit.getBudget()));
        campoZona.setText(clientToEdit.getInterestZones() != null
                ? clientToEdit.getInterestZones() : "");
 
        if (campoHabitaciones.getValueFactory() != null) {
            campoHabitaciones.getValueFactory().setValue(clientToEdit.getMinRooms());
        }
    }
 
    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────
 
    @FXML
    public void guardarCambios() {
        ocultarError();
 
        String nombre   = campoNombre.getText().trim();
        String correo   = campoCorreo.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String password = campoContrasena.getText();
 
        if (nombre.isEmpty())   { mostrarError("El nombre es obligatorio.");   return; }
        if (correo.isEmpty())   { mostrarError("El correo es obligatorio.");   return; }
        if (telefono.isEmpty()) { mostrarError("El teléfono es obligatorio."); return; }
 
        double presupuesto = 0;
        String presupuestoTxt = campoPresupuesto.getText().trim();
        if (!presupuestoTxt.isEmpty()) {
            try {
                presupuesto = Double.parseDouble(
                        presupuestoTxt.replace(",", "").replace("$", ""));
                if (presupuesto < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                mostrarError("El presupuesto debe ser un número positivo.");
                return;
            }
        }
 
        // Aplicar cambios directamente sobre el objeto
        clientToEdit.setName(nombre);
 
        String zona = campoZona.getText().trim().isEmpty()
                ? "Sin definir" : campoZona.getText().trim();
 
        try {
            AppContext.getInstance().getClientManager().updateClient(
                    correo,
                    telefono,
                    presupuesto,
                    zona,
                    campoTipoInmueble.getValue(),
                    campoHabitaciones.getValue()
            );
        } catch (RuntimeException e) {
            // updateClient opera sobre el current — si el cliente editado no es el
            // current, actualizamos los campos directamente
            clientToEdit.setEmail(correo);
            clientToEdit.setPhoneNumber(telefono);
            clientToEdit.setBudget(presupuesto);
            clientToEdit.setInterestZones(zona);
            clientToEdit.setDesiredPropertyType(campoTipoInmueble.getValue());
            clientToEdit.setMinRooms(campoHabitaciones.getValue());
        }
 
        if (campoTipoCliente.getValue() != null) {
            clientToEdit.setClientType(campoTipoCliente.getValue());
        }
        if (campoEstadoBusqueda.getValue() != null) {
            clientToEdit.setSearchStatus(campoEstadoBusqueda.getValue());
        }
        if (!password.isEmpty()) {
            clientToEdit.setPassword(password);
        }
 
        if (onEdicionExitosa != null) onEdicionExitosa.run();
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