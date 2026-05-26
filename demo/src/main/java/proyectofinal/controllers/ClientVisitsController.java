package proyectofinal.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import proyectofinal.Inmueble.Property;
import proyectofinal.Personal.Client;
import proyectofinal.SistemaGestion.AgendamientoVisitas.Visit;
import proyectofinal.SistemaGestion.AgendamientoVisitas.VisitStatus;

public class ClientVisitsController {

    @FXML private TableView<Visit> tablaActivas;
    @FXML private TableColumn<Visit, String> colACodigo, colAInmueble, colAFecha, colAHora, colAEstado, colAAsesor;
    @FXML private Button btnCancelar;

    @FXML private TableView<Property> tablaHistorial;
    @FXML private TableColumn<Property, String> colHCodigo, colHDireccion, colHCiudad, colHTipo, colHPrecio;
    @FXML private Button btnFavoritoDesdeHistorial;

    private ObservableList<Visit> activasList;
    private ObservableList<Property> historialList;

    @FXML
    public void initialize() {
        configurarColumnasActivas();
        configurarColumnasHistorial();
        cargarDatos();
        configurarSelecciones();
    }

    private void configurarColumnasActivas() {
        colACodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCode()));
        colAInmueble.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getProperty().getCode() + " — " + d.getValue().getProperty().getAddress()));
        colAFecha.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDate().toString()));
        colAHora.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTime().toString()));
        colAEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getVisitStatus().name()));
        colAAsesor.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getAssignedAdvisor() != null ? d.getValue().getAssignedAdvisor().getName() : "Sin asignar"));
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
        if (client == null) return;

        // 1. Usamos la lista bidireccional que acabamos de implementar
        activasList = FXCollections.observableArrayList();
        for (Visit v : client.getVisitasProgramadas()) {
            // Filtramos solo las que están activas (PENDING o CONFIRM)
            if (v.getVisitStatus() == VisitStatus.PENDING || v.getVisitStatus() == VisitStatus.CONFIRM) {
                activasList.add(v);
            }
        }
        tablaActivas.setItems(activasList);

        // 2. Historial de propiedades visitadas
        historialList = FXCollections.observableArrayList();
        for (Property p : client.getVisitedPropertiesHistory()) {
            historialList.add(p);
        }
        tablaHistorial.setItems(historialList);
    }

    private void configurarSelecciones() {
        btnCancelar.setDisable(true);
        btnFavoritoDesdeHistorial.setDisable(true);
        tablaActivas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> btnCancelar.setDisable(sel == null));
        tablaHistorial.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> btnFavoritoDesdeHistorial.setDisable(sel == null));
    }

    @FXML
    public void cancelarVisita() {
        Visit selected = tablaActivas.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar Visita");
        dialog.setHeaderText("¿Motivo de la cancelación?");
        dialog.showAndWait().ifPresent(motivo -> {
            // El VisitManager se encarga de llamar internamente a client.removeVisita()
            // gracias a la lógica que ajustamos antes.
            AppContext.getInstance().getVisitManager().cancelVisit(selected, motivo);
            activasList.remove(selected); // Actualizamos la vista
            new Alert(Alert.AlertType.INFORMATION, "Visita cancelada.").show();
        });
    }

    @FXML
    public void guardarFavoritoDesdeHistorial() {
        Property selected = tablaHistorial.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        AppContext.getInstance().getClientManager().markAsFavorite(selected);
        new Alert(Alert.AlertType.INFORMATION, "Guardado en favoritos.").show();
    }
}