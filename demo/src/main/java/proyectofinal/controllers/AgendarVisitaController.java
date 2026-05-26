package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;
import proyectofinal.SistemaGestion.AgendamientoVisitas.Visit;

import java.time.LocalDate;
import java.util.function.Consumer;

public class AgendarVisitaController {

    @FXML
    private ComboBox<Client> campoCliente;
    @FXML
    private Label labelTipoCliente;
    @FXML
    private ComboBox<Property> campoInmueble;
    @FXML
    private Label labelAsesor;
    @FXML
    private DatePicker campoFecha;
    @FXML
    private Spinner<Integer> campoHora;
    @FXML
    private Spinner<Integer> campoMinutos;
    @FXML
    private Label labelError;

    private Client clientePreseleccionado;
    private Property inmueblePreseleccionado;

    private Consumer<Visit> onAgendadoExitoso;

    // ─────────────────────────────────────────────────────────
    // Init
    // ─────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        configurarComboCliente();
        configurarComboInmueble();
        configurarSpinners();
        campoFecha.setValue(LocalDate.now().plusDays(1));

        if (campoCliente.getValue() != null)
            onClienteSeleccionado();
        if (campoInmueble.getValue() != null)
            onInmuebleSeleccionado();
    }

    private void configurarComboCliente() {
        campoCliente.setConverter(new StringConverter<>() {
            @Override
            public String toString(Client c) {
                return c == null ? "" : c.getName() + " (" + c.getId() + ")";
            }

            @Override
            public Client fromString(String s) {
                return null;
            }
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
            @Override
            public String toString(Property p) {
                return p == null ? "" : p.getCode() + " — " + p.getAddress();
            }

            @Override
            public Property fromString(String s) {
                return null;
            }
        });
        for (Property p : AppContext.getInstance().getPropertyManager().getProperties()) {
            if (p.isAvailable())
                campoInmueble.getItems().add(p);
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

    public void setOnAgendadoExitoso(Consumer<Visit> callback) {
        this.onAgendadoExitoso = callback;
    }

    // ─────────────────────────────────────────────────────────
    // Reactive handlers
    // ─────────────────────────────────────────────────────────

    @FXML
    private void onClienteSeleccionado() {
        Client c = campoCliente.getValue();
        labelTipoCliente.setText(c != null ? c.getClientType() : "—");
    }

    @FXML
    private void onInmuebleSeleccionado() {
        Property p = campoInmueble.getValue();
        labelAsesor.setText(p != null && p.getResponsibleAdvisor() != null
                ? p.getResponsibleAdvisor().getName()
                : "Sin asesor asignado");
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
        // 1. Ocultar errores previos en el FXML
        ocultarError();

        // 2. Extraer datos
        Client cliente = campoCliente.getValue();
        Property inmueble = campoInmueble.getValue();
        LocalDate fecha = campoFecha.getValue();

        if (cliente == null) {
            mostrarError("Selecciona un cliente.");
            return;
        }
        if (inmueble == null) {
            mostrarError("Selecciona un inmueble.");
            return;
        }
        if (fecha == null) {
            mostrarError("Selecciona una fecha.");
            return;
        }

        java.time.LocalTime hora = java.time.LocalTime.of(campoHora.getValue(), campoMinutos.getValue());

        // 3. Validación de tiempo
        if (java.time.LocalDateTime.of(fecha, hora).isBefore(java.time.LocalDateTime.now())) {
            mostrarError("La fecha y hora no pueden ser en el pasado.");
            return;
        }

        // 4. Llamada correcta al VisitManager con LocalDate y LocalTime separados
        AppContext.getInstance().getVisitManager()
                .scheduleVisit(cliente, inmueble, fecha, hora);

        // 5. Flujo de éxito original (sin popups informativos)
        if (onAgendadoExitoso != null)
            onAgendadoExitoso.accept(null);
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