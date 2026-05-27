package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;

public class AgendarVisitaCLIController {

    @FXML
    private TextField txtInmueble;
    @FXML
    private DatePicker campoFecha;
    @FXML
    private ComboBox<LocalTime> cmbHora;
    @FXML
    private TextArea txtObservaciones;
    @FXML
    private Label labelError;

    private Property inmuebleSeleccionado;
    private Client clienteActual;

    @FXML
    public void initialize() {
        configurarComboBoxHora();
        campoFecha.setValue(LocalDate.now().plusDays(1));
    }

    private void configurarComboBoxHora() {
        // Limpiamos por si acaso y llenamos con intervalos de 8:00 AM a 5:00 PM
        cmbHora.getItems().clear();
        for (int h = 8; h <= 17; h++) {
            cmbHora.getItems().add(LocalTime.of(h, 0));
            cmbHora.getItems().add(LocalTime.of(h, 30));
        }
        // Seleccionar el primer horario por defecto para evitar NullPointerException
        cmbHora.getSelectionModel().selectFirst();
    }

    public void setDatosIniciales(Property p, Client c) {
        this.inmuebleSeleccionado = p;
        this.clienteActual = c;

        // Actualizamos la UI
        txtInmueble.setText(p.getCode() + " — " + p.getAddress());
        txtInmueble.setEditable(false);
    }

    @FXML
    public void agendarVisita() {
        ocultarError();

        if (clienteActual == null || inmuebleSeleccionado == null) {
            mostrarError("Error: Datos de cliente o inmueble no cargados.");
            return;
        }

        LocalDate fecha = campoFecha.getValue();
        LocalTime hora = cmbHora.getValue(); // Extrae directamente el LocalTime seleccionado

        if (fecha == null) {
            mostrarError("Selecciona una fecha válida.");
            return;
        }
        if (hora == null) {
            mostrarError("Por favor, selecciona una hora para la visita.");
            return;
        }
        if (java.time.LocalDateTime.of(fecha, hora).isBefore(java.time.LocalDateTime.now())) {
            mostrarError("La fecha y hora no pueden ser pasadas.");
            return;
        }

        // Registrar visita usando el manager
        AppContext.getInstance().getVisitManager().scheduleVisit(
                this.clienteActual,
                this.inmuebleSeleccionado,
                fecha,
                hora);

        cerrarVentana();
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    private void mostrarError(String msg) {
        labelError.setText("⚠ " + msg);
        labelError.setVisible(true);
        labelError.setManaged(true);
    }

    private void ocultarError() {
        labelError.setVisible(false);
        labelError.setManaged(false);
    }

    private void cerrarVentana() {
        ((Stage) txtInmueble.getScene().getWindow()).close();
    }
}