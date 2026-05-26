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
        // 1. 🔥 LE QUITAMOS la carga automática para proteger el rendimiento de las estructuras de datos.
        // 2. Le damos estilo de fuente monoespaciada para que las tablas de texto plano se alineen perfectamente.
        areaReporte.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");
        areaReporte.setEditable(false); // Evita que el admin pueda borrar o escribir números falsos dentro del reporte
        
        areaReporte.setText("Presione el botón 'Generar Reporte' para calcular las estadísticas actuales.");
    }

    /**
     * Obtiene los datos calculados desde la capa de negocio (ReportEngine)
     * y los inyecta en el área de texto. Este método debe estar enlazado a tu botón de la UI (onAction).
     */
    @FXML
    public void generarReporte() {
        try {
            // Un pequeño feedback visual para que el usuario sepa que el sistema está trabajando
            areaReporte.setText("Procesando estructuras de datos... Por favor espere.");
            
            AppContext context = AppContext.getInstance();
            String reporteStr = ReportEngine.generateFullSummaryReport(context);
            
            // Actualizar la interfaz con el string final
            areaReporte.setText(reporteStr);
        } catch (Exception e) {
            areaReporte.setText("Error al generar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpia el área de texto. También lo puedes dejar mapeado a un botón de "Limpiar".
     */
    @FXML
    public void limpiarReporte() {
        areaReporte.clear();
        areaReporte.setText("Área limpia. Presione 'Generar Reporte' para recalcular.");
    }
}