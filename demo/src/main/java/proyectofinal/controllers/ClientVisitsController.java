package proyectofinal.controllers;

import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;
import proyectofinal.SistemaGestion.AgendamientoVisitas.Visit;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitStatus;
import proyectofinal.SistemaGestion.Observer.OperationEvent;
import proyectofinal.SistemaGestion.Observer.OperationPublisher;
import proyectofinal.SistemaGestion.OperacionDeNegocio.BusinessOperation;
import proyectofinal.SistemaGestion.OperacionDeNegocio.OperationType;

public class ClientVisitsController {

    @FXML
    private TableView<Visit> tablaActivas;
    @FXML
    private TableColumn<Visit, String> colACodigo, colAInmueble, colAFecha, colAHora, colAEstado, colAAsesor;
    @FXML
    private Button btnCancelar;

    @FXML
    private TableView<Property> tablaHistorial;
    @FXML
    private TableColumn<Property, String> colHCodigo, colHDireccion, colHCiudad, colHTipo, colHPrecio;
    @FXML
    private Button btnFavoritoDesdeHistorial;

    // El botón único e inteligente para iniciar el cierre del negocio
    @FXML
    private Button btnIniciarTramite;

    private ObservableList<Visit> activasList;
    private ObservableList<Property> historialList;

    @FXML
    public void initialize() {
        configurarColumnasActivas();
        configurarColumnasHistorial();
        cargarDatos();
        configurarSelections();
    }

    private void configurarColumnasActivas() {
        colACodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colAInmueble.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProperty().getCode() + " — " + d.getValue().getProperty().getAddress()));
        colAFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDate().toString()));
        colAHora.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTime().toString()));
        colAEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVisitStatus().name()));
        colAAsesor.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getAssignedAdvisor() != null ? d.getValue().getAssignedAdvisor().getName()
                        : "Sin asignar"));
    }

    private void configurarColumnasHistorial() {
        colHCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colHDireccion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAddress()));
        colHCiudad.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCity()));
        colHTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().toString()));
        colHPrecio.setCellValueFactory(d -> new SimpleStringProperty(String.format("$%,.0f", d.getValue().getPrice())));
    }

    private void cargarDatos() {
        Client client = AppContext.getInstance().getClientManager().getCurrent();
        if (client == null)
            return;

        // 1. Cargar visitas que se encuentran activas
        activasList = FXCollections.observableArrayList();
        for (Visit v : client.getVisitasProgramadas()) {
            if (v.getVisitStatus() == VisitStatus.PENDING || v.getVisitStatus() == VisitStatus.CONFIRM) {
                activasList.add(v);
            }
        }
        tablaActivas.setItems(activasList);

        // 2. Historial de propiedades que ya fueron visitadas
        historialList = FXCollections.observableArrayList();
        for (Property p : client.getVisitedPropertiesHistory()) {
            historialList.add(p);
        }
        tablaHistorial.setItems(historialList);
    }

    private void configurarSelections() {
        btnCancelar.setDisable(true);
        btnFavoritoDesdeHistorial.setDisable(true);
        btnIniciarTramite.setDisable(true);

        // Listener para la tabla de visitas activas
        tablaActivas.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> btnCancelar.setDisable(sel == null));

        // Listener reactivo para cambiar la apariencia y texto del botón según el
        // propósito del inmueble
        tablaHistorial.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) {
                btnFavoritoDesdeHistorial.setDisable(true);
                btnIniciarTramite.setDisable(true);
                btnIniciarTramite.setText("💼 Iniciar Trámite");
                btnIniciarTramite.setStyle(
                        "-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6;");
            } else {
                btnFavoritoDesdeHistorial.setDisable(false);
                btnIniciarTramite.setDisable(false);

                String proposito = sel.getPurpose().toUpperCase().trim();
                if (proposito.equals("VENTA")) {
                    btnIniciarTramite.setText("🤝 Solicitar Compra de Inmueble");
                    btnIniciarTramite.setStyle(
                            "-fx-background-color: #16a34a; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                } else {
                    btnIniciarTramite.setText("🔑 Solicitar Arriendo de Inmueble");
                    btnIniciarTramite.setStyle(
                            "-fx-background-color: #7c3aed; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
                }
            }
        });
    }

    @FXML
    public void cancelarVisita() {
        Visit selected = tablaActivas.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar Visita");
        dialog.setHeaderText("Confirmación de Cancelación");
        dialog.setContentText("¿Por qué desea cancelar la visita?:");

        dialog.showAndWait().ifPresent(motivo -> {
            AppContext.getInstance().getVisitManager().cancelVisit(selected, motivo);
            activasList.remove(selected);
            mostrarNotificacionUI("Operación Exitosa", "La visita ha sido cancelada correctamente.");
        });
    }

    @FXML
    public void guardarFavoritoDesdeHistorial() {
        Property selected = tablaHistorial.getSelectionModel().getSelectedItem();
        if (selected == null)
            return;

        AppContext.getInstance().getClientManager().markAsFavorite(selected);
        mostrarNotificacionUI("Favoritos", "El inmueble se agregó a su lista de favoritos.");
    }

   @FXML
private void manejarSolicitudTramite() {
    Property propiedadSeleccionada = tablaHistorial.getSelectionModel().getSelectedItem();
    Client clienteActual = AppContext.getInstance().getClientManager().getCurrent(); 

    System.out.println("[DEBUG] Botón presionado.");
    if (propiedadSeleccionada == null) {
        System.out.println("[DEBUG] ERROR: No hay propiedad seleccionada en la tabla.");
        mostrarNotificacionUI("Selección Requerida", "Por favor, seleccione un inmueble.");
        return;
    }
    if (clienteActual == null) {
        System.out.println("[DEBUG] ERROR: El cliente actual en AppContext es NULL.");
        mostrarNotificacionUI("Error de Sesión", "No se encontró un cliente activo.");
        return;
    }

    try {
        double valorAcordado = propiedadSeleccionada.getPrice();
        double comisionInmobiliaria = 0.0;
        OperationType tipoOperacion;

        // 🚨 SOSPECHOSO 1: ¿El propósito viene vacío, nulo o en minúsculas diferentes?
        if (propiedadSeleccionada.getPurpose() == null) {
            System.out.println("[DEBUG] ERROR: El propósito del inmueble es NULL.");
            return;
        }
        
        String proposito = propiedadSeleccionada.getPurpose().toUpperCase().trim();
        System.out.println("[DEBUG] Propósito del inmueble detectado: " + proposito);

        if (proposito.equals("VENTA")) {
            tipoOperacion = OperationType.SALE;
            comisionInmobiliaria = valorAcordado * 0.03; 
        } else if (proposito.equals("ARRIENDO") || proposito.equals("ALQUILER")) {
            tipoOperacion = OperationType.RENTAL; // 🚨 SOSPECHOSO 2: Verifica si en tu enum es RENT o RENTAL
            comisionInmobiliaria = valorAcordado * 0.10; 
        } else {
            System.out.println("[DEBUG] ERROR: El propósito '" + proposito + "' no coincide con VENTA o ARRIENDO.");
            mostrarNotificacionUI("Error", "El propósito debe ser VENTA o ARRIENDO.");
            return;
        }

        System.out.println("[DEBUG] Intentando instanciar BusinessOperation...");
        
        // 🚨 SOSPECHOSO 3: El estado inicial. 
        // Verifica si tu enum 'ProcessStatus' tiene 'IN_PROGRESS' o 'PENDING_SIGNATURE'.
        // Si usas uno que no es, compila pero puede fallar en lógica interna.
        BusinessOperation nuevaOperacion = new BusinessOperation(
            "OP-" + System.currentTimeMillis(),
            propiedadSeleccionada,
            clienteActual,
            propiedadSeleccionada.getResponsibleAdvisor(), // ¿Esto es null?
            LocalDate.now(),
            tipoOperacion,
            valorAcordado,
            comisionInmobiliaria,
            proyectofinal.SistemaGestion.OperacionDeNegocio.ProcessStatus.IN_PROGRESS 
        );

        System.out.println("[DEBUG] Operación instanciada con éxito: " + nuevaOperacion.getIdentifier());

        // 🚨 SOSPECHOSO 4: Agregar a la estructura
        System.out.println("[DEBUG] Agregando a la lista de AppContext...");
        AppContext.getInstance().getOperations().add(nuevaOperacion);

        // Notificar al Admin
        System.out.println("[DEBUG] Publicando evento OPERATION_CREATED...");
        OperationPublisher.getInstance().publish(nuevaOperacion, OperationEvent.EventType.OPERATION_CREATED);

        // Guardar en disco
        System.out.println("[DEBUG] Guardando en archivos planos...");
        AppContext.getInstance().saveAll();

        System.out.println("[DEBUG] ¡Todo el proceso del cliente terminó con ÉXITO!");
        mostrarNotificacionUI("Trámite Iniciado", "Su solicitud ha sido enviada con éxito.");
        
    } catch (Exception e) {
        System.out.println("[DEBUG] 🔥 CRASH CRÍTICO EN EL PROCESO:");
        e.printStackTrace(); // Esto te dirá exactamente en qué línea se rompe
        mostrarNotificacionUI("Error", "No se pudo procesar: " + e.getMessage());
    }
}

    /**
     * Lanza ventanas emergentes informativas en la interfaz gráfica de usuario
     * utilizando las clases nativas de JavaFX de forma explícita.
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