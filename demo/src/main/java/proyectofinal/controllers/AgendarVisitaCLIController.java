package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.LocalTime;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;

public class AgendarVisitaCLIController {

    @FXML private TextField txtInmueble;
    @FXML private DatePicker campoFecha;
    @FXML private Spinner<Integer> campoHora;
    @FXML private Spinner<Integer> campoMinutos;
    @FXML private Label labelError;

    private Property inmuebleSeleccionado;
    private Client clienteActual;

    @FXML
    public void initialize() {
        configurarSpinners();
        campoFecha.setValue(LocalDate.now().plusDays(1));
    }

    private void configurarSpinners() {
        campoHora.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(8, 17, 9));
        campoMinutos.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
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

        // 1. Usamos la variable que ya inyectamos en setDatosIniciales
        // Esto garantiza que estamos agendando para el cliente que seleccionamos
        if (clienteActual == null || inmuebleSeleccionado == null) {
            mostrarError("Error: Datos de cliente o inmueble no cargados.");
            return;
        }

        LocalDate fecha = campoFecha.getValue();
        LocalTime hora = LocalTime.of(campoHora.getValue(), campoMinutos.getValue());

        // 2. Validaciones
        if (fecha == null) {
            mostrarError("Selecciona una fecha válida.");
            return;
        }
        if (java.time.LocalDateTime.of(fecha, hora).isBefore(java.time.LocalDateTime.now())) {
            mostrarError("La fecha y hora no pueden ser pasadas.");
            return;
        }

        // 3. Registrar visita usando las variables de la clase
        AppContext.getInstance().getVisitManager().scheduleVisit(
            this.clienteActual, 
            this.inmuebleSeleccionado, 
            fecha, 
            hora
        );

        // 4. Cierre
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