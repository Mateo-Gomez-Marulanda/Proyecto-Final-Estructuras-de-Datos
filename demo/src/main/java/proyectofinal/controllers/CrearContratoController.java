package proyectofinal.controllers;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import proyectofinal.SistemaGestion.Observer.OperationEvent;
import proyectofinal.SistemaGestion.Observer.OperationPublisher;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.ProcessStatus;

public class CrearContratoController {

    @FXML
    private TextField campoNombre;
    @FXML
    private DatePicker pickerExpiracion;
    @FXML
    private Label labelError;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnGuardar;

    // Referencia de la operación transaccional que viene desde el
    // OperacionesController
    private BusinessOperation operacionBase;

    /**
     * Inyecta la operación seleccionada en la tabla antes de renderizar el modal
     */
    public void setOperacionBase(BusinessOperation operacion) {
        this.operacionBase = operacion;
    }

    @FXML
    public void initialize() {
        // Inicializar el label de error vacío
        labelError.setText("");
    }

    /**
     * Valida los datos del formulario, cambia el estado de la operación y
     * notifica al Observer para instanciar el contrato en el sistema.
     */
    @FXML
    public void guardarContrato() {
        if (operacionBase == null) {
            labelError.setText("Error crítico: No se encontró la operación base.");
            return;
        }

        String nombreContrato = campoNombre.getText().trim();
        LocalDate fechaExpiracion = pickerExpiracion.getValue();

        // 1. Validaciones estrictas de los campos de entrada
        if (nombreContrato.isEmpty()) {
            labelError.setText("El nombre o título del contrato es obligatorio.");
            return;
        }

        if (fechaExpiracion == null) {
            labelError.setText("Debe seleccionar una fecha de vencimiento válida.");
            return;
        }

        if (fechaExpiracion.isBefore(LocalDate.now())) {
            labelError.setText("La fecha de expiración no puede ser anterior a hoy.");
            return;
        }

        try {
            // 2. Transición formal del estado en la memoria RAM
            operacionBase.setProcessStatus(ProcessStatus.COMPLETED);

            // ======================================================================
            // 🔥 CORRECCIÓN DEL DISPARADOR DE EVENTOS (OBSERVER):
            // Llamamos a tu método real 'publish' pasando la operación y el evento
            // ======================================================================
            OperationPublisher.getInstance().publish(operacionBase, OperationEvent.EventType.OPERATION_UPDATED);

            // 3. Persistencia síncrona inmediata en los archivos planos de texto (.txt)
            AppContext.getInstance().saveAll();

            // 4. Feedback visual de éxito y cierre del diálogo modal
            mostrarNotificacionUI("Contrato Registrado",
                    "El contrato ha sido generado con éxito y la operación se ha cerrado.");
            cerrarVentana();

        } catch (Exception e) {
            labelError.setText("Error al formalizar el contrato: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Cancela la acción actual y cierra el diálogo sin modificar datos
     */
    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    /**
     * Cierra de manera limpia la ventana modal actual
     */
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra ventanas informativas rápidas en la interfaz gráfica
     */
    private void mostrarNotificacionUI(String titulo, String mensaje) {
        javafx.scene.control.Alert aviso = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        aviso.setTitle(titulo);
        aviso.setHeaderText(null);
        aviso.setContentText(mensaje);
        aviso.showAndWait();
    }
}