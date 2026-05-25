package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;

public class AgendarVisitaController {

    @FXML private ComboBox<Client>   campoCliente;
    @FXML private Label              labelTipoCliente;
    @FXML private ComboBox<Property> campoInmueble;
    @FXML private Label              labelAsesor;
    @FXML private DatePicker         campoFecha;
    @FXML private Spinner<Integer>   campoHora;
    @FXML private Spinner<Integer>   campoMinutos;
    @FXML private Label              labelError;

    private Client clientePreseleccionado;
    private Property inmueblePreseleccionado;

    private Consumer<VisitRequest> onAgendadoExitoso;

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarComboCliente();
        configurarComboInmueble();
        configurarSpinners();
        campoFecha.setValue(LocalDate.now().plusDays(1));
    }

    private void configurarComboCliente() {
        campoCliente.setConverter(new StringConverter<>() {
            @Override public String toString(Client c) {
                return c == null ? "" : c.getName() + " (" + c.getId() + ")";
            }
            @Override public Client fromString(String s) { return null; }
        });
        for (Client c : AppContext.getInstance().getClientManager().getAllClients()) {
            // Exclude ADMIN accounts
            if (!"ADMIN".equalsIgnoreCase(c.getClientType())) {
                campoCliente.getItems().add(c);
            }
        }
        if (clientePreseleccionado != null) {
            campoCliente.setValue(clientePreseleccionado);
            campoCliente.setDisable(true);
        }
    }

    private void configurarComboInmueble() {
        campoInmueble.setConverter(new StringConverter<>() {
            @Override public String toString(Property p) {
                return p == null ? "" : p.getCode() + " — " + p.getAddress();
            }
            @Override public Property fromString(String s) { return null; }
        });
        for (Property p : AppContext.getInstance().getPropertyManager().getProperties()) {
            if (p.isAvailable()) campoInmueble.getItems().add(p);
        }
        if (inmueblePreseleccionado != null) {
            campoInmueble.setValue(inmueblePreseleccionado);
            campoInmueble.setDisable(true);
            actualizarInfoAsesor(inmueblePreseleccionado);
        }
    }

    private void configurarSpinners() {
        campoHora.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 9));
        campoMinutos.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
    }

    // ─────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────

    public void setClientePreseleccionado(Client client) {
        this.clientePreseleccionado = client;
    }

    public void setInmueblePreseleccionado(Property property) {
        this.inmueblePreseleccionado = property;
    }

    public void setOnAgendadoExitoso(Consumer<VisitRequest> callback) {
        this.onAgendadoExitoso = callback;
    }

    // ─────────────────────────────────────────────────────────
    // Reactive handlers
    // ─────────────────────────────────────────────────────────

    @FXML
    public void onClienteSeleccionado() {
        Client c = campoCliente.getValue();
        if (c != null) {
            labelTipoCliente.setText(c.getClientType());
        } else {
            labelTipoCliente.setText("—");
        }
    }

    @FXML
    public void onInmuebleSeleccionado() {
        actualizarInfoAsesor(campoInmueble.getValue());
    }

    private void actualizarInfoAsesor(Property p) {
        if (p == null) {
            labelAsesor.setText("Se asignará según el inmueble seleccionado");
            return;
        }
        if (p.getResponsibleAdvisor() != null) {
            labelAsesor.setText(p.getResponsibleAdvisor().getName()
                    + " · " + p.getResponsibleAdvisor().getContactInfo());
        } else {
            labelAsesor.setText("Sin asesor asignado");
        }
    }

    // ─────────────────────────────────────────────────────────
    // Actions
    // ─────────────────────────────────────────────────────────

    @FXML
    public void agendarVisita() {
        ocultarError();

        Client   cliente  = campoCliente.getValue();
        Property inmueble = campoInmueble.getValue();
        LocalDate fecha   = campoFecha.getValue();

        if (cliente == null)  { mostrarError("Selecciona un cliente.");   return; }
        if (inmueble == null) { mostrarError("Selecciona un inmueble.");  return; }
        if (fecha == null)    { mostrarError("Selecciona una fecha.");    return; }
        if (fecha.isBefore(LocalDate.now())) {
            mostrarError("La fecha no puede ser en el pasado."); return;
        }

        LocalDateTime fechaHora = LocalDateTime.of(
                fecha, java.time.LocalTime.of(campoHora.getValue(), campoMinutos.getValue()));

        AppContext.getInstance().getVisitManager()
                .scheduleVisit(cliente, inmueble, fechaHora);

        if (onAgendadoExitoso != null) onAgendadoExitoso.accept(null);
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
        ((Stage) campoFecha.getScene().getWindow()).close();
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