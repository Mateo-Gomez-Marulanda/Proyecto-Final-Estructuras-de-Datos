package proyectofinal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import proyectofinal.SistemaGestion.Reportes.ReportEngine;

public class ReportesController {

    @FXML
    private TextArea areaReporte;

    /**
     * Método llamado automáticamente por JavaFX al cargar la vista.
     */
    @FXML
    public void initialize() {
        // Generar el reporte apenas se abra la ventana
        generarReporte();
    }

    /**
     * Obtiene los datos calculados desde la capa de negocio (ReportEngine)
     * y los inyecta en el área de texto.
     */
    @FXML
    public void generarReporte() {
        try {
            AppContext context = AppContext.getInstance();
            String reporteStr = ReportEngine.generateFullSummaryReport(context);
            
            // Actualizar la interfaz
            areaReporte.setText(reporteStr);
        } catch (Exception e) {
            areaReporte.setText("Error al generar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpia el área de texto.
     */
    @FXML
    public void limpiarReporte() {
        areaReporte.clear();
    }
}